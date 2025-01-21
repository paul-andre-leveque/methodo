package fr.ynov.methodoevalk.domain.port

import fr.ynov.methodoevalk.domain.model.Book

interface BookPort {
    fun getAllBooks(): List<Book>
    fun createBook(book: Book)
    fun reserveBook(id: Int): Boolean

}