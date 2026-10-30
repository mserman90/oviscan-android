package com.oviscan.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oviscan.app.ui.theme.DarkCard
import com.oviscan.app.ui.theme.GreenLight

@Composable
fun SensorStatusBadge(
    label: String,
    statusText: String,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(DarkCard)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Glowing status dot
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(if (isActive) GreenLight else Color(0xFF757575))
        )
        Column {
            Text(
                text = label,
                fontSize = 10.sp,
                color = Color(0xFFB0BEC5),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = statusText,
                fontSize = 11.sp,
                color = if (isActive) Color.White else Color(0xFF9E9E9E),
                fontWeight = FontWeight.Medium
            )
        }
    }
}
