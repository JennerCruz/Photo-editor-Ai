package com.example.processing

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RadialGradient
import android.graphics.Shader
import com.example.model.AdjustmentParameters
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

object ImageProcessor {

    /**
     * Applies the given adjustments to the source bitmap and returns a newly rendered Bitmap.
     */
    fun processBitmap(source: Bitmap, adjustments: AdjustmentParameters): Bitmap {
        if (adjustments.isDefault) {
            return source.copy(Bitmap.Config.ARGB_8888, true)
        }

        val width = source.width
        val height = source.height
        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        // 1. Compose ColorMatrix for Brightness, Contrast, Saturation, Temperature, Tint, Sepia, B&W
        val colorMatrix = createAdjustedColorMatrix(adjustments)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
            colorFilter = ColorMatrixColorFilter(colorMatrix)
        }

        canvas.drawBitmap(source, 0f, 0f, paint)

        // 2. Apply Vignette if specified
        if (adjustments.vignette > 0f) {
            applyVignette(canvas, width, height, adjustments.vignette / 100f)
        }

        // 3. Apply Sharpness if specified
        return if (adjustments.sharpness > 5f) {
            applySharpen(output, adjustments.sharpness / 100f)
        } else {
            output
        }
    }

    private fun createAdjustedColorMatrix(adj: AdjustmentParameters): ColorMatrix {
        val result = ColorMatrix()

        // Saturation (-100 to 100 => 0.0f to 2.0f, normal = 1.0f)
        if (adj.isBlackAndWhite) {
            result.setSaturation(0f)
        } else {
            val satFactor = (adj.saturation + 100f) / 100f
            result.setSaturation(max(0f, satFactor))
        }

        // Contrast (-100 to 100 => 0.0f to 2.5f, normal = 1.0f)
        if (adj.contrast != 0f) {
            val scale = if (adj.contrast > 0f) {
                1f + (adj.contrast / 100f) * 1.5f
            } else {
                1f + (adj.contrast / 100f) * 0.8f
            }
            val translate = (-0.5f * scale + 0.5f) * 255f
            val contrastMatrix = ColorMatrix(
                floatArrayOf(
                    scale, 0f, 0f, 0f, translate,
                    0f, scale, 0f, 0f, translate,
                    0f, 0f, scale, 0f, translate,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            result.postConcat(contrastMatrix)
        }

        // Brightness (-100 to 100 => -128 to +128)
        if (adj.brightness != 0f) {
            val b = (adj.brightness / 100f) * 128f
            val brightnessMatrix = ColorMatrix(
                floatArrayOf(
                    1f, 0f, 0f, 0f, b,
                    0f, 1f, 0f, 0f, b,
                    0f, 0f, 1f, 0f, b,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            result.postConcat(brightnessMatrix)
        }

        // Temperature (cool to warm, -100 to 100)
        // Tint (green to magenta, -100 to 100)
        if (adj.temperature != 0f || adj.tint != 0f) {
            val tempFactor = adj.temperature / 100f
            val tintFactor = adj.tint / 100f

            val rOffset = (tempFactor * 35f) + (tintFactor * 20f)
            val gOffset = (-tintFactor * 25f)
            val bOffset = (-tempFactor * 35f) + (tintFactor * 20f)

            val tempMatrix = ColorMatrix(
                floatArrayOf(
                    1f, 0f, 0f, 0f, rOffset,
                    0f, 1f, 0f, 0f, gOffset,
                    0f, 0f, 1f, 0f, bOffset,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            result.postConcat(tempMatrix)
        }

        // Sepia (0 to 100)
        if (adj.sepia > 0f) {
            val s = adj.sepia / 100f
            val inv = 1f - s
            val sepiaMatrix = ColorMatrix(
                floatArrayOf(
                    inv + s * 0.393f, s * 0.769f, s * 0.189f, 0f, 0f,
                    s * 0.349f, inv + s * 0.686f, s * 0.168f, 0f, 0f,
                    s * 0.272f, s * 0.534f, inv + s * 0.131f, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            result.postConcat(sepiaMatrix)
        }

        return result
    }

    private fun applyVignette(canvas: Canvas, width: Int, height: Int, intensity: Float) {
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = sqrt((width * width + height * height).toDouble()).toFloat() / 2f

        val colors = intArrayOf(
            Color.TRANSPARENT,
            Color.TRANSPARENT,
            Color.argb((intensity * 180).toInt().coerceIn(0, 220), 0, 0, 0)
        )
        val stops = floatArrayOf(0f, 0.45f, 1f)

        val gradient = RadialGradient(
            centerX,
            centerY,
            radius,
            colors,
            stops,
            Shader.TileMode.CLAMP
        )

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = gradient
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
    }

    /**
     * Convolution-based 3x3 unsharp mask for crisp photo restoration.
     */
    private fun applySharpen(source: Bitmap, strength: Float): Bitmap {
        val width = source.width
        val height = source.height
        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val k = strength * 0.75f
        val center = 1f + 4f * k

        val srcPixels = IntArray(width * height)
        val dstPixels = IntArray(width * height)
        source.getPixels(srcPixels, 0, width, 0, 0, width, height)

        for (y in 1 until height - 1) {
            val yOffset = y * width
            val topOffset = (y - 1) * width
            val bottomOffset = (y + 1) * width

            for (x in 1 until width - 1) {
                val p = srcPixels[yOffset + x]
                val pTop = srcPixels[topOffset + x]
                val pBottom = srcPixels[bottomOffset + x]
                val pLeft = srcPixels[yOffset + x - 1]
                val pRight = srcPixels[yOffset + x + 1]

                val a = (p shr 24) and 0xFF

                val r = (Color.red(p) * center -
                        (Color.red(pTop) + Color.red(pBottom) + Color.red(pLeft) + Color.red(pRight)) * k).toInt().coerceIn(0, 255)

                val g = (Color.green(p) * center -
                        (Color.green(pTop) + Color.green(pBottom) + Color.green(pLeft) + Color.green(pRight)) * k).toInt().coerceIn(0, 255)

                val b = (Color.blue(p) * center -
                        (Color.blue(pTop) + Color.blue(pBottom) + Color.blue(pLeft) + Color.blue(pRight)) * k).toInt().coerceIn(0, 255)

                dstPixels[yOffset + x] = (a shl 24) or (r shl 16) or (g shl 8) or b
            }
        }

        // Copy borders
        for (x in 0 until width) {
            dstPixels[x] = srcPixels[x]
            dstPixels[(height - 1) * width + x] = srcPixels[(height - 1) * width + x]
        }
        for (y in 0 until height) {
            dstPixels[y * width] = srcPixels[y * width]
            dstPixels[y * width + (width - 1)] = srcPixels[y * width + (width - 1)]
        }

        output.setPixels(dstPixels, 0, width, 0, 0, width, height)
        return output
    }
}
