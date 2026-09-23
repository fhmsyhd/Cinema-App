package com.fhmsyhd.cinema.view.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.fhmsyhd.cinema.data.data.Resource
import com.fhmsyhd.cinema.data.domain.model.Movie
import com.fhmsyhd.cinema.data.domain.usecase.movie.GetAllPlayingMovieUseCase
import com.fhmsyhd.cinema.data.domain.usecase.movie.GetPopularMovieUseCase
import com.fhmsyhd.cinema.data.domain.usecase.movie.GetTopRatedMovieUseCase
import com.fhmsyhd.cinema.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getAllPlayingMovieUseCase: GetAllPlayingMovieUseCase,
    getPopularMovieUseCase: GetPopularMovieUseCase,
    getTopRatedMovieUseCase: GetTopRatedMovieUseCase
): ViewModel() {
    
    val topRatedMovie: LiveData<UiState<List<Movie>>> = getTopRatedMovieUseCase().map { resource ->
        val state: UiState<List<Movie>> = when (resource) {
            is Resource.Loading -> UiState.Loading
            is Resource.Success -> UiState.Success(resource.data ?: emptyList())
            is Resource.Error -> UiState.Error(resource.message ?: "error_unknown")
        }
        state
    }.asLiveData()

    val popularMovie: LiveData<UiState<List<Movie>>> = getPopularMovieUseCase().map { resource ->
        val state: UiState<List<Movie>> = when (resource) {
            is Resource.Loading -> UiState.Loading
            is Resource.Success -> UiState.Success(resource.data ?: emptyList())
            is Resource.Error -> UiState.Error(resource.message ?: "error_unknown")
        }
        state
    }.asLiveData()

    val playingMovie: LiveData<UiState<List<Movie>>> = getAllPlayingMovieUseCase().map { resource ->
        val state: UiState<List<Movie>> = when (resource) {
            is Resource.Loading -> UiState.Loading
            is Resource.Success -> UiState.Success(resource.data ?: emptyList())
            is Resource.Error -> UiState.Error(resource.message ?: "error_unknown")
        }
        state
    }.asLiveData()
}
