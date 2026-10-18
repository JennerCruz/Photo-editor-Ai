package com.example.model

import androidx.compose.ui.graphics.vector.ImageVector

enum class PresetCategory(val label: String) {
    TODOS("Todos"),
    RESTAURACION("Restauración"),
    RETRATO("Retrato"),
    CINEMATICO("Cinemático"),
    VINTAGE("Vintage"),
    CREATIVO("Creativo")
}

data class AdjustmentParameters(
    val brightness: Float = 0f,      // -100 to 100 (0 = normal)
    val contrast: Float = 0f,        // -100 to 100 (0 = normal)
    val saturation: Float = 0f,      // -100 to 100 (0 = normal)
    val temperature: Float = 0f,     // -100 to 100 (cool to warm)
    val tint: Float = 0f,            // -100 to 100 (green to magenta)
    val sharpness: Float = 0f,       // 0 to 100
    val vignette: Float = 0f,        // 0 to 100
    val sepia: Float = 0f,           // 0 to 100
    val isBlackAndWhite: Boolean = false,
    val highlights: Float = 0f,      // -100 to 100
    val shadows: Float = 0f          // -100 to 100
) {
    val isDefault: Boolean
        get() = brightness == 0f && contrast == 0f && saturation == 0f &&
                temperature == 0f && tint == 0f && sharpness == 0f &&
                vignette == 0f && sepia == 0f && !isBlackAndWhite &&
                highlights == 0f && shadows == 0f

    fun lerpWith(target: AdjustmentParameters, fraction: Float): AdjustmentParameters {
        return AdjustmentParameters(
            brightness = this.brightness + (target.brightness - this.brightness) * fraction,
            contrast = this.contrast + (target.contrast - this.contrast) * fraction,
            saturation = this.saturation + (target.saturation - this.saturation) * fraction,
            temperature = this.temperature + (target.temperature - this.temperature) * fraction,
            tint = this.tint + (target.tint - this.tint) * fraction,
            sharpness = this.sharpness + (target.sharpness - this.sharpness) * fraction,
            vignette = this.vignette + (target.vignette - this.vignette) * fraction,
            sepia = this.sepia + (target.sepia - this.sepia) * fraction,
            isBlackAndWhite = if (fraction > 0.5f) target.isBlackAndWhite else this.isBlackAndWhite,
            highlights = this.highlights + (target.highlights - this.highlights) * fraction,
            shadows = this.shadows + (target.shadows - this.shadows) * fraction
        )
    }
}

data class PhotoPreset(
    val id: String,
    val name: String,
    val subtitle: String,
    val category: PresetCategory,
    val iconEmoji: String,
    val adjustments: AdjustmentParameters
)

data class PerfectionMode(
    val id: String,
    val title: String,
    val subtitle: String,
    val tag: String,
    val iconEmoji: String,
    val adjustments: AdjustmentParameters
)

data class AiDiagnostic(
    val scoreCondition: Int,    // 0 to 100
    val scoreClarity: Int,      // 0 to 100
    val scoreColor: Int,        // 0 to 100
    val defectsDetected: List<String>,
    val diagnosisSummary: String,
    val recommendedActions: List<String>,
    val recommendedAdjustments: AdjustmentParameters
)
