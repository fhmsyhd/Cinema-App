package com.fhmsyhd.cinema.data.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "movie_category",
    primaryKeys = ["movieId", "category"],
    foreignKeys = [
        ForeignKey(
            entity = MovieEntity::class,
            parentColumns = ["movieId"],
            childColumns = ["movieId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MovieCategoryEntity(
    @ColumnInfo(name = "movieId")
    val movieId: String,
    @ColumnInfo(name = "category")
    val category: String
)
