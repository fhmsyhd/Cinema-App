package com.fhmsyhd.cinema.data.utils

import com.fhmsyhd.cinema.data.data.source.local.entity.MovieEntity
import com.fhmsyhd.cinema.data.data.source.local.entity.MovieSummaryEntity
import com.fhmsyhd.cinema.data.data.source.local.entity.SimilarMovieEntity
import com.fhmsyhd.cinema.data.data.source.remote.response.MovieResponse
import com.fhmsyhd.cinema.data.data.source.remote.response.ReviewResponse
import com.fhmsyhd.cinema.data.domain.model.Movie
import com.fhmsyhd.cinema.data.domain.model.Review

object DataMapper {
    fun mapResponsesToEntities(input: List<MovieResponse>): List<MovieEntity> {
        return input.map {
            MovieEntity(
                movieId = it.id,
                title = it.title,
                overview = it.overview,
                releaseDate = it.releaseDate,
                posterPath = it.posterPath,
                popularity = it.popularity,
                voteAverage = it.voteAverage,
                voteCount = it.voteCount,
                isFavorite = false,
                backdropPath = it.backdropPath
            )
        }
    }

    fun mapResponsesToSummaries(input: List<MovieResponse>): List<MovieSummaryEntity> {
        return input.map {
            MovieSummaryEntity(
                movieId = it.id,
                title = it.title,
                overview = it.overview,
                releaseDate = it.releaseDate,
                posterPath = it.posterPath,
                popularity = it.popularity,
                voteAverage = it.voteAverage,
                voteCount = it.voteCount,
                backdropPath = it.backdropPath
            )
        }
    }

    fun mapEntitiesToDomain(input: List<MovieEntity>): List<Movie> =
        input.map {
            Movie(
                movieId = it.movieId,
                title = it.title,
                overview = it.overview,
                releaseDate = it.releaseDate,
                posterPath = it.posterPath,
                popularity = it.popularity,
                voteAverage = it.voteAverage,
                voteCount = it.voteCount,
                isFavorite = it.isFavorite,
                backdropPath = it.backdropPath
            )
        }

    fun mapSimilarResponsesToEntities(
        sourceMovieId: String,
        input: List<MovieResponse>
    ): List<SimilarMovieEntity> {
        return input.map {
            SimilarMovieEntity(
                sourceMovieId = sourceMovieId,
                movieId = it.id,
                title = it.title,
                overview = it.overview,
                releaseDate = it.releaseDate,
                posterPath = it.posterPath,
                popularity = it.popularity,
                voteAverage = it.voteAverage,
                voteCount = it.voteCount,
                isFavorite = false,
                backdropPath = it.backdropPath
            )
        }
    }

    fun mapSimilarEntitiesToDomain(input: List<SimilarMovieEntity>): List<Movie> =
        input.map {
            Movie(
                movieId = it.movieId,
                title = it.title,
                overview = it.overview,
                releaseDate = it.releaseDate,
                posterPath = it.posterPath,
                popularity = it.popularity,
                voteAverage = it.voteAverage,
                voteCount = it.voteCount,
                isFavorite = it.isFavorite,
                backdropPath = it.backdropPath
            )
        }

    fun mapDomainToEntity(input: Movie) = MovieEntity(
        movieId = input.movieId,
        title = input.title,
        overview = input.overview,
        releaseDate = input.releaseDate,
        posterPath = input.posterPath,
        popularity = input.popularity,
        voteAverage = input.voteAverage,
        voteCount = input.voteCount,
        isFavorite = input.isFavorite,
        backdropPath = input.backdropPath
    )

    fun mapReviewResponsesToDomain(input: List<ReviewResponse>): List<Review> =
        input.map {
            Review(
                author = it.author,
                content = it.content,
                rating = it.authorDetails.rating
            )
        }
}
