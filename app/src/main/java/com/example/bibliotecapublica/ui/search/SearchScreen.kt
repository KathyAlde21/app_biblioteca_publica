package com.example.bibliotecapublica.ui.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bibliotecapublica.data.model.Book

@Composable
fun SearchScreen(
    viewModel: SearchViewModel
) {
    // StateFlow → UI reactiva a los cambios del ViewModel
    val uiState by viewModel.uiState.collectAsState()

    var query by remember { mutableStateOf("") }

    // Este Composable corre en Main Thread, solo dibuja y reacciona a estados
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Biblioteca Pública",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Buscar por título o autor") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                // acá solo se dispara la búsqueda, la lógica pesada vive en el ViewModel
                viewModel.searchBooks(query)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Buscar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when {
            uiState.isLoading -> {
                // ProgressBar → feedback visual mientras corre la corrutina de búsqueda
                CircularProgressIndicator()
            }

            uiState.errorMessage != null -> {
                Text(
                    text = uiState.errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error
                )
            }

            uiState.results.isEmpty() && query.isNotBlank() -> {
                Text("No se encontraron libros para \"$query\"")
            }

            else -> {
                BookList(books = uiState.results)
            }
        }
    }
}

@Composable
fun BookList(books: List<Book>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(books) { book ->
            BookItem(book = book)
            Divider()
        }
    }
}

@Composable
fun BookItem(book: Book) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(text = book.title, style = MaterialTheme.typography.titleMedium)
        Text(text = "Autor: ${book.author}")
        Text(
            text = if (book.available) "Disponible" else "No disponible",
            color = if (book.available) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.error
        )
    }
}
