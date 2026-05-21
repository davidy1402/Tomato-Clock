package com.example.ui.theme

import androidx.compose.ui.graphics.Color

enum class SereneThemeType(
    val id: String,
    val displayName: String,
    val primary: Color,
    val secondary: Color,
    val backgroundStart: Color,
    val backgroundEnd: Color,
    val textColor: Color,
    val cardBackground: Color,
    val isDark: Boolean
) {
    FOREST_DAWN(
        id = "forest_dawn",
        displayName = "雅致草木",
        primary = Color(0xFF5A5A40),        // Muted Sage/Olive green
        secondary = Color(0xFF8C8A82),      // Sophisticated warm gray
        backgroundStart = Color(0xFFFAF9F6),  // Alabaster / Warm Cream Ivory
        backgroundEnd = Color(0xFFF2EFE7),    // Slightly darker warm cream
        textColor = Color(0xFF2D2D2D),        // Charcoal / Editorial black
        cardBackground = Color(0xFFFFFFFF),   // Pure matte white
        isDark = false
    ),
    WARM_SUNSHINE(
        id = "warm_sunshine",
        displayName = "暖阳诗篇",
        primary = Color(0xFFB56A54),        // Terracotta red-orange (Muted)
        secondary = Color(0xFFCE9E8B),      // Warm soft apricot
        backgroundStart = Color(0xFFFCFAF6),  // Bright eggshell
        backgroundEnd = Color(0xFFF4EDE2),    // Cream sand
        textColor = Color(0xFF33221C),        // Dark deep earth
        cardBackground = Color(0xFFFFFFFF),   // Pure matte white
        isDark = false
    ),
    DEEP_OCEAN(
        id = "deep_ocean",
        displayName = "静谧水墨",
        primary = Color(0xFF4C5866),        // Steel blue slate
        secondary = Color(0xFF8FA3B5),      // Air force misty blue
        backgroundStart = Color(0xFFF7F8FA),  // Cloud white
        backgroundEnd = Color(0xFFECEEF2),    // Cool misty grey
        textColor = Color(0xFF222831),        // Dark obsidian text
        cardBackground = Color(0xFFFFFFFF),   // Pure white card
        isDark = false
    ),
    COSMIC_SLATE(
        id = "cosmic_slate",
        displayName = "至臻黑金",
        primary = Color(0xFFCABFA3),        // Fine Champagne gold
        secondary = Color(0xFF8C8A82),      // Deep matte gray
        backgroundStart = Color(0xFF141512),  // Matte obsidian carbon
        backgroundEnd = Color(0xFF1E1F1B),    // Dark rich slate
        textColor = Color(0xFFECEAE4),        // Off-white book paper color
        cardBackground = Color(0xFF262723),   // Charcoal board card
        isDark = true
    )
}
