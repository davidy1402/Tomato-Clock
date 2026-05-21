package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "focus_sessions")
data class FocusSession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val durationMinutes: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val sessionType: String,      // "Work" / "Short Break" / "Long Break"
    val whiteNoiseUsed: String,   // "None" / "White Noise" / "Rain" / "Ocean" / "Campfire"
    val themeUsed: String,        // "Forest Dawn" / "Warm Sunshine" / "Deep Ocean" / "Cosmic Slate"
    val completed: Boolean = true
)
