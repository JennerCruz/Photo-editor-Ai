package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PerfectionMode
import com.example.model.PresetsCatalog

@Composable
fun PerfectionModesSheet(
    activeMode: PerfectionMode?,
    onApplyMode: (PerfectionMode) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .testTag("perfection_modes_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "CONFIGURACIONES DE PERFECCIÓN PRECONFIGURADAS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.5f),
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }

        items(PresetsCatalog.perfectionModes, key = { it.id }) { mode ->
            val isActive = activeMode?.id == mode.id
            PerfectionModeCard(
                mode = mode,
                isActive = isActive,
                onClick = { onApplyMode(mode) },
                modifier = Modifier.testTag("perfection_card_${mode.id}")
            )
        }
    }
}

@Composable
fun PerfectionModeCard(
    mode: PerfectionMode,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isActive) Color(0xFF38E1FF) else Color(0xFF282D42)
    val backgroundBrush = if (isActive) {
        Brush.horizontalGradient(
            listOf(Color(0xFF1B283D), Color(0xFF141A28))
        )
    } else {
        Brush.horizontalGradient(
            listOf(Color(0xFF161926), Color(0xFF10131F))
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(backgroundBrush)
            .border(
                width = if (isActive) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Circle
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(Color(0xFF23283E), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = mode.iconEmoji, fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = mode.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isActive) Color(0xFF38E1FF) else Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = if (isActive) Color(0xFF38E1FF).copy(alpha = 0.2f) else Color(0xFF262C40),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = mode.tag,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isActive) Color(0xFF38E1FF) else Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = mode.subtitle,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.65f),
                    lineHeight = 15.sp
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Action Indicator
            if (isActive) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color(0xFF38E1FF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Activo",
                        tint = Color(0xFF0D0F17),
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else {
                Surface(
                    color = Color(0xFF22273B),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "Aplicar",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}
