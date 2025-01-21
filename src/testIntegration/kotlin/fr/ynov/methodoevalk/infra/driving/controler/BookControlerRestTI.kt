import com.ninjasquad.springmockk.MockkBean
import fr.ynov.methodoevalk.MethodoEvalKApplication
import fr.ynov.methodoevalk.domain.service.BookService
import fr.ynov.methodoevalk.infra.driving.controler.BookControlerRest
import fr.ynov.methodoevalk.infra.driving.controler.dto.BookDTO
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.mockk.every
import io.mockk.justRun
import io.mockk.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put

@WebMvcTest(controllers = [BookControlerRest::class])
@ContextConfiguration(classes = [MethodoEvalKApplication::class])
class BookControlerRestIT(
    @Autowired private val mockMvc: MockMvc,
    @MockkBean private val bookService: BookService,
) : FunSpec({

    extension(SpringExtension)

    test("doit retourner la liste des livres avec isReserved") {

        every { bookService.getAllBooks() } returns listOf(
            BookDTO(id = 1, title = "test", author = "tata", isReserved = false),
            BookDTO(id = 2, title = "titi", author = "toto", isReserved = true)
        )

        mockMvc.get("/books")
            .andExpect {
                status { isOk() }
                content { contentType(APPLICATION_JSON) }
                jsonPath("$[0].id") { value(1) }
                jsonPath("$[0].title") { value("test") }
                jsonPath("$[0].author") { value("tata") }
                jsonPath("$[0].reserved") { value(false) }
                jsonPath("$[1].id") { value(2) }
                jsonPath("$[1].title") { value("titi") }
                jsonPath("$[1].author") { value("toto") }
                jsonPath("$[1].reserved") { value(true) }
            }
    }

    test("doit créer un livre") {

        val newBookDTO = BookDTO(id = 0, title = "test1", author = "test2")
        justRun { bookService.createBook(newBookDTO) }

        mockMvc.post("/books") {
            contentType = APPLICATION_JSON
            accept = APPLICATION_JSON
            content = """
              {
                "title": "test1",
                "author": "test2"
              }
            """.trimIndent()
        }
            .andExpect {
                status { isCreated() }
            }

        verify(exactly = 1) {
            bookService.createBook(newBookDTO)
        }
    }

    test("doit retourner 400 si le JSON est invalide") {

        mockMvc.post("/books") {
            contentType = APPLICATION_JSON
            accept = APPLICATION_JSON
            content = """ { "name": "tata" } """ // Champ invalide
        }
            .andExpect {
                status { isBadRequest() }
            }

        verify(exactly = 0) {
            bookService.createBook(any())
        }
    }

    test("doit réserver un livre avec isReserved") {

        every { bookService.reserveBook(1) } returns true

        mockMvc.put("/books/reserve") {
            param("id", "1")
        }
            .andExpect {
                status { isOk() }
                content { string("Book reserver") }
            }

        verify(exactly = 1) {
            bookService.reserveBook(1)
        }
    }

    test("doit retourner une erreur lors de la réservation d'un livre déjà réservé ou inexistant") {

        every { bookService.reserveBook(1) } returns false

        mockMvc.put("/books/reserve") {
            param("id", "1")
        }
            .andExpect {
                status { isBadRequest() }
                content { string("Book deja reserver ou indisponible") }
            }

        verify(exactly = 1) {
            bookService.reserveBook(1)
        }
    }

})
