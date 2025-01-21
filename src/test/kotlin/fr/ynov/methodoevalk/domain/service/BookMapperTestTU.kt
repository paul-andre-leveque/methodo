package fr.ynov.methodoevalk.domain.service

import fr.ynov.methodoevalk.domain.model.Book
import fr.ynov.methodoevalk.infra.driving.controler.dto.BookDTO
import fr.ynov.methodoevalk.infra.driving.mapper.BookMapper
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class BookMapperTest : FunSpec({
    val mapper = BookMapper()

    test("toDTO() should map Book to BookDTO") {

        val book = Book(
            id = 1,
            title = "Kotlin",
            author = "Dmitry",
            isReserved = false
        )

        val result = mapper.toDTO(book)

        result.id shouldBe 1
        result.title shouldBe "Kotlin"
        result.author shouldBe "Dmitry"
        result.isReserved shouldBe false
    }

    test("toModel() should map BookDTO to Book") {

        val dto = BookDTO(
            id = 2,
            title = "Java",
            author = "Joshua",
            isReserved = true
        )

        val result = mapper.toModel(dto)

        result.id shouldBe 2
        result.title shouldBe "Java"
        result.author shouldBe "Joshua"
        result.isReserved shouldBe true
    }
})