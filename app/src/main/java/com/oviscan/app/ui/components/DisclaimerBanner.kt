package com.oviscan.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oviscan.app.ui.theme.AmberWarning

@Composable
fun DisclaimerBanner(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF261D11))
            .border(1.dp, AmberWarning.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Uyarı",
                tint = AmberWarning,
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(
                    text = "YASAL VETERİNER HEKİM UYARISI",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = AmberWarning,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Bu uygulama bir veteriner hekim teşhisi DEĞİLDİR; ultrason ve kan testi gerektirmeyen bir ön tarama ve karar destek aracıdır. Kesin tanı ve yasal işlemler için mutlaka veteriner hekiminize başvurunuz.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFFE0E0E0),
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                )
            }
        }
    }
}
