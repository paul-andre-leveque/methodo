package fr.ynov.methodoevalk.domain.service

import fr.ynov.methodoevalk.domain.port.BookPort
import fr.ynov.methodoevalk.infra.driving.controler.dto.BookDTO
import fr.ynov.methodoevalk.infra.driving.mapper.BookMapper

class BookService(
        private val bookPort: BookPort,
        private val bookMapper: BookMapper
) {
    fun getAllBooks(): List<BookDTO> {
        val books = bookPort.getAllBooks()
        return books.sortedBy { it.title }
                .map { bookMapper.toDTO(it) }
    }
    fun createBook(bookDTO: BookDTO) {
        val book = bookMapper.toModel(bookDTO)
        bookPort.createBook(book)
    }
    fun reserveBook(id: Int): Boolean {
        return bookPort.reserveBook(id)
    }
}