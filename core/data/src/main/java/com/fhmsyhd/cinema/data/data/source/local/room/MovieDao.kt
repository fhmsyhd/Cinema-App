package com.fhmsyhd.cinema.data.data.source.local.room

import androidx.room.*
import com.fhmsyhd.cinema.data.data.source.local.entity.MovieCategoryEntity
import com.fhmsyhd.cinema.data.data.source.local.entity.MovieEntity
import com.fhmsyhd.cinema.data.data.source.local.entity.MovieSummaryEntity
import com.fhmsyhd.cinema.data.data.source.local.entity.SimilarMovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Query("SELECT movie.* FROM movie INNER JOIN movie_category ON movie.movieId = movie_category.movieId WHERE movie_category.category = :category")
    fun getAllMovieByCategory(category: String): Flow<List<MovieEntity>>

    @Query("SELECT * FROM similarMovie WHERE sourceMovieId = :sourceMovieId")
    fun getAllSimilarMovie(sourceMovieId: String): Flow<List<SimilarMovieEntity>>

    @Query("SELECT * FROM movie where isFavorite = 1")
    fun getFavoriteMovie(): Flow<List<MovieEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMovies(movies: List<MovieEntity>)

    @Update(entity = MovieEntity::class)
    suspend fun updateMovieSummary(movies: List<MovieSummaryEntity>)

    @Transaction
    suspend fun insertOrUpdateMovies(movies: List<MovieEntity>, summaries: List<MovieSummaryEntity>) {
        insertMovies(movies)
        updateMovieSummary(summaries)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovieCategories(categories: List<MovieCategoryEntity>)

    @Query("DELETE FROM movie_category WHERE category = :category")
    suspend fun deleteCategoriesByType(category: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSimilarMovie(movie: List<SimilarMovieEntity>)

    @Query("DELETE FROM similarMovie WHERE sourceMovieId = :sourceMovieId")
    suspend fun deleteSimilarMovie(sourceMovieId: String)

    @Transaction
    suspend fun updateFavoriteStatus(movieId: String, isFavorite: Boolean) {
        updateMovieFavorite(movieId, isFavorite)
        updateSimilarMovieFavorite(movieId, isFavorite)
    }

    @Query("UPDATE movie SET isFavorite = :isFavorite WHERE movieId = :movieId")
    suspend fun updateMovieFavorite(movieId: String, isFavorite: Boolean)

    @Query("UPDATE similarMovie SET isFavorite = :isFavorite WHERE movieId = :movieId")
    suspend fun updateSimilarMovieFavorite(movieId: String, isFavorite: Boolean)

    @Query("SELECT EXISTS(SELECT 1 FROM movie WHERE movieId = :movieId)")
    suspend fun isMovieExists(movieId: String): Boolean
}
