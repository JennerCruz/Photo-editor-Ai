package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PhotoPreset
import com.example.model.PresetCategory
import com.example.model.PresetsCatalog
import kotlin.math.roundToInt

@Composable
fun PresetSelector(
    selectedPreset: PhotoPreset?,
    presetIntensity: Float,
    onSelectPreset: (PhotoPreset?) -> Unit,
    onIntensityChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf(PresetCategory.TODOS) }

    val filteredPresets = remember(selectedCategory) {
        if (selectedCategory == PresetCategory.TODOS) {
            PresetsCatalog.allPresets
        } else {
            PresetsCatalog.allPresets.filter { it.category == selectedCategory }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("preset_selector_container")
    ) {
        // Preset Intensity Slider (visible when a preset is active)
        AnimatedVisibility(
            visible = selectedPreset != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            if (selectedPreset != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Intensidad",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Slider(
                        value = presetIntensity,
                        onValueChange = onIntensityChange,
                        valueRange = 0f..1f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF38E1FF),
                            activeTrackColor = Color(0xFF38E1FF),
                            inactiveTrackColor = Color(0xFF282C3F)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("preset_intensity_slider")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${(presetIntensity * 100).roundToInt()}%",
                        fontSize = 12.sp,
                        color = Color(0xFF38E1FF),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(38.dp)
                    )
                }
            }
        }

        // Category Filter Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(PresetCategory.values()) { cat ->
                val isSelected = cat == selectedCategory
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategory = cat },
                    label = {
                        Text(
                            text = cat.label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF38E1FF),
                        selectedLabelColor = Color(0xFF0D0F17),
                        containerColor = Color(0xFF1E2235),
                        labelColor = Color.White.copy(alpha = 0.8f)
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = if (isSelected) Color(0xFF38E1FF) else Color(0xFF2E344E)
                    ),
                    modifier = Modifier.testTag("filter_chip_${cat.name.lowercase()}")
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Presets Carousel
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // "Sin Preset / Original" Card
            item {
                PresetCard(
                    title = "Original",
                    subtitle = "Sin efecto",
                    iconEmoji = "⚪",
                    isSelected = selectedPreset == null,
                    onClick = { onSelectPreset(null) },
                    modifier = Modifier.testTag("preset_card_none")
                )
            }

            items(filteredPresets, key = { it.id }) { preset ->
                val isSelected = selectedPreset?.id == preset.id
                PresetCard(
                    title = preset.name,
                    subtitle = preset.subtitle,
                    iconEmoji = preset.iconEmoji,
                    isSelected = isSelected,
                    onClick = { onSelectPreset(preset) },
                    modifier = Modifier.testTag("preset_card_${preset.id}")
                )
            }
        }
    }
}

@Composable
fun PresetCard(
    title: String,
    subtitle: String,
    iconEmoji: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) Color(0xFF38E1FF) else Color(0xFF2B3047)
    val backgroundBrush = if (isSelected) {
        Brush.verticalGradient(
            listOf(Color(0xFF1E2D44), Color(0xFF131929))
        )
    } else {
        Brush.verticalGradient(
            listOf(Color(0xFF1B1F2E), Color(0xFF121522))
        )
    }

    Box(
        modifier = modifier
            .width(115.dp)
            .height(110.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(backgroundBrush)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = iconEmoji,
                    fontSize = 24.sp
                )
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .background(Color(0xFF38E1FF), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Seleccionado",
                            tint = Color(0xFF0D0F17),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color(0xFF38E1FF) else Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = subtitle,
                fontSize = 9.sp,
                color = Color.White.copy(alpha = 0.55f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 11.sp
            )
        }
    }
}
