package fr.ynov.methodoevalk.infra.driving.controler.dto

data class BookDTO (
    val id: Int = 0,
    val title: String,
    val author: String,
    val isReserved: Boolean = false
)