package com.fhmsyhd.cinema.data.domain.usecase.movie

import com.fhmsyhd.cinema.data.data.Resource
import com.fhmsyhd.cinema.data.domain.model.Review
import com.fhmsyhd.cinema.data.domain.repository.IMovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMovieReviewsUseCase @Inject constructor(private val movieRepository: IMovieRepository) {
    operator fun invoke(movieId: String): Flow<Resource<List<Review>>> = movieRepository.getMovieReviews(movieId)
}
