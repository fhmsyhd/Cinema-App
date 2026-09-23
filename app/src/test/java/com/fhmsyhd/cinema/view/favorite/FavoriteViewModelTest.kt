package com.fhmsyhd.cinema.view.favorite

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.fhmsyhd.cinema.data.domain.model.Movie
import com.fhmsyhd.cinema.data.domain.usecase.movie.GetFavoriteMovieUseCase
import com.fhmsyhd.cinema.data.domain.usecase.movie.SetFavoriteMovieUseCase
import com.fhmsyhd.cinema.utils.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class FavoriteViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var getFavoriteMovieUseCase: GetFavoriteMovieUseCase
    @Mock
    private lateinit var setFavoriteMovieUseCase: SetFavoriteMovieUseCase

    @Mock
    private lateinit var observer: Observer<UiState<List<Movie>>>

    private lateinit var favoriteViewModel: FavoriteViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        MockitoAnnotations.openMocks(this)
        
        // Initial setup
        `when`(getFavoriteMovieUseCase()).thenReturn(flowOf(emptyList()))
        favoriteViewModel = FavoriteViewModel(getFavoriteMovieUseCase, setFavoriteMovieUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when favoriteMovie is not empty, it should return Success`() = runTest {
        // Arrange
        val movies = listOf(
            Movie("1", "Title", "Overview", "2024", "/path.jpg", 1.0, 8.0, 100, true)
        )
        `when`(getFavoriteMovieUseCase()).thenReturn(flowOf(movies))

        // Act
        favoriteViewModel = FavoriteViewModel(getFavoriteMovieUseCase, setFavoriteMovieUseCase)
        favoriteViewModel.favoriteMovie.observeForever(observer)

        // Assert
        verify(observer).onChanged(UiState.Success(movies))
        favoriteViewModel.favoriteMovie.removeObserver(observer)
    }

    @Test
    fun `when favoriteMovie is empty, it should return Success with empty list`() = runTest {
        // Arrange
        `when`(getFavoriteMovieUseCase()).thenReturn(flowOf(emptyList()))

        // Act
        favoriteViewModel = FavoriteViewModel(getFavoriteMovieUseCase, setFavoriteMovieUseCase)
        favoriteViewModel.favoriteMovie.observeForever(observer)

        // Assert
        verify(observer).onChanged(UiState.Success(emptyList()))
        favoriteViewModel.favoriteMovie.removeObserver(observer)
    }

    @Test
    fun `removeFavorite should clear favorite status`() {
        val movie = Movie("1", "Title", "Overview", "2024", "/path.jpg", 1.0, 8.0, 100, true)

        favoriteViewModel.removeFavorite(movie)

        verify(setFavoriteMovieUseCase).invoke(movie, false)
    }

    @Test
    fun `restoreFavorite should restore favorite status`() {
        val movie = Movie("1", "Title", "Overview", "2024", "/path.jpg", 1.0, 8.0, 100, true)

        favoriteViewModel.restoreFavorite(movie)

        verify(setFavoriteMovieUseCase).invoke(movie, true)
    }
}
