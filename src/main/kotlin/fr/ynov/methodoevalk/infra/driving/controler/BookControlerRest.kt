package fr.ynov.methodoevalk.infra.driving.controler

import fr.ynov.methodoevalk.domain.service.BookService
import fr.ynov.methodoevalk.infra.driving.controler.dto.BookDTO
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/books")
class BookControlerRest(
        private val bookService: BookService
) {

    @GetMapping()
    fun getAllBooks(): List<BookDTO> {
        return bookService.getAllBooks()
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createBook(@RequestBody bookDTO: BookDTO) {
        bookService.createBook(bookDTO)
    }

    @PutMapping("/reserve")
    fun reserveBook(@RequestParam id: Int): ResponseEntity<String> {
        val success = bookService.reserveBook(id)
        return if (success) {
            ResponseEntity("Book reserver", HttpStatus.OK)
        } else {
            ResponseEntity("Book deja reserver ou indisponible", HttpStatus.BAD_REQUEST)
        }
    }
}