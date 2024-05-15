package com.mangbaam.composeplayground.shape

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.Transformation
import com.mangbaam.composeplayground.ui.theme.ComposePlaygroundTheme

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

@Preview
@Composable
private fun SquareTransformationPreview() {
    val imageUrl = "https://source.unsplash.com/random/360x640?sig=6"

    ComposePlaygroundTheme {
        Surface {
            Column {
                Text(text = "원본 이미지")
                AsyncImage(model = imageUrl, contentDescription = null)
                Spacer(modifier = Modifier.size(30.dp))

                Text(text = "정사각형 이미지")
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .transformations(SquareTransformation())
                        .build(),
                    contentDescription = null
                )
            }
        }
    }
}
