package com.example.bibliotecapublica.data.repository

import com.example.bibliotecapublica.data.model.Book
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

// esta clase cumple el contrato de BookRepository y simula una API REST
class FakeBookRepository : BookRepository {

    // Mock local que simula una API/BD remota
    private val books = listOf(
        Book(1, "Cien años de soledad", "Gabriel García Márquez", true),
        Book(2, "El principito", "Antoine de Saint-Exupéry", false),
        Book(3, "Rayuela", "Julio Cortázar", true),
        Book(4, "La ciudad y los perros", "Mario Vargas Llosa", true),
        Book(5, "Don Quijote de la Mancha", "Miguel de Cervantes", false),
    )

    override suspend fun searchBooks(query: String): List<Book> =
        // acá nos vamos al hilo de background (IO) para simular la API
        withContext(Dispatchers.IO) {
            val deferredResults = async {
                // simulación de latencia de servidor
                delay(1500L)

                if (query.isBlank()) {
                    emptyList()
                } else {
                    books.filter { book ->
                        book.title.contains(query, ignoreCase = true) ||
                                book.author.contains(query, ignoreCase = true)
                    }
                }
            }

            // acá esperamos el resultado sin bloquear el Main Thread
            deferredResults.await()
        }
}