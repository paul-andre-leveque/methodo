package fr.ynov.methodoevalk.domain.model

data class Book (

    val id: Int,
    val title: String,
    val author: String,
    val isReserved: Boolean = false
)