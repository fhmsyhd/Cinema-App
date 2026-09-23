package com.fhmsyhd.cinema.data.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class ListReviewResponse(
    @field:SerializedName("id")
    val id: Int,
    @field:SerializedName("results")
    val results: List<ReviewResponse>
)

data class ReviewResponse(
    @field:SerializedName("author")
    val author: String,
    @field:SerializedName("content")
    val content: String,
    @field:SerializedName("author_details")
    val authorDetails: AuthorDetailsResponse
)

data class AuthorDetailsResponse(
    @field:SerializedName("rating")
    val rating: Double?
)
