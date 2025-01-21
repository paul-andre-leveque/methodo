package fr.ynov.methodoevalk.infra.driven.adapter


import fr.ynov.methodoevalk.domain.model.Book
import fr.ynov.methodoevalk.domain.port.BookPort
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

import org.springframework.stereotype.Service

@Service
class BookDAO(
        private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate
) : BookPort {
    override fun getAllBooks(): List<Book> {
        val sql = "SELECT * FROM book"
        return namedParameterJdbcTemplate.query(sql, MapSqlParameterSource()) { rs, _ ->
            Book(
                    id = rs.getInt("id"),
                    title = rs.getString("title"),
                    author = rs.getString("author"),
                    isReserved = rs.getBoolean("isReserved")
            )
        }
    }

    override fun createBook(book: Book) {
        val sql = "INSERT INTO book (title, author, is_reserved) VALUES (:title, :author: :isReserved)"
        val params = mapOf(
                "title" to book.title,
                "author" to book.author,
                "isReserved" to book.isReserved
        )
        namedParameterJdbcTemplate.update(sql, params)
    }

    override fun reserveBook(id: Int): Boolean {
        val checkSql = "SELECT * FROM book WHERE id = :id"
        val books = namedParameterJdbcTemplate.query(checkSql, mapOf("id" to id)) { rs, _ ->
            Book(
                id = rs.getInt("id"),
                title = rs.getString("title"),
                author = rs.getString("author"),
                isReserved = rs.getBoolean("is_reserved")
            )
        }
        val book = books.firstOrNull()

        if (book == null || book.isReserved) {
            return false
        }
        val updateSql = "UPDATE book SET is_reserved = TRUE WHERE id = :id"
        val rowsUpdated = namedParameterJdbcTemplate.update(updateSql, mapOf("id" to id))
        return rowsUpdated > 0
    }
}