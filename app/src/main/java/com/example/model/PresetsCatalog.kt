package com.example.model

object PresetsCatalog {

    val allPresets: List<PhotoPreset> = listOf(
        // Restauración
        PhotoPreset(
            id = "restore_revive",
            name = "Revivir Antiguo",
            subtitle = "Contraste + Nitidez + Balance",
            category = PresetCategory.RESTAURACION,
            iconEmoji = "✨",
            adjustments = AdjustmentParameters(
                brightness = 8f,
                contrast = 24f,
                saturation = 18f,
                temperature = 5f,
                sharpness = 45f,
                vignette = 0f,
                sepia = 0f,
                highlights = 15f,
                shadows = 20f
            )
        ),
        PhotoPreset(
            id = "restore_clarity",
            name = "Super Nitidez Pro",
            subtitle = "Recuperación de detalle y micro-textura",
            category = PresetCategory.RESTAURACION,
            iconEmoji = "🔍",
            adjustments = AdjustmentParameters(
                brightness = 4f,
                contrast = 28f,
                saturation = 6f,
                sharpness = 75f,
                vignette = 5f,
                highlights = 10f,
                shadows = 15f
            )
        ),
        PhotoPreset(
            id = "restore_scratch_fade",
            name = "Filtro Suavizado",
            subtitle = "Disimula rayones y ruido visual",
            category = PresetCategory.RESTAURACION,
            iconEmoji = "🩹",
            adjustments = AdjustmentParameters(
                brightness = 10f,
                contrast = 15f,
                saturation = 12f,
                temperature = 8f,
                sharpness = 20f,
                vignette = 0f
            )
        ),
        PhotoPreset(
            id = "restore_colorize",
            name = "Colorizar Vivo",
            subtitle = "Inyecta color a fotos descoloridas",
            category = PresetCategory.RESTAURACION,
            iconEmoji = "🎨",
            adjustments = AdjustmentParameters(
                brightness = 6f,
                contrast = 22f,
                saturation = 45f,
                temperature = 14f,
                tint = 6f,
                sharpness = 35f
            )
        ),

        // Retrato
        PhotoPreset(
            id = "portrait_porcelain",
            name = "Piel de Porcelana",
            subtitle = "Luz suave y tono cálido embellecedor",
            category = PresetCategory.RETRATO,
            iconEmoji = "🌸",
            adjustments = AdjustmentParameters(
                brightness = 14f,
                contrast = 8f,
                saturation = 8f,
                temperature = 10f,
                tint = 8f,
                sharpness = 18f,
                vignette = 12f
            )
        ),
        PhotoPreset(
            id = "portrait_golden_hour",
            name = "Atardecer Dorado",
            subtitle = "Calidez radiante de sol poniente",
            category = PresetCategory.RETRATO,
            iconEmoji = "🌅",
            adjustments = AdjustmentParameters(
                brightness = 6f,
                contrast = 20f,
                saturation = 28f,
                temperature = 36f,
                tint = 12f,
                sharpness = 30f,
                vignette = 16f
            )
        ),
        PhotoPreset(
            id = "portrait_editorial",
            name = "Estudio Editorial",
            subtitle = "Iluminación de revista de alta costura",
            category = PresetCategory.RETRATO,
            iconEmoji = "💎",
            adjustments = AdjustmentParameters(
                brightness = 4f,
                contrast = 32f,
                saturation = 14f,
                temperature = -4f,
                sharpness = 55f,
                vignette = 18f
            )
        ),
        PhotoPreset(
            id = "portrait_bw_glam",
            name = "Glamour B&W",
            subtitle = "Monocromático dramático con sombras profundas",
            category = PresetCategory.RETRATO,
            iconEmoji = "🎭",
            adjustments = AdjustmentParameters(
                brightness = 5f,
                contrast = 45f,
                sharpness = 40f,
                vignette = 28f,
                isBlackAndWhite = true
            )
        ),

        // Cinemático
        PhotoPreset(
            id = "cinema_teal_orange",
            name = "Teal & Orange",
            subtitle = "Look taquillero de cine de Hollywood",
            category = PresetCategory.CINEMATICO,
            iconEmoji = "🎬",
            adjustments = AdjustmentParameters(
                brightness = -2f,
                contrast = 35f,
                saturation = 25f,
                temperature = 15f,
                tint = -12f,
                sharpness = 40f,
                vignette = 25f
            )
        ),
        PhotoPreset(
            id = "cinema_cyberpunk",
            name = "Cyberpunk Neón",
            subtitle = "Magente y cian de noche futurista",
            category = PresetCategory.CINEMATICO,
            iconEmoji = "🌆",
            adjustments = AdjustmentParameters(
                brightness = -6f,
                contrast = 40f,
                saturation = 55f,
                temperature = -28f,
                tint = 35f,
                sharpness = 45f,
                vignette = 30f
            )
        ),
        PhotoPreset(
            id = "cinema_moody",
            name = "Moody Forest",
            subtitle = "Tonos verdes profundos y sombras oscuras",
            category = PresetCategory.CINEMATICO,
            iconEmoji = "🌲",
            adjustments = AdjustmentParameters(
                brightness = -10f,
                contrast = 28f,
                saturation = -15f,
                temperature = -10f,
                tint = -18f,
                sharpness = 35f,
                vignette = 35f
            )
        ),

        // Vintage
        PhotoPreset(
            id = "vintage_film_35mm",
            name = "Película 35mm",
            subtitle = "Textura analógica y negros atenuados",
            category = PresetCategory.VINTAGE,
            iconEmoji = "🎞️",
            adjustments = AdjustmentParameters(
                brightness = 8f,
                contrast = -10f,
                saturation = -12f,
                temperature = 18f,
                tint = 4f,
                sharpness = 22f,
                vignette = 22f,
                sepia = 18f
            )
        ),
        PhotoPreset(
            id = "vintage_polaroid",
            name = "Polaroid 70s",
            subtitle = "Look nostálgico de cámara instantánea",
            category = PresetCategory.VINTAGE,
            iconEmoji = "📸",
            adjustments = AdjustmentParameters(
                brightness = 15f,
                contrast = -8f,
                saturation = 22f,
                temperature = 25f,
                tint = -8f,
                sharpness = 15f,
                vignette = 30f,
                sepia = 24f
            )
        ),
        PhotoPreset(
            id = "vintage_sepia_classic",
            name = "Sepia Imperial",
            subtitle = "Elegante tono sepia de época",
            category = PresetCategory.VINTAGE,
            iconEmoji = "📜",
            adjustments = AdjustmentParameters(
                brightness = 4f,
                contrast = 20f,
                saturation = -20f,
                temperature = 30f,
                sharpness = 30f,
                vignette = 35f,
                sepia = 80f
            )
        ),

        // Creativo
        PhotoPreset(
            id = "creative_hdr",
            name = "HDR Ultra Vívido",
            subtitle = "Máximo rango dinámico y colores vibrantes",
            category = PresetCategory.CREATIVO,
            iconEmoji = "🌈",
            adjustments = AdjustmentParameters(
                brightness = 10f,
                contrast = 38f,
                saturation = 48f,
                sharpness = 60f,
                vignette = 10f,
                highlights = 25f,
                shadows = 35f
            )
        ),
        PhotoPreset(
            id = "creative_minimal_cold",
            name = "Minimal Nórdico",
            subtitle = "Estilo frío, limpio y desaturado",
            category = PresetCategory.CREATIVO,
            iconEmoji = "❄️",
            adjustments = AdjustmentParameters(
                brightness = 12f,
                contrast = 18f,
                saturation = -35f,
                temperature = -30f,
                tint = 5f,
                sharpness = 45f,
                vignette = 8f
            )
        ),
        PhotoPreset(
            id = "creative_pastel",
            name = "Pastel Dream",
            subtitle = "Suavidad etérea y sombras luminosas",
            category = PresetCategory.CREATIVO,
            iconEmoji = "🧁",
            adjustments = AdjustmentParameters(
                brightness = 20f,
                contrast = -15f,
                saturation = 15f,
                temperature = 8f,
                tint = 14f,
                sharpness = 10f,
                vignette = 5f
            )
        )
    )

    val perfectionModes: List<PerfectionMode> = listOf(
        PerfectionMode(
            id = "perf_restoration",
            title = "Restaurar Foto Antigua",
            subtitle = "Elimina tinte amarillo, realza contraste y nitidez en fotos viejas",
            tag = "HISTÓRICA",
            iconEmoji = "🏛️",
            adjustments = AdjustmentParameters(
                brightness = 12f,
                contrast = 30f,
                saturation = 20f,
                temperature = -8f,
                sharpness = 65f,
                highlights = 20f,
                shadows = 25f
            )
        ),
        PerfectionMode(
            id = "perf_portrait_glow",
            title = "Rostro & Piel Radiante",
            subtitle = "Tonos de piel naturales, brillo en ojos y balance cálido",
            tag = "BELLEZA",
            iconEmoji = "✨",
            adjustments = AdjustmentParameters(
                brightness = 14f,
                contrast = 12f,
                saturation = 10f,
                temperature = 12f,
                tint = 6f,
                sharpness = 25f,
                vignette = 14f
            )
        ),
        PerfectionMode(
            id = "perf_clarity",
            title = "Ultra Nitidez & Textura",
            subtitle = "Enfoca bordes difusos y saca a relucir micro-detalles perdidos",
            tag = "DETALLE",
            iconEmoji = "🔬",
            adjustments = AdjustmentParameters(
                brightness = 5f,
                contrast = 26f,
                sharpness = 85f,
                highlights = 15f,
                shadows = 15f
            )
        ),
        PerfectionMode(
            id = "perf_hdr",
            title = "Rango Dinámico HDR",
            subtitle = "Recupera zonas sobreexpuestas y levanta sombras oscuras",
            tag = "DINÁMICO",
            iconEmoji = "⚡",
            adjustments = AdjustmentParameters(
                brightness = 8f,
                contrast = 35f,
                saturation = 30f,
                sharpness = 50f,
                highlights = 30f,
                shadows = 40f
            )
        ),
        PerfectionMode(
            id = "perf_white_balance",
            title = "Balance de Luz & Color",
            subtitle = "Neutraliza dominantes de color y equilibra la exposición",
            tag = "PRO",
            iconEmoji = "⚖️",
            adjustments = AdjustmentParameters(
                brightness = 6f,
                contrast = 18f,
                saturation = 15f,
                temperature = 0f,
                tint = 0f,
                sharpness = 30f
            )
        )
    )
}
