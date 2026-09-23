package com.fhmsyhd.cinema.data.domain.usecase.movie

import com.fhmsyhd.cinema.data.domain.model.Movie
import com.fhmsyhd.cinema.data.domain.repository.IMovieRepository
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify

class SetFavoriteMovieUseCaseTest {

    @Mock
    private lateinit var movieRepository: IMovieRepository

    private lateinit var setFavoriteMovieUseCase: SetFavoriteMovieUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        setFavoriteMovieUseCase = SetFavoriteMovieUseCase(movieRepository)
    }

    @Test
    fun `invoke should call repository to set favorite status`() {
        val movie = Movie("1", "Title", "Overview", "2024", "/path.jpg", 1.0, 8.0, 100, false)
        val newStatus = true

        setFavoriteMovieUseCase(movie, newStatus)

        verify(movieRepository).setFavoriteMovie(movie, newStatus)
    }
}
