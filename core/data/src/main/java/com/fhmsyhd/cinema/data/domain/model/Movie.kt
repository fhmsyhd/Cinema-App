package com.fhmsyhd.cinema.data.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Movie(
    val movieId: String,
    val title: String,
    val overview: String,
    val releaseDate: String,
    val posterPath: String?,
    val popularity: Double,
    val voteAverage: Double,
    val voteCount: Int,
    val isFavorite: Boolean
) : Parcelable