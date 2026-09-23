package com.fhmsyhd.cinema.data.utils

import com.fhmsyhd.cinema.data.data.source.local.entity.MovieEntity
import com.fhmsyhd.cinema.data.data.source.remote.response.MovieResponse
import org.junit.Assert.assertEquals
import org.junit.Test

class DataMapperTest {

    @Test
    fun `mapResponsesToEntities should return correct entities`() {
        val responses = listOf(
            MovieResponse(
                id = "1",
                title = "Title 1",
                overview = "Overview 1",
                releaseDate = "2024-01-01",
                posterPath = "/path1.jpg",
                popularity = 1.0,
                voteAverage = 7.5,
                voteCount = 100
            )
        )

        val entities = DataMapper.mapResponsesToEntities(responses)

        assertEquals(1, entities.size)
        assertEquals("1", entities[0].movieId)
        assertEquals("Title 1", entities[0].title)
        assertEquals("/path1.jpg", entities[0].posterPath)
    }

    @Test
    fun `mapEntitiesToDomain should return correct domain models`() {
        val entities = listOf(
            MovieEntity(
                movieId = "1",
                title = "Title 1",
                overview = "Overview 1",
                releaseDate = "2024-01-01",
                posterPath = "/path1.jpg",
                popularity = 1.0,
                voteAverage = 7.5,
                voteCount = 100,
                isFavorite = true
            )
        )

        val domain = DataMapper.mapEntitiesToDomain(entities)

        assertEquals(1, domain.size)
        assertEquals("1", domain[0].movieId)
        assertEquals(true, domain[0].isFavorite)
        assertEquals("/path1.jpg", domain[0].posterPath)
    }

    @Test
    fun `mapResponsesToEntities with null posterPath should handle it correctly`() {
        val responses = listOf(
            MovieResponse(
                id = "1",
                title = "Title 1",
                overview = "Overview 1",
                releaseDate = "2024-01-01",
                posterPath = null,
                popularity = 1.0,
                voteAverage = 7.5,
                voteCount = 100
            )
        )

        val entities = DataMapper.mapResponsesToEntities(responses)

        assertEquals(null, entities[0].posterPath)
    }

    @Test
    fun `mapSimilarResponsesToEntities should keep the source movie id`() {
        val responses = listOf(
            MovieResponse(
                id = "similar-1",
                title = "Similar Movie",
                overview = "Overview",
                releaseDate = "2024-01-01",
                posterPath = "/poster.jpg",
                popularity = 1.0,
                voteAverage = 7.5,
                voteCount = 100
            )
        )

        val entities = DataMapper.mapSimilarResponsesToEntities("source-1", responses)

        assertEquals("source-1", entities.single().sourceMovieId)
        assertEquals("similar-1", entities.single().movieId)
    }
}
