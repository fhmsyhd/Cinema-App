package com.fhmsyhd.cinema.data.domain.usecase.movie

import com.fhmsyhd.cinema.data.data.Resource
import com.fhmsyhd.cinema.data.domain.model.Movie
import com.fhmsyhd.cinema.data.domain.repository.IMovieRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify

class GetAllPlayingMovieUseCaseTest {

    @Mock
    private lateinit var movieRepository: IMovieRepository

    private lateinit var getAllPlayingMovieUseCase: GetAllPlayingMovieUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        getAllPlayingMovieUseCase = GetAllPlayingMovieUseCase(movieRepository)
    }

    @Test
    fun `invoke should call repository and return flow of Resource`() = runTest {
        // Arrange
        val movies = listOf(
            Movie(
                movieId = "1",
                title = "Test Movie",
                overview = "Overview",
                releaseDate = "2024",
                posterPath = "/path.jpg",
                popularity = 1.0,
                voteAverage = 8.0,
                voteCount = 100,
                isFavorite = false
            )
        )
        val expectedResource = Resource.Success(movies)
        `when`(movieRepository.getAllPlayingMovie()).thenReturn(flowOf(expectedResource))

        // Act
        val resultFlow = getAllPlayingMovieUseCase()

        // Assert
        resultFlow.collect { resource ->
            assertEquals(expectedResource, resource)
            assertEquals(movies, resource.data)
        }
        verify(movieRepository).getAllPlayingMovie()
    }
}
