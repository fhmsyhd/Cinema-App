package com.fhmsyhd.cinema.data.domain.usecase.movie

import com.fhmsyhd.cinema.data.data.Resource
import com.fhmsyhd.cinema.data.domain.model.Movie
import com.fhmsyhd.cinema.data.domain.repository.IMovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTopRatedMovieUseCase @Inject constructor(private val movieRepository: IMovieRepository) {
    operator fun invoke(): Flow<Resource<List<Movie>>> = movieRepository.getTopRatedMovie()
}
