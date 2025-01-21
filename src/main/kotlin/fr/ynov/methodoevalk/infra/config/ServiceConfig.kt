package fr.ynov.methodoevalk.infra.config

import fr.ynov.methodoevalk.domain.port.BookPort
import fr.ynov.methodoevalk.domain.service.BookService
import fr.ynov.methodoevalk.infra.driving.mapper.BookMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ServiceConfig {
    @Bean
    fun bookMapper(): BookMapper {
        return BookMapper()
    }

    @Bean
    fun bookService(bookPort: BookPort, bookMapper: BookMapper): BookService {
        return BookService(bookPort, bookMapper)
    }
}

