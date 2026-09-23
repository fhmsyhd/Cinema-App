package com.fhmsyhd.cinema.data.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class ListMovieResponse (
    @field:SerializedName("page")
    val page: String,

    @field:SerializedName("total_pages")
    val description: Int,

    @field:SerializedName("results")
    val result: List<MovieResponse>
)