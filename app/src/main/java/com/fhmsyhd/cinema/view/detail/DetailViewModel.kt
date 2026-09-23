package com.fhmsyhd.cinema.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.fhmsyhd.cinema.data.data.Resource
import com.fhmsyhd.cinema.data.domain.model.Movie
import com.fhmsyhd.cinema.data.domain.model.Review
import com.fhmsyhd.cinema.data.domain.usecase.movie.GetMovieReviewsUseCase
import com.fhmsyhd.cinema.data.domain.usecase.movie.GetSimilarMovieUseCase
import com.fhmsyhd.cinema.data.domain.usecase.movie.SetFavoriteMovieUseCase
import com.fhmsyhd.cinema.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getSimilarMovieUseCase: GetSimilarMovieUseCase,
    private val setFavoriteMovieUseCase: SetFavoriteMovieUseCase,
    private val getMovieReviewsUseCase: GetMovieReviewsUseCase
) : ViewModel() {

    private val similarMovieRequest = MutableLiveData<String>()
    private val reviewsRequest = MutableLiveData<String>()

    val similarMovies: LiveData<UiState<List<Movie>>> = similarMovieRequest.switchMap { movieId ->
        getSimilarMovieUseCase(movieId).mapMoviesToUiState().asLiveData()
    }

    val reviews: LiveData<UiState<List<Review>>> = reviewsRequest.switchMap { movieId ->
        getMovieReviewsUseCase(movieId).mapReviewsToUiState().asLiveData()
    }

    fun load(movieId: String) {
        if (similarMovieRequest.value != movieId) {
            similarMovieRequest.value = movieId
        }
        if (reviewsRequest.value != movieId) {
            reviewsRequest.value = movieId
        }
    }

    fun retrySimilarMovies() {
        similarMovieRequest.value = similarMovieRequest.value
    }

    fun retryReviews() {
        reviewsRequest.value = reviewsRequest.value
    }

    fun setFavoriteMovie(movie: Movie, newStatus: Boolean) =
        setFavoriteMovieUseCase(movie, newStatus)

    private fun Flow<Resource<List<Movie>>>.mapMoviesToUiState() = map { resource ->
        val cachedData = resource.data.orEmpty()
        when (resource) {
            is Resource.Loading -> cachedData.toSuccessOr { UiState.Loading }
            is Resource.Success -> UiState.Success(cachedData)
            is Resource.Error -> cachedData.toSuccessOr {
                UiState.Error(resource.message ?: "error_unknown")
            }
        }
    }

    private fun Flow<Resource<List<Review>>>.mapReviewsToUiState() = map { resource ->
        when (resource) {
            is Resource.Loading -> UiState.Loading
            is Resource.Success -> UiState.Success(resource.data.orEmpty())
            is Resource.Error -> UiState.Error(resource.message ?: "error_unknown")
        }
    }

    private inline fun List<Movie>.toSuccessOr(
        fallback: () -> UiState<List<Movie>>
    ): UiState<List<Movie>> = if (isNotEmpty()) UiState.Success(this) else fallback()
}
