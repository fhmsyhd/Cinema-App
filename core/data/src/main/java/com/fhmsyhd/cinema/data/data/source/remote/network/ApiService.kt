package com.fhmsyhd.cinema.data.data.source.remote.network

import com.fhmsyhd.cinema.data.data.source.remote.response.ListMovieResponse
import com.fhmsyhd.cinema.data.data.source.remote.response.ListReviewResponse
import com.fhmsyhd.cinema.data.utils.Constant.END_POINT_PLAYING_MOVIE
import com.fhmsyhd.cinema.data.utils.Constant.END_POINT_POPULAR_MOVIE
import com.fhmsyhd.cinema.data.utils.Constant.END_POINT_REVIEW_MOVIE
import com.fhmsyhd.cinema.data.utils.Constant.END_POINT_SIMILAR_MOVIE
import com.fhmsyhd.cinema.data.utils.Constant.END_POINT_TOP_MOVIE
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET(END_POINT_PLAYING_MOVIE)
    suspend fun getListMovieNowPlaying(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US"
    ): ListMovieResponse

    @GET(END_POINT_POPULAR_MOVIE)
    suspend fun getListPopularMovie(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US"
    ): ListMovieResponse

    @GET(END_POINT_TOP_MOVIE)
    suspend fun getListTopRatedMovie(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US"
    ): ListMovieResponse

    @GET(END_POINT_SIMILAR_MOVIE)
    suspend fun getListSimilarMovie(
        @Path("movie_id") movieId: String,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US",
        @Query("page") page: String = "1"
    ): ListMovieResponse

    @GET(END_POINT_REVIEW_MOVIE)
    suspend fun getListReviewMovie(
        @Path("movie_id") movieId: String,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US",
        @Query("page") page: String = "1"
    ): ListReviewResponse
}
