package com.example.shaadi.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush

// Reusable primary gradient based on the brand color #D64978 with purple accents
object Gradients {
    // Horizontal gradient for app bars and headers
    val PrimaryHorizontal = Brush.horizontalGradient(
        colors = listOf(
            ShaadiPink,
            ShaadiPurple,
            ShaadiPurpleDeep
        )
    )

    // Diagonal gradient for larger hero sections if needed
    val PrimaryDiagonal = Brush.linearGradient(
        colors = listOf(
            ShaadiPink,
            ShaadiPurple,
            ShaadiPurpleDeep
        ),
        start = Offset.Zero,
        end = Offset(600f, 600f)
    )
}
