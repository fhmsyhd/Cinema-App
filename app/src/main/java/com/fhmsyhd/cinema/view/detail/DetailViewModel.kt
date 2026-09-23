package com.fhmsyhd.cinema.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.fhmsyhd.cinema.data.data.Resource
import com.fhmsyhd.cinema.data.domain.model.Movie
import com.fhmsyhd.cinema.data.domain.model.Review
import com.fhmsyhd.cinema.data.domain.usecase.movie.GetMovieReviewsUseCase
import com.fhmsyhd.cinema.data.domain.usecase.movie.GetSimilarMovieUseCase
import com.fhmsyhd.cinema.data.domain.usecase.movie.SetFavoriteMovieUseCase
import com.fhmsyhd.cinema.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getSimilarMovieUseCase: GetSimilarMovieUseCase,
    private val setFavoriteMovieUseCase: SetFavoriteMovieUseCase,
    private val getMovieReviewsUseCase: GetMovieReviewsUseCase
): ViewModel() {

    fun getSimilarMovie(movieId: String): LiveData<UiState<List<Movie>>> {
        return getSimilarMovieUseCase(movieId).map { resource ->
            val state: UiState<List<Movie>> = when (resource) {
                is Resource.Loading -> UiState.Loading
                is Resource.Success -> UiState.Success(resource.data ?: emptyList())
                is Resource.Error -> UiState.Error(resource.message ?: "error_unknown")
            }
            state
        }.asLiveData()
    }

    fun getMovieReviews(movieId: String): LiveData<UiState<List<Review>>> {
        return getMovieReviewsUseCase(movieId).map { resource ->
            val state: UiState<List<Review>> = when (resource) {
                is Resource.Loading -> UiState.Loading
                is Resource.Success -> UiState.Success(resource.data ?: emptyList())
                is Resource.Error -> UiState.Error(resource.message ?: "error_unknown")
            }
            state
        }.asLiveData()
    }

    fun setFavoriteMovie(movie: Movie, newStatus: Boolean) =
        setFavoriteMovieUseCase(movie, newStatus)
}
