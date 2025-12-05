package com.example.bibliotecapublica

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import com.example.bibliotecapublica.data.repository.FakeBookRepository
import com.example.bibliotecapublica.ui.search.SearchScreen
import com.example.bibliotecapublica.ui.search.SearchViewModel
import com.example.bibliotecapublica.ui.search.SearchViewModelFactory
import com.example.bibliotecapublica.ui.theme.BibliotecaPublicaTheme

class MainActivity : ComponentActivity() {

    // ViewModel asociado a la Activity (ciclo de vida de pantalla)
    private val searchViewModel: SearchViewModel by viewModels {
        SearchViewModelFactory(FakeBookRepository())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Main Thread → setContent + Compose UI
        setContent {
            BibliotecaPublicaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Composable de búsqueda en la biblioteca
                    SearchScreen(viewModel = searchViewModel)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        // acá se demuestra la cancelación del Job al abandonar la pantalla
        searchViewModel.cancelSearchFromActivity()
    }

    override fun onDestroy() {
        // por si quedara algo pendiente, se refuerza la cancelación
        searchViewModel.cancelSearchFromActivity()
        super.onDestroy()
    }
}