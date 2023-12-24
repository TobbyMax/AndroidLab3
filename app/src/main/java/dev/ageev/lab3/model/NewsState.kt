package dev.ageev.lab3.model

import dev.ageev.lab3.api.dto.NewsData

data class NewsState(
    val news: NewsData? = null,
    val isLoading: Boolean = false,
    val error: String? = null
) {

}