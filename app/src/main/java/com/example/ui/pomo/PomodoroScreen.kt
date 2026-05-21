package com.example.ui.pomo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.NoiseType
import com.example.data.FocusSession
import com.example.ui.theme.SereneThemeType
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PomodoroScreen(
    viewModel: PomodoroViewModel,
    modifier: Modifier = Modifier
) {
    val theme by viewModel.currentTheme.collectAsState()
    val mode by viewModel.currentMode.collectAsState()
    val timerMinutes by viewModel.timerMinutes.collectAsState()
    val timeLeftSeconds by viewModel.timeLeftSeconds.collectAsState()
    val isRunning by viewModel.isTimerRunning.collectAsState()
    val selectedNoise by viewModel.currentNoise.collectAsState()
    val volume by viewModel.volume.collectAsState()

    val history by viewModel.historySessions.collectAsState()
    val totalMinutes by viewModel.totalMinutesFocused.collectAsState()
    val completedCount by viewModel.completedCount.collectAsState()

    var showTimeAdjuster by remember { mutableStateOf(false) }

    // Editorial Aesthetic color transitions
    val animatedBgStart by animateColorAsState(targetValue = theme.backgroundStart, animationSpec = tween(600))
    val animatedBgEnd by animateColorAsState(targetValue = theme.backgroundEnd, animationSpec = tween(600))
    val animatedPrimary by animateColorAsState(targetValue = theme.primary, animationSpec = tween(600))
    val animatedSecondary by animateColorAsState(targetValue = theme.secondary, animationSpec = tween(600))
    val animatedText by animateColorAsState(targetValue = theme.textColor, animationSpec = tween(600))
    val animatedCardBg by animateColorAsState(targetValue = theme.cardBackground, animationSpec = tween(600))

    // Delicate border color matching general Editorial bone structures
    val animatedBorderColor by animateColorAsState(
        targetValue = if (theme.isDark) animatedText.copy(alpha = 0.15f) else Color(0xFFE5E2D9),
        animationSpec = tween(600)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(animatedBgStart, animatedBgEnd)
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Elegant Editorial Header Navigation Row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: History indicator badge
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(animatedCardBg)
                            .border(1.dp, animatedBorderColor, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.History,
                            contentDescription = "历史统计",
                            tint = animatedPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Center: High-Fashion Spaced metadata title
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "FOCUS MODE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = animatedSecondary,
                            letterSpacing = 2.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(animatedPrimary)
                        )
                    }

                    // Right: Quick toggle duration slider
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (showTimeAdjuster) animatedPrimary else animatedCardBg)
                            .border(1.dp, animatedBorderColor, RoundedCornerShape(16.dp))
                            .clickable { showTimeAdjuster = !showTimeAdjuster },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.SettingsInputComposite,
                            contentDescription = "调整时长",
                            tint = if (showTimeAdjuster) (if (theme.isDark) Color.Black else Color.White) else animatedPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Beautiful Large Font Title Block
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "番茄钟与自然治愈",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily.Serif,
                        color = animatedText,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "The Silent Healing Spaces",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = animatedSecondary,
                        letterSpacing = 2.sp
                    )
                }
            }

            // Mode Selector Pill Card style
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(animatedCardBg)
                        .border(1.dp, animatedBorderColor, RoundedCornerShape(20.dp))
                        .padding(5.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TimerMode.values().forEach { timerMode ->
                        val isSelected = mode == timerMode
                        val buttonColor by animateColorAsState(
                            targetValue = if (isSelected) animatedPrimary else Color.Transparent,
                            animationSpec = tween(300)
                        )
                        val contentColor by animateColorAsState(
                            targetValue = if (isSelected) {
                                if (theme.isDark) Color.Black else Color.White
                            } else animatedText.copy(alpha = 0.6f),
                            animationSpec = tween(300)
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clip(RoundedCornerShape(15.dp))
                                .background(buttonColor)
                                .clickable {
                                    viewModel.setTimerMode(timerMode)
                                }
                                .testTag("mode_tab_${timerMode.name.lowercase()}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = timerMode.displayName,
                                color = contentColor,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Expanded Creative Display Clock Block (Editorial Typography, High Contrast)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Ambient light circular border track (very delicate and minimal)
                    val totalDurationSeconds = timerMinutes * 60f
                    val progressRatio = if (totalDurationSeconds > 0) {
                        timeLeftSeconds / totalDurationSeconds
                    } else 0f

                    val animatedProgress by animateFloatAsState(
                        targetValue = progressRatio,
                        animationSpec = tween(1000)
                    )

                    Box(
                        modifier = Modifier.size(240.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            // High-end elegant double rings
                            drawCircle(
                                color = animatedPrimary.copy(alpha = 0.08f),
                                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                            )
                            drawArc(
                                color = animatedPrimary,
                                startAngle = -90f,
                                sweepAngle = 360f * animatedProgress,
                                useCenter = false,
                                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }

                        // Massive Serif Font Timer display
                        val rawTime = formatTime(timeLeftSeconds)
                        val colonIndex = rawTime.indexOf(":")
                        val minsPart = if (colonIndex != -1) rawTime.substring(0, colonIndex) else "25"
                        val secsPart = if (colonIndex != -1) rawTime.substring(colonIndex + 1) else "00"

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = minsPart,
                                fontSize = 56.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif,
                                color = animatedText,
                                letterSpacing = (-2).sp
                            )
                            Text(
                                text = ":",
                                fontSize = 56.sp,
                                fontWeight = FontWeight.Light,
                                fontFamily = FontFamily.Serif,
                                color = animatedText.copy(alpha = 0.3f),
                                modifier = Modifier.padding(horizontal = 2.dp)
                            )
                            Text(
                                text = secsPart,
                                fontSize = 56.sp,
                                fontWeight = FontWeight.Normal,
                                fontFamily = FontFamily.Serif,
                                color = animatedText,
                                letterSpacing = (-2).sp
                            )
                        }
                    }

                    // High-Fashion Rotated "HEALING / ACTIVE" Tag on the Top-Right of the clock frame
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = (-20).dp, y = (10).dp)
                            .graphicsLayer(rotationZ = 12f)
                            .background(animatedPrimary, RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isRunning) "ACTIVE" else "HEALING",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (theme.isDark) Color.Black else Color.White,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            // Editorial Quote Style Left-Border Status Banner
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .border(
                            BorderStroke(0.dp, Color.Transparent) // placeholder
                        )
                ) {
                    // Left vertical colored stroke line
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height(54.dp)
                            .clip(RoundedCornerShape(1.dp))
                            .background(animatedPrimary)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    // Text blocks
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (selectedNoise == NoiseType.NONE) "自然物语" else selectedNoise.displayName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal,
                            fontStyle = FontStyle.Italic,
                            fontFamily = FontFamily.Serif,
                            color = animatedPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "当前时段 · " + mode.displayName + " (建议保持全屏专注)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = animatedSecondary,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            // Custom Time Controller Slider (Expandable)
            item {
                AnimatedVisibility(visible = showTimeAdjuster) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = animatedCardBg),
                        border = BorderStroke(1.dp, animatedBorderColor)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "拖动滑块自定义专注或休息时长",
                                fontSize = 12.sp,
                                color = animatedText.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Slider(
                                value = timerMinutes.toFloat(),
                                onValueChange = { viewModel.setCustomMinutes(it.toInt()) },
                                valueRange = 1f..60f,
                                steps = 59,
                                modifier = Modifier.fillMaxWidth().testTag("duration_slider"),
                                colors = SliderDefaults.colors(
                                    thumbColor = animatedPrimary,
                                    activeTrackColor = animatedPrimary,
                                    inactiveTrackColor = animatedPrimary.copy(alpha = 0.15f)
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "设定时间: ${timerMinutes} 分钟",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = animatedText,
                                fontFamily = FontFamily.Serif
                            )
                        }
                    }
                }
            }

            // Playback Actions control layout with Editorial flair
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Reset Button (Thin rounded outline block)
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(animatedCardBg)
                            .border(1.dp, animatedBorderColor, RoundedCornerShape(18.dp))
                            .clickable { viewModel.resetTimer() }
                            .testTag("reset_timer_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "重置计时器",
                            tint = animatedPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Main Start/Pause Action Button (Wide, clean, and bold charcoal style button)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(58.dp)
                            .padding(horizontal = 14.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (theme.isDark) animatedPrimary else Color(0xFF2D2D2D))
                            .clickable {
                                if (isRunning) viewModel.pauseTimer() else viewModel.startTimer()
                            }
                            .testTag("toggle_timer_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isRunning) "暂停" else "开始",
                                tint = if (theme.isDark) Color.Black else Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isRunning) "PAUSE FOCUS" else "START FOCUS",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (theme.isDark) Color.Black else Color.White,
                                letterSpacing = 2.sp
                            )
                        }
                    }

                    // Skip Button (Thin outline rounded block)
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(animatedCardBg)
                            .border(1.dp, animatedBorderColor, RoundedCornerShape(18.dp))
                            .clickable {
                                if (mode == TimerMode.WORK) {
                                    viewModel.setTimerMode(TimerMode.SHORT_BREAK)
                                } else if (mode == TimerMode.SHORT_BREAK) {
                                    viewModel.setTimerMode(TimerMode.LONG_BREAK)
                                } else {
                                    viewModel.setTimerMode(TimerMode.WORK)
                                }
                            }
                            .testTag("skip_timer_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "跳过",
                            tint = animatedPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // Dynamic Healing White Noise Panel
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("noise_container"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = animatedCardBg),
                    border = BorderStroke(1.dp, animatedBorderColor)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.MusicNote,
                                contentDescription = null,
                                tint = animatedPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "治愈系自研白噪音",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif,
                                color = animatedText
                            )
                        }

                        // Grid Flow of sound selectors styled like high-fashion chips
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            maxItemsInEachRow = 2
                        ) {
                            NoiseType.values().forEach { noiseType ->
                                val isSelected = selectedNoise == noiseType
                                val cardWeightModifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 4.dp)

                                val itemBgColor by animateColorAsState(
                                    targetValue = if (isSelected) animatedPrimary else Color.Transparent,
                                    animationSpec = tween(300)
                                )

                                val itemContentColor by animateColorAsState(
                                    targetValue = if (isSelected) {
                                        if (theme.isDark) Color.Black else Color.White
                                    } else animatedText.copy(alpha = 0.75f),
                                    animationSpec = tween(300)
                                )

                                val itemBorderStroke = if (isSelected) {
                                    BorderStroke(1.dp, animatedPrimary)
                                } else {
                                    BorderStroke(1.dp, animatedBorderColor)
                                }

                                Row(
                                    modifier = cardWeightModifier
                                        .clip(RoundedCornerShape(24.dp))
                                        .background(itemBgColor)
                                        .border(itemBorderStroke, RoundedCornerShape(24.dp))
                                        .clickable { viewModel.toggleWhiteNoise(noiseType) }
                                        .padding(horizontal = 14.dp, vertical = 11.dp)
                                        .testTag("noise_item_${noiseType.name.lowercase()}"),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = getNoiseIcon(noiseType),
                                        contentDescription = null,
                                        tint = if (isSelected) (if (theme.isDark) Color.Black else Color.White) else animatedPrimary,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = noiseType.displayName,
                                        color = itemContentColor,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            }
                        }

                        // High End Volume Slider
                        AnimatedVisibility(visible = selectedNoise != NoiseType.NONE) {
                            Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.VolumeUp,
                                            contentDescription = null,
                                            tint = animatedText.copy(alpha = 0.5f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "白噪音音量调制",
                                            fontSize = 11.sp,
                                            color = animatedText.copy(alpha = 0.6f)
                                        )
                                    }
                                    Text(
                                        text = "${(volume * 100).toInt()}%",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = animatedPrimary,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Slider(
                                    value = volume,
                                    onValueChange = { viewModel.setNoiseVolume(it) },
                                    modifier = Modifier.fillMaxWidth().testTag("volume_slider"),
                                    colors = SliderDefaults.colors(
                                        thumbColor = animatedPrimary,
                                        activeTrackColor = animatedPrimary,
                                        inactiveTrackColor = animatedPrimary.copy(alpha = 0.15f)
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Healing Themes Swapper Block
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("themes_container"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = animatedCardBg),
                    border = BorderStroke(1.dp, animatedBorderColor)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Palette,
                                contentDescription = null,
                                tint = animatedPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "治愈系空间主题",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif,
                                color = animatedText
                            )
                        }

                        // Row of Spheres
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            SereneThemeType.values().forEach { themeType ->
                                val isActive = theme == themeType

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clickable { viewModel.selectTheme(themeType) }
                                        .testTag("theme_picker_${themeType.id}")
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.radialGradient(
                                                    colors = listOf(themeType.primary, themeType.backgroundEnd)
                                                )
                                            )
                                            .border(
                                                width = if (isActive) 3.dp else 1.dp,
                                                color = if (isActive) themeType.primary else animatedBorderColor,
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isActive) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "已选择",
                                                tint = if (themeType.isDark) Color.Black else Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = themeType.displayName,
                                        fontSize = 11.sp,
                                        color = animatedText,
                                        fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Statistics Dashboard
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Stat Card Left
                    Card(
                        modifier = Modifier.weight(1f).testTag("minutes_spent_card"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = animatedCardBg),
                        border = BorderStroke(1.dp, animatedBorderColor)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Timer,
                                contentDescription = null,
                                tint = animatedPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "专注总时长",
                                fontSize = 11.sp,
                                color = animatedText.copy(alpha = 0.5f),
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "${totalMinutes ?: 0} min",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = animatedText,
                                fontFamily = FontFamily.Serif
                            )
                        }
                    }

                    // Stat Card Right
                    Card(
                        modifier = Modifier.weight(1f).testTag("sessions_completed_card"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = animatedCardBg),
                        border = BorderStroke(1.dp, animatedBorderColor)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.SelfImprovement,
                                contentDescription = null,
                                tint = animatedPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "已达标番茄数",
                                fontSize = 11.sp,
                                color = animatedText.copy(alpha = 0.5f),
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "$completedCount 次完成",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = animatedText,
                                fontFamily = FontFamily.Serif
                            )
                        }
                    }
                }
            }

            // Focus Logs Title
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.History,
                            contentDescription = null,
                            tint = animatedPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "专注历史记录",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            color = animatedText
                        )
                    }

                    if (history.isNotEmpty()) {
                        Text(
                            text = "清空",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = animatedPrimary,
                            modifier = Modifier
                                .clickable { viewModel.clearHistory() }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("clear_history_text")
                        )
                    }
                }
            }

            // Focus Items (History Items)
            if (history.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.SpaceDashboard,
                            contentDescription = "暂无记录",
                            tint = animatedText.copy(alpha = 0.15f),
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "纸上诗篇，等待开启专注...",
                            fontSize = 13.sp,
                            color = animatedText.copy(alpha = 0.4f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                items(history, key = { it.id }) { session ->
                    HistoryItem(
                        session = session,
                        cardBg = animatedCardBg,
                        primaryTint = animatedPrimary,
                        borderColor = animatedBorderColor,
                        textCol = animatedText,
                        onDeleteClick = { viewModel.deleteHistoryItem(session.id) }
                    )
                }
            }
        }
    }
}

// History Helper Item
@Composable
fun HistoryItem(
    session: FocusSession,
    cardBg: Color,
    primaryTint: Color,
    borderColor: Color,
    textCol: Color,
    onDeleteClick: () -> Unit
) {
    val formatter = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }
    val dateStr = formatter.format(Date(session.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("history_item_${session.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(primaryTint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (session.sessionType) {
                        "短休一下", "长休放松" -> Icons.Outlined.SelfImprovement
                        else -> Icons.Outlined.LocalActivity
                    },
                    contentDescription = null,
                    tint = primaryTint,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = session.sessionType,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = textCol
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${session.durationMinutes}分钟",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Serif,
                        color = textCol.copy(alpha = 0.6f)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "使用: " + session.whiteNoiseUsed + " ✕ " + session.themeUsed + "空间",
                    fontSize = 11.sp,
                    color = textCol.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = dateStr,
                    fontSize = 10.sp,
                    color = textCol.copy(alpha = 0.4f)
                )
            }

            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.testTag("delete_item_button_${session.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "删除记录",
                    tint = textCol.copy(alpha = 0.35f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// Clock Text Display Helper Formatting
private fun formatTime(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return String.format("%02d:%02d", m, s)
}

// Noise Icon Helper
private fun getNoiseIcon(type: NoiseType): androidx.compose.ui.graphics.vector.ImageVector {
    return when (type) {
        NoiseType.NONE -> Icons.Outlined.VolumeOff
        NoiseType.WHITE_NOISE -> Icons.Outlined.Air
        NoiseType.RAIN -> Icons.Outlined.WaterDrop
        NoiseType.OCEAN -> Icons.Outlined.Waves
        NoiseType.CAMPFIRE -> Icons.Outlined.LocalFireDepartment
    }
}
