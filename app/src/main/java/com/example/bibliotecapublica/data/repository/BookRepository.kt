package com.example.bibliotecapublica.data.repository

import com.example.bibliotecapublica.data.model.Book

// acá va solo el contrato de repositorio, sin implementación
interface BookRepository {
    suspend fun searchBooks(query: String): List<Book>
}