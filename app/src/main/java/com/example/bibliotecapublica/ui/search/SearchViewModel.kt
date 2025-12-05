package com.example.bibliotecapublica.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.bibliotecapublica.data.model.Book
import com.example.bibliotecapublica.data.repository.BookRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SearchUiState(
    val isLoading: Boolean = false,
    val results: List<Book> = emptyList(),
    val errorMessage: String? = null
)

class SearchViewModel(
    private val repository: BookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState

    // Job específico para la búsqueda actual
    private var searchJob: Job? = null

    fun searchBooks(query: String) {
        // Si ya hay una búsqueda corriendo, se cancela antes de lanzar otra
        searchJob?.cancel()

        // launch → corrutina ligada al Main Thread (UI) por defecto
        searchJob = viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null
                )

                // llamada suspend que se va a ejecutar en Dispatchers.IO (repositorio)
                val books = repository.searchBooks(query)

                // de vuelta en Main Thread, se actualiza la UI
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    results = books
                )
            } catch (e: Exception) {
                // acá va manejo básico de error de la “API”
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al buscar libros"
                )
            }
        }
    }

    // Cancelación explícita desde Activity (onStop/onDestroy)
    fun cancelSearchFromActivity() {
        // acá se corta cualquier búsqueda pendiente al salir de la pantalla
        searchJob?.cancel()
        _uiState.value = _uiState.value.copy(
            isLoading = false
        )
    }
}

// Factory simple para inyectar el repositorio sin usar Hilt
class SearchViewModelFactory(
    private val repository: BookRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
