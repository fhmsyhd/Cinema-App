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

class GetPopularMovieUseCaseTest {

    @Mock
    private lateinit var movieRepository: IMovieRepository

    private lateinit var getPopularMovieUseCase: GetPopularMovieUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        getPopularMovieUseCase = GetPopularMovieUseCase(movieRepository)
    }

    @Test
    fun `invoke should call repository and return flow of Resource`() = runTest {
        val movies = listOf(
            Movie("1", "Popular", "Overview", "2024", "/path.jpg", 10.0, 8.0, 100, false)
        )
        val expectedResource = Resource.Success(movies)
        `when`(movieRepository.getPopularMovie()).thenReturn(flowOf(expectedResource))

        val resultFlow = getPopularMovieUseCase()

        resultFlow.collect { resource ->
            assertEquals(expectedResource, resource)
        }
        verify(movieRepository).getPopularMovie()
    }
}
