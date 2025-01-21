package fr.ynov.methodoevalk.domain.service

import fr.ynov.methodoevalk.domain.model.Book
import fr.ynov.methodoevalk.domain.port.BookPort
import fr.ynov.methodoevalk.infra.driving.controler.dto.BookDTO
import fr.ynov.methodoevalk.infra.driving.mapper.BookMapper

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import io.kotest.core.spec.style.StringSpec
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class BookServiceTest : StringSpec({

    "getAllBooks" {

        val bookPort = mockk<BookPort>()
        val bookMapper = mockk<BookMapper>()
        val bookService = BookService(bookPort, bookMapper)

        val books = listOf(
            Book(id = 2, title = "Kotlin", author = "Dmitry", isReserved = true),
            Book(id = 1, title = "Java", author = "Joshua", isReserved = false)
        )

        val bookDTOs = listOf(
            BookDTO(id = 1, title = "Java", author = "Joshua", isReserved = false),
            BookDTO(id = 2, title = "Kotlin", author = "Dmitry", isReserved = true)
        )

        every { bookPort.getAllBooks() } returns books
        every { bookMapper.toDTO(any()) } answers { callOriginal() }

        val result = bookService.getAllBooks()

        result shouldBe bookDTOs.sortedBy { it.title }
        verify(exactly = 1) { bookPort.getAllBooks() }
        verify(exactly = books.size) { bookMapper.toDTO(any()) }
    }

    "createBook" {
        val bookPort = mockk<BookPort>()
        val bookMapper = mockk<BookMapper>()
        val bookService = BookService(bookPort, bookMapper)

        val bookDTO = BookDTO(id = 0, title = "Kotlin", author = "Dmitry", isReserved = false)
            val book = Book(id = 0, title = "Java", author = "Joshua", isReserved = false)

        every { bookMapper.toModel(bookDTO) } returns book
        justRun { bookPort.createBook(book) }

        bookService.createBook(bookDTO)

        verify(exactly = 1) {
            bookMapper.toModel(bookDTO)
            bookPort.createBook(book)
        }
    }

    "reserveBook" {

        val bookPort = mockk<BookPort>()
        val bookMapper = mockk<BookMapper>()
        val bookService = BookService(bookPort, bookMapper)

        val bookId = 1
        every { bookPort.reserveBook(bookId) } returns true

        val result = bookService.reserveBook(bookId)

        result shouldBe true
        verify(exactly = 1) { bookPort.reserveBook(bookId) }
    }

    "reserveBook KO" {

        val bookPort = mockk<BookPort>()
        val bookMapper = mockk<BookMapper>()
        val bookService = BookService(bookPort, bookMapper)

        val bookId = 1
        every { bookPort.reserveBook(bookId) } returns false

        val result = bookService.reserveBook(bookId)

        result shouldBe false
        verify(exactly = 1) { bookPort.reserveBook(bookId) }
    }
})
