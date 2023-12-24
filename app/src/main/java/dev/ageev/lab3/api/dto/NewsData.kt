package dev.ageev.lab3.api.dto

import com.google.gson.annotations.SerializedName

data class NewsData (

    @SerializedName("results")
    val news: MutableList<News>?,
)