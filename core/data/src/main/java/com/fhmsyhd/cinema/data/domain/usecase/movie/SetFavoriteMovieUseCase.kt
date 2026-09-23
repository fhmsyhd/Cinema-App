package com.fhmsyhd.cinema.data.domain.usecase.movie

import com.fhmsyhd.cinema.data.domain.model.Movie
import com.fhmsyhd.cinema.data.domain.repository.IMovieRepository
import javax.inject.Inject

class SetFavoriteMovieUseCase @Inject constructor(private val movieRepository: IMovieRepository) {
    operator fun invoke(movie: Movie, state: Boolean) = movieRepository.setFavoriteMovie(movie, state)
}
