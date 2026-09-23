package com.fhmsyhd.cinema.data.data.source.local

import com.fhmsyhd.cinema.data.data.source.local.entity.MovieCategoryEntity
import com.fhmsyhd.cinema.data.data.source.local.entity.MovieEntity
import com.fhmsyhd.cinema.data.data.source.local.entity.MovieSummaryEntity
import com.fhmsyhd.cinema.data.data.source.local.entity.SimilarMovieEntity
import com.fhmsyhd.cinema.data.data.source.local.room.MovieDao
import com.fhmsyhd.cinema.data.domain.model.Movie
import com.fhmsyhd.cinema.data.utils.DataMapper
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataSource @Inject constructor(private val movieDao: MovieDao) {

    fun getAllMovie(category: String): Flow<List<MovieEntity>> = movieDao.getAllMovieByCategory(category)

    fun getAllSimilarMovie(sourceMovieId: String): Flow<List<SimilarMovieEntity>> =
        movieDao.getAllSimilarMovie(sourceMovieId)

    fun getFavoriteMovie(): Flow<List<MovieEntity>> = movieDao.getFavoriteMovie()

    suspend fun insertOrUpdateMovies(movieList: List<MovieEntity>, summaryList: List<MovieSummaryEntity>) = 
        movieDao.insertOrUpdateMovies(movieList, summaryList)

    suspend fun insertMovieCategories(categories: List<MovieCategoryEntity>) = movieDao.insertMovieCategories(categories)

    suspend fun deleteCategoriesByType(category: String) = movieDao.deleteCategoriesByType(category)

    suspend fun insertSimilarMovie(movieList: List<SimilarMovieEntity>) = movieDao.insertSimilarMovie(movieList)

    suspend fun deleteSimilarMovie(sourceMovieId: String) = movieDao.deleteSimilarMovie(sourceMovieId)

    suspend fun setFavoriteMovie(movie: Movie, newState: Boolean) {
        // Jika film belum ada di tabel utama (misal baru dari similar movie), masukkan dulu
        if (!movieDao.isMovieExists(movie.movieId)) {
            val entity = DataMapper.mapDomainToEntity(movie)
            movieDao.insertMovies(listOf(entity))
        }
        movieDao.updateFavoriteStatus(movie.movieId, newState)
    }
}
