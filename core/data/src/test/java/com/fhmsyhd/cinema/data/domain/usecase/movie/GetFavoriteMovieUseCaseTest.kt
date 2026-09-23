package com.fhmsyhd.cinema.data.domain.usecase.movie

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

class GetFavoriteMovieUseCaseTest {

    @Mock
    private lateinit var movieRepository: IMovieRepository

    private lateinit var getFavoriteMovieUseCase: GetFavoriteMovieUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        getFavoriteMovieUseCase = GetFavoriteMovieUseCase(movieRepository)
    }

    @Test
    fun `invoke should call repository and return flow of favorite movies`() = runTest {
        val movies = listOf(
            Movie("1", "Favorite", "Overview", "2024", "/path.jpg", 1.0, 8.0, 100, true)
        )
        `when`(movieRepository.getFavoriteMovie()).thenReturn(flowOf(movies))

        val resultFlow = getFavoriteMovieUseCase()

        resultFlow.collect { result ->
            assertEquals(movies, result)
        }
        verify(movieRepository).getFavoriteMovie()
    }
}
