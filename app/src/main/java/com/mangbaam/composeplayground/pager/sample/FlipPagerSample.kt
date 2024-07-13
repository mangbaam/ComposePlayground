package com.mangbaam.composeplayground.pager.sample

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mangbaam.composeplayground.pager.FlipPager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import kotlin.math.roundToInt

private val Books = mapOf(
    "Kotlin in Action" to "S000001804588",
    "코틀린 코루틴" to "S000210537188",
    "이펙티브 코틀린" to "S000001033129",
    "보편의 단어" to "S000211734678",
    "해커스 토익 기출 VOCA(보카)" to "S000001020219",
)

@Composable
fun KyoboEBook(
    bookId: String,
    modifier: Modifier = Modifier,
    pagePerView: Int = 2,
) {
    val scope = rememberCoroutineScope()
    var pages by remember {
        mutableStateOf<List<Pair<String, String>>>(emptyList())
    }
    LaunchedEffect(bookId) {
        pages = scope.async { getKyoboEbookPages(bookId) }.await()
    }

    pages.forEach {
        Log.d("[MANGBAAM]KyoboEbook", "url: ${it.first} desc: ${it.second}")
    }

    val state = rememberPagerState { (pages.size / pagePerView.toFloat()).roundToInt() }
    LaunchedEffect(bookId) { state.scrollToPage(0) }

    FlipPager(
        modifier = modifier
            .padding(30.dp)
            .aspectRatio((817 / 1024f) * pagePerView),
        state = state,
    ) { page ->
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            repeat(pagePerView) {
                val (imgUrl, desc) = pages
                    .getOrElse(page * pagePerView + it % pagePerView) {
                        Pair("", "")
                    }

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .background(Color.White),
                    contentAlignment = Alignment.Center,
                ) {
                    AsyncImage(
                        model = imgUrl,
                        contentDescription = desc,
                    )
                }
            }
        }
    }
}

private suspend fun getKyoboEbookPages(
    bookId: String
): List<Pair<String, String>> = withContext(Dispatchers.IO) {
    val url = "https://product.kyobobook.co.kr/book/preview/$bookId"
    val doc = Jsoup.connect(url).get()

    return@withContext doc.select(
        "#popProductPreviewPaper > div.dialog_contents > div > div.viewer_box > div > div.preview_item"
    ).map { previewItem ->
        previewItem.select("div > img").let {
            it.attr("src") to it.attr("alt")
        }
    }
}

@Preview
@Composable
private fun KyoboEBookPreview() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.LightGray,
    ) {
        var currentBook by remember { mutableStateOf("") }
        Column {
            Column(
                modifier = Modifier.padding(start = 32.dp),
            ) {
                Books.entries.forEach { (bookName, bookId) ->
                    Row(
                        modifier = Modifier.clickable { currentBook = bookId },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = currentBook == bookId,
                            onClick = { currentBook = bookId },
                        )
                        Text(text = bookName)
                    }
                }
            }
            if (currentBook.isNotEmpty()) {
                KyoboEBook(
                    modifier = Modifier.padding(top = 36.dp),
                    bookId = currentBook,
                )
            }
        }
    }
}
