package com.fhmsyhd.cinema.data.utils

object Constant {

    // Uri Api
    const val BASE_URL = "https://api.themoviedb.org/3/"
    const val END_POINT_POPULAR_MOVIE = "movie/popular"
    const val END_POINT_TOP_MOVIE = "movie/top_rated"
    const val END_POINT_PLAYING_MOVIE = "movie/now_playing"
    const val END_POINT_SIMILAR_MOVIE = "movie/{movie_id}/similar"
    const val END_POINT_REVIEW_MOVIE = "movie/{movie_id}/reviews"
    const val IMAGE_URL = "https://image.tmdb.org/t/p/w500"
    const val BACKDROP_IMAGE_URL = "https://image.tmdb.org/t/p/w780"

    // Entity
    const val DB_MOVIE = "Movie.db"
    const val TABLE_MOVIE = "movie"
    const val TABLE_SIMILAR_MOVIE = "similarMovie"
}
