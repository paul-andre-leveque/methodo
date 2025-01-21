package fr.ynov.methodoevalk.infra.driven

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import fr.ynov.methodoevalk.domain.model.Book
import fr.ynov.methodoevalk.infra.driven.adapter.BookDAO
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.containers.PostgreSQLContainer
import java.sql.ResultSet

@SpringBootTest
@ActiveProfiles("testIntegration")
class BookDAOIT(
    private val bookDAO: BookDAO
) : StringSpec({

    "getAllBooks() doit retourner les livres présents en base avec isReserved" {
        // Arrange
        performQuery(
            """
            INSERT INTO book (id, title, author, is_reserved)
            VALUES 
                (1, 'java', 'Joshua', false),
                (2, 'kotlin', 'Dmitry', true),
                (3, 'jojo', 'Rowling', false);
            """.trimIndent()
        )

        // Act
        val res = bookDAO.getAllBooks()

        // Assert
        res.shouldContainExactly(
            Book(id = 1, title = "java", author = "Joshua", isReserved = false),
            Book(id = 2, title = "kotlin", author = "Dmitry", isReserved = true),
            Book(id = 3, title = "jojo", author = "Rowling", isReserved = false)
        )
    }

    "createBook() doit insérer un livre en base avec isReserved par défaut" {

        val newBook = Book(id = 0, title = "kotlin", author = "Dmitry")

        bookDAO.createBook(newBook)

        val rows = performQuery("SELECT * FROM book WHERE title = 'kotlin' AND author = 'Dmitry'")
        rows shouldHaveSize 1
        val row = rows.first()
        (row["id"] as Int) shouldBeGreaterThan 0
        row["title"] shouldBe "kotlin"
        row["author"] shouldBe "Dmitry"
        row["is_reserved"] shouldBe false
    }

    "reserveBook(id) doit réserver un livre non réservé" {

        performQuery(
            """
            INSERT INTO book (id, title, author, is_reserved)
            VALUES 
                (1, 'java', 'Joshua', false);
            """.trimIndent()
        )

        // Act
        val result = bookDAO.reserveBook(1)

        // Assert
        result.shouldBeTrue()
        val isReserved = performQuery("SELECT is_reserved FROM book WHERE id = 1").first()["is_reserved"] as Boolean
        isReserved shouldBe true
    }

    "reserveBook(id) ne doit pas réserver un livre déjà réservé" {
        // Arrange
        performQuery(
            """
            INSERT INTO book (id, title, author, is_reserved)
            VALUES 
                (1, 'java', 'Joshua', true);
            """.trimIndent()
        )

        val result = bookDAO.reserveBook(1)

        result.shouldBeFalse()
        val isReserved = performQuery("SELECT is_reserved FROM book WHERE id = 1").first()["is_reserved"] as Boolean
        isReserved shouldBe true
    }

    "reserveBook(id) ne doit pas réserver un livre inexistant" {

        val result = bookDAO.reserveBook(999) // ID inexistant

        result.shouldBeFalse()
    }

    beforeTest {
        performQuery("DELETE FROM book")
    }

}) {

    companion object {
        private val container = PostgreSQLContainer<Nothing>("postgres:13-alpine").apply {
            start()
            System.setProperty("spring.datasource.url", jdbcUrl)
            System.setProperty("spring.datasource.username", username)
            System.setProperty("spring.datasource.password", password)
        }
        fun performQuery(sql: String): List<Map<String, Any>> {
            val hikariConfig = HikariConfig().apply {
                jdbcUrl = container.jdbcUrl
                username = container.username
                password = container.password
                driverClassName = container.driverClassName
            }
            HikariDataSource(hikariConfig).use { ds ->
                ds.connection.use { conn ->
                    val statement = conn.createStatement()
                    val hasResultSet = statement.execute(sql)
                    return if (hasResultSet) {
                        statement.resultSet.use { rs ->
                            rs.toList()
                        }
                    } else {
                        emptyList()
                    }
                }
            }
        }
        private fun ResultSet.toList(): List<Map<String, Any>> {
            val md = this.metaData
            val columns = md.columnCount
            val rows: MutableList<Map<String, Any>> = mutableListOf()

            while (this.next()) {
                val row: MutableMap<String, Any> = mutableMapOf()
                for (i in 1..columns) {
                    row[md.getColumnName(i)] = this.getObject(i)
                }
                rows.add(row)
            }
            return rows
        }
    }
}