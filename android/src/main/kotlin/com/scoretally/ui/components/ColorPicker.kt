package com.scoretally.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val DefaultPlayerColors = listOf(
    "#6750A4",
    "#1976D2",
    "#388E3C",
    "#D32F2F",
    "#F57C00",
    "#C2185B",
    "#7B1FA2",
    "#0097A7"
)

fun String.toComposeColor(): Color {
    return try {
        Color(android.graphics.Color.parseColor(this))
    } catch (e: IllegalArgumentException) {
        Color(0xFF6750A4)
    }
}

@Composable
fun ColorPicker(
    selectedColor: String,
    onColorSelected: (String) -> Unit,
    enabled: Boolean = true,
    colors: List<String> = DefaultPlayerColors
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        colors.chunked(4).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { colorHex ->
                    ColorCircle(
                        colorHex = colorHex,
                        isSelected = selectedColor == colorHex,
                        onClick = { onColorSelected(colorHex) },
                        enabled = enabled,
                        modifier = Modifier.weight(1f)
                    )
                }
                repeat(4 - row.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun ColorCircle(
    colorHex: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val color = colorHex.toComposeColor()

    Column(
        modifier = modifier.clickable(enabled = enabled, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(if (isSelected) 56.dp else 48.dp)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}
