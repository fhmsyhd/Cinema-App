package com.fhmsyhd.cinema.view.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.fhmsyhd.cinema.data.domain.model.Movie
import com.fhmsyhd.cinema.data.domain.usecase.movie.GetFavoriteMovieUseCase
import com.fhmsyhd.cinema.data.domain.usecase.movie.SetFavoriteMovieUseCase
import com.fhmsyhd.cinema.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    getFavoriteMovieUseCase: GetFavoriteMovieUseCase,
    private val setFavoriteMovieUseCase: SetFavoriteMovieUseCase
) : ViewModel() {

    private val favoriteStates: Flow<UiState<List<Movie>>> = getFavoriteMovieUseCase().map { movies ->
        UiState.Success(movies)
    }

    val favoriteMovie: LiveData<UiState<List<Movie>>> = favoriteStates
        .onStart { emit(UiState.Loading) }
        .asLiveData()

    fun removeFavorite(movie: Movie) {
        setFavoriteMovieUseCase(movie, false)
    }

    fun restoreFavorite(movie: Movie) {
        setFavoriteMovieUseCase(movie, true)
    }
}
