package com.fhmsyhd.cinema.view.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
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
    private val getAllPlayingMovieUseCase: GetAllPlayingMovieUseCase,
    private val getPopularMovieUseCase: GetPopularMovieUseCase,
    private val getTopRatedMovieUseCase: GetTopRatedMovieUseCase
): ViewModel() {

    private val topRatedRefresh = MutableLiveData(Unit)
    private val popularRefresh = MutableLiveData(Unit)
    private val playingRefresh = MutableLiveData(Unit)

    val topRatedMovie: LiveData<UiState<List<Movie>>> = topRatedRefresh.switchMap {
        getTopRatedMovieUseCase().mapToUiState().asLiveData()
    }

    val popularMovie: LiveData<UiState<List<Movie>>> = popularRefresh.switchMap {
        getPopularMovieUseCase().mapToUiState().asLiveData()
    }

    val playingMovie: LiveData<UiState<List<Movie>>> = playingRefresh.switchMap {
        getAllPlayingMovieUseCase().mapToUiState().asLiveData()
    }

    fun retryTopRated() {
        topRatedRefresh.value = Unit
    }

    fun retryPopular() {
        popularRefresh.value = Unit
    }

    fun retryPlaying() {
        playingRefresh.value = Unit
    }

    private fun kotlinx.coroutines.flow.Flow<Resource<List<Movie>>>.mapToUiState() = map { resource ->
        val cachedData = resource.data.orEmpty()
        when (resource) {
            is Resource.Loading -> cachedData.toSuccessOr { UiState.Loading }
            is Resource.Success -> UiState.Success(cachedData)
            is Resource.Error -> cachedData.toSuccessOr {
                UiState.Error(resource.message ?: "error_unknown")
            }
        }
    }

    private inline fun List<Movie>.toSuccessOr(
        fallback: () -> UiState<List<Movie>>
    ): UiState<List<Movie>> = if (isNotEmpty()) UiState.Success(this) else fallback()
}
