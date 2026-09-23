package com.fhmsyhd.cinema.data.data

import com.fhmsyhd.cinema.data.data.source.local.LocalDataSource
import com.fhmsyhd.cinema.data.data.source.local.entity.MovieCategoryEntity
import com.fhmsyhd.cinema.data.data.source.remote.RemoteDataSource
import com.fhmsyhd.cinema.data.data.source.remote.network.ApiResponse
import com.fhmsyhd.cinema.data.data.source.remote.response.MovieResponse
import com.fhmsyhd.cinema.data.domain.model.Movie
import com.fhmsyhd.cinema.data.domain.model.Review
import com.fhmsyhd.cinema.data.domain.repository.IMovieRepository
import com.fhmsyhd.cinema.data.utils.AppExecutors
import com.fhmsyhd.cinema.data.utils.DataMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val appExecutors: AppExecutors
): IMovieRepository {

    override fun getAllPlayingMovie(): Flow<Resource<List<Movie>>> =
        object : NetworkBoundResource<List<Movie>, List<MovieResponse>>(){
            override fun loadFromDB(): Flow<List<Movie>> {
                return localDataSource.getAllMovie("now_playing").map {
                    DataMapper.mapEntitiesToDomain(it)
                }
            }

            override fun shouldFetch(data: List<Movie>?): Boolean = true

            override suspend fun createCall(): Flow<ApiResponse<List<MovieResponse>>> =
                remoteDataSource.getAllPlayingMovie()

            override suspend fun saveCallResult(data: List<MovieResponse>) {
                val movieList = DataMapper.mapResponsesToEntities(data)
                val summaryList = DataMapper.mapResponsesToSummaries(data)
                localDataSource.insertOrUpdateMovies(movieList, summaryList)
                
                val categories = data.map { MovieCategoryEntity(it.id, "now_playing") }
                localDataSource.insertMovieCategories(categories)
            }
        }.asFlow()

    override fun getPopularMovie(): Flow<Resource<List<Movie>>> =
        object : NetworkBoundResource<List<Movie>, List<MovieResponse>>(){
            override fun loadFromDB(): Flow<List<Movie>> {
                return localDataSource.getAllMovie("popular").map {
                    DataMapper.mapEntitiesToDomain(it)
                }
            }

            override fun shouldFetch(data: List<Movie>?): Boolean = true

            override suspend fun createCall(): Flow<ApiResponse<List<MovieResponse>>> =
                remoteDataSource.getPopularMovie()

            override suspend fun saveCallResult(data: List<MovieResponse>) {
                val movieList = DataMapper.mapResponsesToEntities(data)
                val summaryList = DataMapper.mapResponsesToSummaries(data)
                localDataSource.insertOrUpdateMovies(movieList, summaryList)

                val categories = data.map { MovieCategoryEntity(it.id, "popular") }
                localDataSource.insertMovieCategories(categories)
            }
        }.asFlow()

    override fun getTopRatedMovie(): Flow<Resource<List<Movie>>> =
        object : NetworkBoundResource<List<Movie>, List<MovieResponse>>(){
            override fun loadFromDB(): Flow<List<Movie>> {
                return localDataSource.getAllMovie("top_rated").map {
                    DataMapper.mapEntitiesToDomain(it)
                }
            }

            override fun shouldFetch(data: List<Movie>?): Boolean = true

            override suspend fun createCall(): Flow<ApiResponse<List<MovieResponse>>> =
                remoteDataSource.getTopRatedMovie()

            override suspend fun saveCallResult(data: List<MovieResponse>) {
                val movieList = DataMapper.mapResponsesToEntities(data)
                val summaryList = DataMapper.mapResponsesToSummaries(data)
                localDataSource.insertOrUpdateMovies(movieList, summaryList)

                val categories = data.map { MovieCategoryEntity(it.id, "top_rated") }
                localDataSource.insertMovieCategories(categories)
            }
        }.asFlow()

    override fun getAllSimilarMovie(movieId: String): Flow<Resource<List<Movie>>> =
        object : NetworkBoundResource<List<Movie>, List<MovieResponse>>(){
            override fun loadFromDB(): Flow<List<Movie>> {
                return localDataSource.getAllSimilarMovie(movieId).map {
                    DataMapper.mapSimilarEntitiesToDomain(it)
                }
            }

            override fun shouldFetch(data: List<Movie>?): Boolean = true

            override suspend fun createCall(): Flow<ApiResponse<List<MovieResponse>>> =
                remoteDataSource.getAllSimilarMovie(movieId)

            override suspend fun saveCallResult(data: List<MovieResponse>) {
                localDataSource.deleteSimilarMovie(movieId)
                val movieList = DataMapper.mapSimilarResponsesToEntities(movieId, data)
                localDataSource.insertSimilarMovie(movieList)
            }
        }.asFlow()

    override fun getMovieReviews(movieId: String): Flow<Resource<List<Review>>> = flow {
        emit(Resource.Loading())
        when (val apiResponse = remoteDataSource.getMovieReviews(movieId).first()) {
            is ApiResponse.Success -> {
                emit(Resource.Success(DataMapper.mapReviewResponsesToDomain(apiResponse.data)))
            }
            is ApiResponse.Empty -> {
                emit(Resource.Success(emptyList()))
            }
            is ApiResponse.Error -> {
                emit(Resource.Error(apiResponse.errorMessage))
            }
        }
    }

    override fun getFavoriteMovie(): Flow<List<Movie>> {
        return localDataSource.getFavoriteMovie().map {
            DataMapper.mapEntitiesToDomain(it)
        }
    }

    override fun setFavoriteMovie(movie: Movie, state: Boolean) {
        appExecutors.diskIO().execute {
            kotlinx.coroutines.runBlocking {
                localDataSource.setFavoriteMovie(movie, state)
            }
        }
    }
}
