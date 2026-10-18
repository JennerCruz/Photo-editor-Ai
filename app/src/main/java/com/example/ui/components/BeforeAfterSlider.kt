package com.example.ui.components

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun BeforeAfterSlider(
    originalBitmap: Bitmap?,
    processedBitmap: Bitmap?,
    splitPosition: Float,
    onSplitPositionChange: (Float) -> Unit,
    isComparingOriginal: Boolean,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0D0F17))
            .testTag("before_after_slider_container")
    ) {
        val totalWidth = constraints.maxWidth.toFloat()
        val totalHeight = constraints.maxHeight.toFloat()

        if (originalBitmap != null && processedBitmap != null) {
            val origImageBitmap = originalBitmap.asImageBitmap()
            val procImageBitmap = processedBitmap.asImageBitmap()

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(totalWidth) {
                        detectDragGestures { change, _ ->
                            change.consume()
                            val newFraction = (change.position.x / totalWidth).coerceIn(0.02f, 0.98f)
                            onSplitPositionChange(newFraction)
                        }
                    }
                    .testTag("split_canvas")
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // Draw destination scaling
                val srcWidth = origImageBitmap.width.toFloat()
                val srcHeight = origImageBitmap.height.toFloat()

                val scale = minOf(canvasWidth / srcWidth, canvasHeight / srcHeight)
                val destWidth = srcWidth * scale
                val destHeight = srcHeight * scale
                val offsetX = (canvasWidth - destWidth) / 2f
                val offsetY = (canvasHeight - destHeight) / 2f

                val destRect = Rect(offsetX, offsetY, offsetX + destWidth, offsetY + destHeight)

                if (isComparingOriginal) {
                    // Show 100% original when user holds compare button
                    drawImage(
                        image = origImageBitmap,
                        dstOffset = IntOffset(destRect.left.roundToInt(), destRect.top.roundToInt()),
                        dstSize = IntSize(destRect.width.roundToInt(), destRect.height.roundToInt())
                    )
                } else {
                    val splitX = canvasWidth * splitPosition

                    // Draw Left: Original Image
                    clipRect(left = 0f, top = 0f, right = splitX, bottom = canvasHeight) {
                        drawImage(
                            image = origImageBitmap,
                            dstOffset = IntOffset(destRect.left.roundToInt(), destRect.top.roundToInt()),
                            dstSize = IntSize(destRect.width.roundToInt(), destRect.height.roundToInt())
                        )
                    }

                    // Draw Right: Processed Image
                    clipRect(left = splitX, top = 0f, right = canvasWidth, bottom = canvasHeight) {
                        drawImage(
                            image = procImageBitmap,
                            dstOffset = IntOffset(destRect.left.roundToInt(), destRect.top.roundToInt()),
                            dstSize = IntSize(destRect.width.roundToInt(), destRect.height.roundToInt())
                        )
                    }

                    // Draw Divider Line
                    drawLine(
                        color = Color.White.copy(alpha = 0.9f),
                        start = Offset(splitX, 0f),
                        end = Offset(splitX, canvasHeight),
                        strokeWidth = 3.dp.toPx()
                    )
                    drawLine(
                        color = Color(0xFF38E1FF).copy(alpha = 0.7f),
                        start = Offset(splitX, 0f),
                        end = Offset(splitX, canvasHeight),
                        strokeWidth = 1.dp.toPx()
                    )
                }
            }

            // Divider Handle Draggable Pill
            if (!isComparingOriginal) {
                val handleX = (maxWidth.value * splitPosition).dp
                Box(
                    modifier = Modifier
                        .offset(x = handleX - 22.dp)
                        .align(Alignment.CenterStart)
                        .size(44.dp)
                        .shadow(8.dp, CircleShape)
                        .background(Color(0xFF141726), CircleShape)
                        .testTag("divider_handle_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFF38E1FF), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.CompareArrows,
                            contentDescription = "Deslizar para comparar",
                            tint = Color(0xFF0D0F17),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // Floating Badges: "ANTES" on left, "DESPUÉS" on right
            RowLabelBadges(
                isComparingOriginal = isComparingOriginal,
                splitPosition = splitPosition,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 12.dp, start = 12.dp, end = 12.dp)
            )
        }
    }
}

@Composable
private fun RowLabelBadges(
    isComparingOriginal: Boolean,
    splitPosition: Float,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        if (isComparingOriginal) {
            Surface(
                color = Color(0xCC000000),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                Text(
                    text = "VISTA ORIGINAL",
                    color = Color(0xFFFFB74D),
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        } else {
            // "ANTES" badge
            AnimatedVisibility(
                visible = splitPosition > 0.15f,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Surface(
                    color = Color(0xB3141726),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(start = 12.dp, top = 8.dp)
                ) {
                    Text(
                        text = "ORIGINAL",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            // "DESPUÉS" badge
            AnimatedVisibility(
                visible = splitPosition < 0.85f,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Surface(
                    color = Color(0xD900838F),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(end = 12.dp, top = 8.dp)
                ) {
                    Text(
                        text = "RESTAURADA",
                        color = Color.White,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }
    }
}
