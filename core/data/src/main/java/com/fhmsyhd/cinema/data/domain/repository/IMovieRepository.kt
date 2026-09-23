package com.fhmsyhd.cinema.data.domain.repository

import com.fhmsyhd.cinema.data.data.Resource
import com.fhmsyhd.cinema.data.domain.model.Movie
import com.fhmsyhd.cinema.data.domain.model.Review
import kotlinx.coroutines.flow.Flow

interface IMovieRepository {

    fun getAllPlayingMovie(): Flow<Resource<List<Movie>>>

    fun getPopularMovie(): Flow<Resource<List<Movie>>>

    fun getTopRatedMovie(): Flow<Resource<List<Movie>>>

    fun getAllSimilarMovie(movieId: String): Flow<Resource<List<Movie>>>

    fun getMovieReviews(movieId: String): Flow<Resource<List<Review>>>

    fun getFavoriteMovie(): Flow<List<Movie>>

    fun setFavoriteMovie(movie: Movie, state: Boolean)
}
