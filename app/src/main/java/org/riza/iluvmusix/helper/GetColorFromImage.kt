package org.riza.iluvmusix.helper

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.palette.graphics.Palette
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.core.graphics.drawable.toBitmap
import coil.request.ImageRequest

// 1. The math function
fun extractMixedColorFromBitmap(bitmap: Bitmap, onColorGenerated: (Color) -> Unit) {
    Palette.Builder(bitmap).maximumColorCount(24).generate { palette ->
        val topSwatches = palette?.swatches
            ?.sortedByDescending { it.population }
            ?.take(3)

        if (topSwatches.isNullOrEmpty()) return@generate

        val totalPopulation = topSwatches.sumOf { it.population }.toFloat()
        var mixedRed = 0f
        var mixedGreen = 0f
        var mixedBlue = 0f

        for (swatch in topSwatches) {
            val weight = swatch.population / totalPopulation
            val color = Color(swatch.rgb)

            mixedRed += color.red * weight
            mixedGreen += color.green * weight
            mixedBlue += color.blue * weight
        }

        onColorGenerated(Color(red = mixedRed, green = mixedGreen, blue = mixedBlue))
    }
}

@Composable
fun rememberPaletteImageRequest(
    imageUrl: String?,
    context: Context,
    onColorExtracted: (Color) -> Unit
): ImageRequest {
    return remember(imageUrl) {
        ImageRequest.Builder(context)
            .data(imageUrl)
            .allowHardware(false)
            .listener(
                onSuccess = { _, result ->
                    val bitmap = result.drawable.toBitmap()
                    // Call our extracted math function!
                    extractMixedColorFromBitmap(bitmap, onColorExtracted)
                }
            )
            .build()
    }
}