package dev.ageev.lab3.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ageev.lab3.api.NewsApi
import dev.ageev.lab3.api.dto.News
import dev.ageev.lab3.model.NewsState
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NewsViewModel
@Inject constructor(val newsApi: NewsApi) : ViewModel() {

    var newsState by mutableStateOf(NewsState())
        private set

    var searchPlaceholder by mutableStateOf(String())

    var active: Boolean by mutableStateOf(false)
    var showDialog by mutableStateOf(false)

    var language by mutableStateOf("ru")
    var currentTitle by mutableStateOf("No title")
    var currentContent by mutableStateOf("No content")
    var currentLink by mutableStateOf(String())

    companion object {
        const val API_KEY = "pub_3519324d9bfc906a67286dbce3077fefea93c"
    }

    fun loadNews() {
        viewModelScope.launch {
            try {
                newsState = newsState.copy(
                    news = null,
                    isLoading = true,
                    error = null
                )
                val news = newsApi.news(API_KEY, searchPlaceholder, language)
                newsState = newsState.copy(
                    news = news,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                Log.e(null, e.message ?: "")
                newsState = newsState.copy(
                    news = null,
                    isLoading = false,
                    error = e.message
                )
            }

        }
    }

    fun openNewsArticle(it: News) {
        showDialog = true
        currentTitle = it.title ?: currentTitle
        currentContent = it.content ?: currentContent
        currentLink = it.link ?: currentLink
    }

}