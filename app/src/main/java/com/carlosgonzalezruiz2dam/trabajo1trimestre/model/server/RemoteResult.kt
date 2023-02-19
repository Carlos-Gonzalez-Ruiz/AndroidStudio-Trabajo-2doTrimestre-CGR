package com.carlosgonzalezruiz2dam.trabajo1trimestre.model.server

import java.util.*

data class RemoteResult(
    val status: String,
    val totalResults: Int,
    val articles: List<New>
)

data class Source (
    val id: String,
    val name: String
)

data class New(
    val source: Source,
    val author: String,
    val title: String,
    val description: String,
    val url: String,
    val urlToImage : String,
    val publishedAt : String,
    val content : String
)