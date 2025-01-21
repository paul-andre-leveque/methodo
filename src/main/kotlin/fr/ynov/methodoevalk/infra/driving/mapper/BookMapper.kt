package fr.ynov.methodoevalk.infra.driving.mapper

import fr.ynov.methodoevalk.domain.model.Book
import fr.ynov.methodoevalk.infra.driving.controler.dto.BookDTO
import org.springframework.stereotype.Component

@Component
class BookMapper {

    fun toDTO(book: Book): BookDTO {
        return BookDTO(
                id = book.id,
                title = book.title,
                author = book.author,
                isReserved = book.isReserved
        )
    }

    fun toModel(bookDTO: BookDTO): Book {
        return Book(
                id = bookDTO.id,
                title = bookDTO.title,
                author = bookDTO.author,
                isReserved = bookDTO.isReserved
        )
    }
}