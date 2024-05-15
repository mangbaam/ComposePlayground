package com.mangbaam.composeplayground.shape

import android.graphics.Bitmap
import androidx.compose.ui.geometry.Offset
import coil.transform.Transformation

class SquareTransformation(
    private val offset: Offset = Offset.Zero,
    private val size: Int? = null,
) : Transformation {
    override val cacheKey: String = this.javaClass.name

    override suspend fun transform(input: Bitmap, size: coil.size.Size): Bitmap {
        val squareSize = minOf(input.width, input.height, this.size ?: Int.MAX_VALUE)
        return Bitmap.createBitmap(
            input,
            minOf(offset.x.toInt(), input.width),
            minOf(offset.y.toInt(), input.height),
            squareSize,
            squareSize,
        )
    }
}
