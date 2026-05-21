package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class NoiseType(val displayName: String) {
    NONE("无白噪音"),
    WHITE_NOISE("纯白噪音"),
    RAIN("极简雨声"),
    OCEAN("海浪拍岸"),
    CAMPFIRE("木柴篝火")
}

class NoiseSynthesizer {
    private var audioTrack: AudioTrack? = null
    private var synthJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)
    private var currentType = NoiseType.NONE
    private var volume = 0.5f

    fun start(type: NoiseType) {
        if (type == currentType) return
        stop()
        if (type == NoiseType.NONE) return

        currentType = type
        val sampleRate = 22050 // Optimized sample rate for CPU efficiency
        val minBufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        // Ensure buffer size is large enough to prevent audio stuttering
        val bufferSize = (minBufferSize * 2).coerceAtLeast(4096)

        try {
            audioTrack = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()
            } else {
                @Suppress("DEPRECATION")
                AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize,
                    AudioTrack.MODE_STREAM
                )
            }

            audioTrack?.setVolume(volume)
            audioTrack?.play()

            synthJob = scope.launch {
                val buffer = ShortArray(bufferSize / 2)
                var phase = 0f
                val random = Random(System.currentTimeMillis())

                // Audio DSP Filter properties
                var lastOut = 0f
                var b0 = 0f; var b1 = 0f; var b2 = 0f; var b3 = 0f; var b4 = 0f; var b5 = 0f; var b6 = 0f // Pink noise filter state
                var crackleCd = 0

                while (isActive) {
                    for (i in buffer.indices) {
                        val white = random.nextFloat() * 2f - 1f // [-1, 1] bounds

                        val sample: Float = when (type) {
                            NoiseType.WHITE_NOISE -> {
                                white
                            }
                            NoiseType.RAIN -> {
                                // Rain: Low-pass filtered pink-brownish noise with occasional high-pass rain-patter impulses
                                lastOut = 0.88f * lastOut + 0.12f * white
                                val droplet = if (random.nextFloat() > 0.994f) (random.nextFloat() * 0.35f) else 0f
                                (lastOut * 0.82f + droplet * 0.18f)
                            }
                            NoiseType.OCEAN -> {
                                // Ocean: Soft brown noise slowly modulated by an ultra-low frequency amplitude-modulation (LFO)
                                // Standard brown filter approximation
                                lastOut = 0.94f * lastOut + 0.06f * white
                                val modLfo = 0.45f + 0.45f * kotlin.math.sin(phase)
                                phase += (2f * Math.PI.toFloat() / (sampleRate * 5.5f)) // Waves crash every 5.5s
                                if (phase > 2f * Math.PI) phase -= 2f * Math.PI.toFloat()
                                lastOut * modLfo
                            }
                            NoiseType.CAMPFIRE -> {
                                // Campfire: Deep cozy crackle. Pink/Brown low Rumble background with sparse high-frequency spikes.
                                // Pink noise generator
                                b0 = 0.99886f * b0 + white * 0.0555179f
                                b1 = 0.99332f * b1 + white * 0.0750759f
                                b2 = 0.96900f * b2 + white * 0.1538520f
                                b3 = 0.86650f * b3 + white * 0.3104856f
                                b4 = 0.55000f * b4 + white * 0.5329522f
                                b5 = -0.7616f * b5 - white * 0.0168980f
                                val pink = b0 + b1 + b2 + b3 + b4 + b5 + b6 + white * 0.5362f
                                b6 = white * 0.115926f
                                val rumble = pink * 0.11f // Normalise

                                var snap = 0f
                                if (crackleCd <= 0) {
                                    if (random.nextFloat() > 0.999f) {
                                        // Crack!
                                        snap = (random.nextFloat() * 0.75f + 0.15f)
                                        crackleCd = random.nextInt(sampleRate / 10, sampleRate / 2)
                                    }
                                } else {
                                    crackleCd--
                                    // Decay / flutter the smoke
                                    if (crackleCd in 200..205 || crackleCd in 100..105) {
                                        snap = (random.nextFloat() * 0.25f)
                                    }
                                }
                                (rumble * 0.5f + snap * 0.5f)
                            }
                            else -> 0f
                        }

                        // Short sample formatting
                        val shortVal = (sample * 32767f * volume).toInt().coerceIn(-32768, 32767)
                        buffer[i] = shortVal.toShort()
                    }

                    if (audioTrack?.state == AudioTrack.STATE_INITIALIZED) {
                        try {
                            audioTrack?.write(buffer, 0, buffer.size)
                        } catch (e: Exception) {
                            break
                        }
                    } else {
                        break
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("NoiseSynthesizer", "Error starts audio synthesis thread", e)
        }
    }

    fun setVolume(vol: Float) {
        this.volume = vol.coerceIn(0f, 1f)
        try {
            audioTrack?.setVolume(volume)
        } catch (e: Exception) {
            Log.e("NoiseSynthesizer", "Error setting volume in AudioTrack", e)
        }
    }

    fun stop() {
        currentType = NoiseType.NONE
        synthJob?.cancel()
        synthJob = null
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (e: Exception) {
            // Ignore state exceptions during teardown
        }
        audioTrack = null
    }
}
