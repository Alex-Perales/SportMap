package com.tunalex.sportmap.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BrandLogo(
    size: Int = 96,
    onPrimaryColor: Color = Color.White,
    showName: Boolean = true,
    nameColor: Color = Color.White
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(size.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.SportsScore,
                contentDescription = "SportMap logo",
                tint = onPrimaryColor,
                modifier = Modifier.size((size * 0.6).dp)
            )
        }
        if (showName) {
            Text(
                text = "SportMap",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = nameColor
            )
        }
    }
}
