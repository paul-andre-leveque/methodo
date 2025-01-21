package fr.ynov.methodoevalk

import io.cucumber.java.Before
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.kotest.matchers.shouldBe
import io.restassured.RestAssured
import io.restassured.http.ContentType

import io.restassured.response.ValidatableResponse
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.stereotype.Component


@Component
class BookStepDefs {

    companion object {
        lateinit var lastBookResult: ValidatableResponse
    }

    @LocalServerPort
    var port: Int = 0

    @Before
    fun setup() {
        RestAssured.port = port
        RestAssured.baseURI = "http://localhost"
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
    }

    @When("createBook")
    fun createBook(title: String, author: String) {
        lastBookResult = RestAssured
            .given()
            .contentType(ContentType.JSON)
            .and()
            .body(
                """
                    {

                      "title": "$title",
                      "author": "$author"
                    }
                """.trimIndent()
            )
            .`when`()
            .post("/books")
            .then()
    }


    @When("allBooks")
    fun getAllBooks() {
        lastBookResult = RestAssured
            .given()
            .contentType(ContentType.JSON)
            .`when`()
            .get("/books")
            .then()
    }

    @Then("the list should contain the following books in the same order")
    fun shouldHaveListOfBooks(payload: List<Map<String, Any>>) {
        lastBookResult.statusCode(200)

        val expectedResponse = payload.map { book ->
            mapOf(
                "id" to book["id"],
                "title" to book["title"],
                "author" to book["author"],
                "isReserved" to book["isReserved"]
            )
        }

        val actualResponse: List<Map<String, Any>> =
            lastBookResult.extract().body().`as`(List::class.java) as List<Map<String, Any>>

        actualResponse shouldBe expectedResponse
    }

    @Then("reserve ok")
    fun reservationShouldBeSuccessful() {
        lastBookResult.statusCode(200)
        lastBookResult.extract().body().asString() shouldBe "Book reserved successfully."
    }

    @Then("reseve ko")
    fun reservationShouldFail() {
        lastBookResult.statusCode(400)
        lastBookResult.extract().body().asString() shouldBe "Book is already reserved or does not exist."
    }

    @Given("deja resever")
    fun reserveBookGiven(id: Int) {
        lastBookResult = RestAssured
            .given()
            .param("id", id)
            .`when`()
            .put("/books/reserve")
            .then()
    }
}