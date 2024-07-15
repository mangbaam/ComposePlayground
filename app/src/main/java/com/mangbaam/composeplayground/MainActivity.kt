package com.mangbaam.composeplayground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mangbaam.composeplayground.pager.sample.KyoboEBook
import com.mangbaam.composeplayground.ui.theme.ComposePlaygroundTheme

class MainActivity : ComponentActivity() {
    private val Books = mapOf(
        "Kotlin in Action" to "S000001804588",
        "코틀린 코루틴" to "S000210537188",
        "이펙티브 코틀린" to "S000001033129",
        "보편의 단어" to "S000211734678",
        "해커스 토익 기출 VOCA(보카)" to "S000001020219",
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposePlaygroundTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = Color.LightGray,
                    ) {
                        var currentBook by remember { mutableStateOf("") }
                        var keyword by remember { mutableStateOf("") }

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

                            Row(
                                modifier = Modifier
                                    .padding(top = 16.dp)
                                    .align(Alignment.CenterHorizontally),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                TextField(value = keyword, onValueChange = { keyword = it })
                                Button(onClick = { currentBook = keyword }) {
                                    Text(text = "검색")
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
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ComposePlaygroundTheme {
        Greeting("Android")
    }
}
