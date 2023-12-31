package dev.ageev.lab3

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dagger.hilt.android.AndroidEntryPoint
import dev.ageev.lab3.api.dto.News
import dev.ageev.lab3.ui.theme.Lab3Theme
import dev.ageev.lab3.viewmodel.NewsViewModel


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val newsViewModel: NewsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Lab3Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (newsViewModel.showDialog) {
                        ArticleDialog()
                    }
                    Scaffold(
                        topBar = {
                            NewsSearchBar()
                        }
                    ) { paddingValues ->
                        ResultsBox(paddingValues)
                    }
                }
            }
        }
    }

    @Composable
    fun ResultsBox(paddingValues: PaddingValues) {
        Box(Modifier.padding(paddingValues)) {

            val newsState = newsViewModel.newsState
            if (newsState.isLoading) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator()
                }
            }
            if (newsState.error == null && newsState.news == null && !newsState.isLoading) {
                TextPlaceholder(text = "Nothing to show")
            }
            newsState.error?.let {
                TextPlaceholder(text = "Something went wrong")
            }
            newsState.news?.let { newsData ->
                if (newsData.news?.size == 0) {
                    TextPlaceholder(text = "No news found")
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(items = newsData.news?.toList() ?: listOf()) {
                            NewsCard(it)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun TextPlaceholder(text : String) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(text = text)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun NewsCard(it: News) {
        Card(
            shape = RoundedCornerShape(10.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            onClick = {
                newsViewModel.openNewsArticle(it)
            },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(5.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row {
                    Text(
                        modifier = Modifier
                            .weight(0.5f)
                            .padding(5.dp),
                        text = it.title ?: "No title",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }

    @Composable
    fun LanguageDropdownMenu() {
        IconButton(onClick = {
            newsViewModel.displayMenu = newsViewModel.displayMenu.not()
        }) {
            Icon(Icons.Default.MoreVert, "")
        }

        DropdownMenu(
            expanded = newsViewModel.displayMenu,
            onDismissRequest = { newsViewModel.displayMenu = false }
        ) {
            LanguageItem("Russian", "ru")
            LanguageItem("English", "en")
            LanguageItem("French", "fr")
            LanguageItem("Spanish", "es")
        }
    }

    @Composable
    fun LanguageItem(language: String, abbreviation: String) {
        DropdownMenuItem(trailingIcon = {
            if (newsViewModel.language == abbreviation) {
                Icon(Icons.Default.Check, "")
            }
        },
            text = { Text(text = language) }, onClick = {
                newsViewModel.language = abbreviation
                newsViewModel.displayMenu =
                    newsViewModel.displayMenu.not()
            })
    }

    @Composable
    fun ArticleDialog() {
        Dialog(
            onDismissRequest = { newsViewModel.showDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(0.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        TextButton(
                            onClick = { newsViewModel.showDialog = false },
                            modifier = Modifier.padding(8.dp),
                        ) {
                            Text("Back")
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(
                            onClick = {
                                if (newsViewModel.currentLink == "") {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "No link to article",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    startActivity(
                                        Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse(newsViewModel.currentLink)
                                        )
                                    )
                                }
                            },
                            modifier = Modifier.padding(8.dp),
                        ) {
                            Text("Full Article")
                        }
                    }
                    Text(
                        text = newsViewModel.currentTitle,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = newsViewModel.currentContent,
                        textAlign = TextAlign.Justify,
                        modifier = Modifier.padding(16.dp),
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun NewsSearchBar() {
        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            query = newsViewModel.query,
            onQueryChange = { newsViewModel.query = it },
            onSearch = {
                newsViewModel.active = false
                newsViewModel.loadNews()
            },
            active = newsViewModel.active,
            onActiveChange = {
                newsViewModel.active = it
            },
            placeholder = {
                Text(text = newsViewModel.searchPlaceholder)
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null
                )
            },
            trailingIcon = { LanguageDropdownMenu() }
        ) {
            val newsState = newsViewModel.newsState
            if (newsState.isLoading) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator()
                }
            }

        }
    }
}