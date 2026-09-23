package com.fhmsyhd.cinema.view.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.fhmsyhd.cinema.data.domain.model.Movie
import com.fhmsyhd.cinema.data.domain.usecase.movie.GetFavoriteMovieUseCase
import com.fhmsyhd.cinema.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(getFavoriteMovieUseCase: GetFavoriteMovieUseCase): ViewModel() {
    val favoriteMovie: LiveData<UiState<List<Movie>>> = getFavoriteMovieUseCase().map { list ->
        if (list.isNotEmpty()) {
            UiState.Success(list)
        } else {
            UiState.Error("error_empty_favorite")
        }
    }.asLiveData()
}
