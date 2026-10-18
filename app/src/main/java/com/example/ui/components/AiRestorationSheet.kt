package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AiDiagnostic
import com.example.model.PresetsCatalog

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiRestorationSheet(
    aiDiagnostic: AiDiagnostic?,
    isAnalyzing: Boolean,
    isDeepRestoring: Boolean,
    onRunDiagnosis: () -> Unit,
    onApplyAiAdjustments: () -> Unit,
    onRunDeepRestoration: () -> Unit,
    onApplyPresetById: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("ai_restoration_sheet")
    ) {
        // Hero AI Diagnostic Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF132038), Color(0xFF1F1A3A))
                    )
                )
                .border(1.dp, Color(0xFF38E1FF).copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFF38E1FF).copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color(0xFF38E1FF),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Diagnóstico y Reparación IA",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Análisis inteligente con Gemini Vision",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }

                    if (isAnalyzing) {
                        CircularProgressIndicator(
                            color = Color(0xFF38E1FF),
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (aiDiagnostic == null) {
                    Text(
                        text = "Escanea la imagen con IA para detectar grietas, desvanecimiento de color, falta de nitidez y recibir una receta de restauración personalizada.",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onRunDiagnosis,
                        enabled = !isAnalyzing,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF38E1FF),
                            contentColor = Color(0xFF0D0F17)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("run_diagnosis_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAnalyzing) "Analizando fotografía..." else "Iniciar Diagnóstico IA",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    // Show Diagnostic Metrics
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ScoreMetricItem(title = "Conservación", score = aiDiagnostic.scoreCondition, modifier = Modifier.weight(1f))
                        ScoreMetricItem(title = "Nitidez", score = aiDiagnostic.scoreClarity, modifier = Modifier.weight(1f))
                        ScoreMetricItem(title = "Color", score = aiDiagnostic.scoreColor, modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = aiDiagnostic.diagnosisSummary,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        lineHeight = 16.sp
                    )

                    if (aiDiagnostic.defectsDetected.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            aiDiagnostic.defectsDetected.forEach { defect ->
                                Surface(
                                    color = Color(0x33FF5252),
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x66FF5252))
                                ) {
                                    Text(
                                        text = "⚠️ $defect",
                                        fontSize = 10.sp,
                                        color = Color(0xFFFF8A80),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onApplyAiAdjustments,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF38E1FF),
                            contentColor = Color(0xFF0D0F17)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("apply_ai_recommended_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Aplicar Restauración Sugerida",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Specialized AI Restoration Actions
        Text(
            text = "RESTAURACIONES ESPECIALIZADAS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.5f),
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SpecializedActionCard(
                title = "Revivir Foto",
                subtitle = "Antigua / Sepia",
                icon = Icons.Default.Healing,
                accentColor = Color(0xFFFFAB40),
                onClick = { onApplyPresetById("restore_revive") },
                modifier = Modifier.weight(1f)
            )

            SpecializedActionCard(
                title = "Colorizar",
                subtitle = "Blanco & Negro",
                icon = Icons.Default.ColorLens,
                accentColor = Color(0xFFD946EF),
                onClick = { onApplyPresetById("restore_colorize") },
                modifier = Modifier.weight(1f)
            )

            SpecializedActionCard(
                title = "Super Nitidez",
                subtitle = "Foco & Bordes",
                icon = Icons.Default.Face,
                accentColor = Color(0xFF38E1FF),
                onClick = { onApplyPresetById("restore_clarity") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Deep Generative AI Restoration
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF171B2B))
                .border(1.dp, Color(0xFF2B334E), RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🪄", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Restauración Profunda (Gemini 2.5 Flash)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Reconstruye píxeles faltantes y texturas dañadas",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onRunDeepRestoration,
                    enabled = !isDeepRestoring,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF38E1FF)
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38E1FF)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("run_deep_ai_button")
                ) {
                    if (isDeepRestoring) {
                        CircularProgressIndicator(
                            color = Color(0xFF38E1FF),
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Reconstruyendo fotografía con IA...", fontSize = 12.sp)
                    } else {
                        Text(text = "Ejecutar Restauración Profunda IA", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun ScoreMetricItem(
    title: String,
    score: Int,
    modifier: Modifier = Modifier
) {
    val scoreColor = when {
        score >= 75 -> Color(0xFF00E676)
        score >= 50 -> Color(0xFFFFD600)
        else -> Color(0xFFFF5252)
    }

    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$score%",
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            color = scoreColor
        )
        Text(
            text = title,
            fontSize = 10.sp,
            color = Color.White.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { score / 100f },
            color = scoreColor,
            trackColor = Color(0xFF282C3F),
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
        )
    }
}

@Composable
private fun SpecializedActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF161A28))
            .border(1.dp, Color(0xFF262D42), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(10.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(accentColor.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = subtitle,
                fontSize = 9.sp,
                color = Color.White.copy(alpha = 0.5f)
            )
        }
    }
}
