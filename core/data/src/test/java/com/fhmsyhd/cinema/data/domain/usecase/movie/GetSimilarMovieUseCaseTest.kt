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

class GetSimilarMovieUseCaseTest {

    @Mock
    private lateinit var movieRepository: IMovieRepository

    private lateinit var getSimilarMovieUseCase: GetSimilarMovieUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        getSimilarMovieUseCase = GetSimilarMovieUseCase(movieRepository)
    }

    @Test
    fun `invoke should call repository with movieId and return flow of Resource`() = runTest {
        val movieId = "123"
        val movies = listOf(
            Movie("1", "Similar", "Overview", "2024", "/path.jpg", 1.0, 8.0, 100, false)
        )
        val expectedResource = Resource.Success(movies)
        `when`(movieRepository.getAllSimilarMovie(movieId)).thenReturn(flowOf(expectedResource))

        val resultFlow = getSimilarMovieUseCase(movieId)

        resultFlow.collect { resource ->
            assertEquals(expectedResource, resource)
        }
        verify(movieRepository).getAllSimilarMovie(movieId)
    }
}
