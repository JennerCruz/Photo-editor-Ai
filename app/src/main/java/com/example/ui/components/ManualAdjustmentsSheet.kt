package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AdjustmentParameters
import kotlin.math.roundToInt

@Composable
fun ManualAdjustmentsSheet(
    adjustments: AdjustmentParameters,
    onUpdateAdjustment: ((AdjustmentParameters) -> AdjustmentParameters) -> Unit,
    onResetAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .testTag("manual_adjustments_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AJUSTES MANUALES DE PRECISIÓN",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.5f),
                    letterSpacing = 1.sp
                )
                OutlinedButton(
                    onClick = onResetAll,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFFB74D)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB74D).copy(alpha = 0.5f)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("reset_all_adjustments_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.RestartAlt,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Restablecer", fontSize = 11.sp)
                }
            }
        }

        // B&W Toggle Card
        item {
            AdjustmentContainer {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Modo Blanco y Negro (Monocromo)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Text(
                            text = "Convierte a escala de grises de alta fidelidad",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    }
                    Switch(
                        checked = adjustments.isBlackAndWhite,
                        onCheckedChange = { checked ->
                            onUpdateAdjustment { it.copy(isBlackAndWhite = checked) }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF38E1FF),
                            checkedTrackColor = Color(0xFF1E3A5F),
                            uncheckedThumbColor = Color(0xFF6B7280),
                            uncheckedTrackColor = Color(0xFF1F2937)
                        ),
                        modifier = Modifier.testTag("switch_black_and_white")
                    )
                }
            }
        }

        // Section: Luz & Contraste
        item {
            AdjustmentContainer {
                Column {
                    AdjustmentSliderRow(
                        title = "Exposición / Brillo",
                        icon = "☀️",
                        value = adjustments.brightness,
                        min = -100f,
                        max = 100f,
                        onValueChange = { v -> onUpdateAdjustment { it.copy(brightness = v) } },
                        testTag = "slider_brightness"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    AdjustmentSliderRow(
                        title = "Contraste",
                        icon = "◐",
                        value = adjustments.contrast,
                        min = -100f,
                        max = 100f,
                        onValueChange = { v -> onUpdateAdjustment { it.copy(contrast = v) } },
                        testTag = "slider_contrast"
                    )
                }
            }
        }

        // Section: Color & Tono
        item {
            AdjustmentContainer {
                Column {
                    AdjustmentSliderRow(
                        title = "Saturación de Color",
                        icon = "🎨",
                        value = adjustments.saturation,
                        min = -100f,
                        max = 100f,
                        onValueChange = { v -> onUpdateAdjustment { it.copy(saturation = v) } },
                        testTag = "slider_saturation"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    AdjustmentSliderRow(
                        title = "Temperatura (Frío / Cálido)",
                        icon = "🌡️",
                        value = adjustments.temperature,
                        min = -100f,
                        max = 100f,
                        onValueChange = { v -> onUpdateAdjustment { it.copy(temperature = v) } },
                        testTag = "slider_temperature"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    AdjustmentSliderRow(
                        title = "Tinte (Verde / Magenta)",
                        icon = "🟣",
                        value = adjustments.tint,
                        min = -100f,
                        max = 100f,
                        onValueChange = { v -> onUpdateAdjustment { it.copy(tint = v) } },
                        testTag = "slider_tint"
                    )
                }
            }
        }

        // Section: Detalle & Efectos
        item {
            AdjustmentContainer {
                Column {
                    AdjustmentSliderRow(
                        title = "Nitidez (Unsharp Mask)",
                        icon = "🗡️",
                        value = adjustments.sharpness,
                        min = 0f,
                        max = 100f,
                        onValueChange = { v -> onUpdateAdjustment { it.copy(sharpness = v) } },
                        testTag = "slider_sharpness"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    AdjustmentSliderRow(
                        title = "Efecto Viñeta",
                        icon = "🔘",
                        value = adjustments.vignette,
                        min = 0f,
                        max = 100f,
                        onValueChange = { v -> onUpdateAdjustment { it.copy(vignette = v) } },
                        testTag = "slider_vignette"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    AdjustmentSliderRow(
                        title = "Tono Sepia Vintage",
                        icon = "📜",
                        value = adjustments.sepia,
                        min = 0f,
                        max = 100f,
                        onValueChange = { v -> onUpdateAdjustment { it.copy(sepia = v) } },
                        testTag = "slider_sepia"
                    )
                }
            }
        }
    }
}

@Composable
private fun AdjustmentContainer(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF161A28))
            .border(1.dp, Color(0xFF252B3F), RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        content()
    }
}

@Composable
private fun AdjustmentSliderRow(
    title: String,
    icon: String,
    value: Float,
    min: Float,
    max: Float,
    onValueChange: (Float) -> Unit,
    testTag: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = icon, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
            Text(
                text = "${value.roundToInt()}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (value != 0f) Color(0xFF38E1FF) else Color.White.copy(alpha = 0.5f)
            )
        }

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = min..max,
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFF38E1FF),
                activeTrackColor = Color(0xFF38E1FF),
                inactiveTrackColor = Color(0xFF282C3F)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag)
        )
    }
}
