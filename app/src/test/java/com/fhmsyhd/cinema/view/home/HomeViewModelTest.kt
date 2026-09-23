package com.fhmsyhd.cinema.view.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.fhmsyhd.cinema.data.data.Resource
import com.fhmsyhd.cinema.data.domain.model.Movie
import com.fhmsyhd.cinema.data.domain.usecase.movie.GetAllPlayingMovieUseCase
import com.fhmsyhd.cinema.data.domain.usecase.movie.GetPopularMovieUseCase
import com.fhmsyhd.cinema.data.domain.usecase.movie.GetTopRatedMovieUseCase
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
import org.mockito.Mockito.clearInvocations
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var getAllPlayingMovieUseCase: GetAllPlayingMovieUseCase
    @Mock
    private lateinit var getPopularMovieUseCase: GetPopularMovieUseCase
    @Mock
    private lateinit var getTopRatedMovieUseCase: GetTopRatedMovieUseCase

    @Mock
    private lateinit var observer: Observer<UiState<List<Movie>>>

    private lateinit var homeViewModel: HomeViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        MockitoAnnotations.openMocks(this)
        
        // Arrange default behaviors
        `when`(getAllPlayingMovieUseCase()).thenReturn(flowOf(Resource.Loading()))
        `when`(getPopularMovieUseCase()).thenReturn(flowOf(Resource.Loading()))
        `when`(getTopRatedMovieUseCase()).thenReturn(flowOf(Resource.Loading()))
        
        homeViewModel = HomeViewModel(getAllPlayingMovieUseCase, getPopularMovieUseCase, getTopRatedMovieUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when playingMovie is requested, it should return Success from UseCase`() = runTest {
        // Arrange
        val movies = listOf(
            Movie("1", "Title", "Overview", "2024", "/path.jpg", 1.0, 8.0, 100, false)
        )
        `when`(getAllPlayingMovieUseCase()).thenReturn(flowOf(Resource.Success(movies)))

        // Act
        homeViewModel = HomeViewModel(getAllPlayingMovieUseCase, getPopularMovieUseCase, getTopRatedMovieUseCase)
        homeViewModel.playingMovie.observeForever(observer)

        // Assert
        verify(observer).onChanged(UiState.Success(movies))
        homeViewModel.playingMovie.removeObserver(observer)
    }

    @Test
    fun `when topRatedMovie is requested, it should return Error from UseCase`() = runTest {
        // Arrange
        val errorMessage = "Network Error"
        `when`(getTopRatedMovieUseCase()).thenReturn(flowOf(Resource.Error(errorMessage)))

        // Act
        homeViewModel = HomeViewModel(getAllPlayingMovieUseCase, getPopularMovieUseCase, getTopRatedMovieUseCase)
        homeViewModel.topRatedMovie.observeForever(observer)

        // Assert
        verify(observer).onChanged(UiState.Error(errorMessage))
        homeViewModel.topRatedMovie.removeObserver(observer)
    }

    @Test
    fun `when popularMovie is requested, it should return Success from UseCase`() = runTest {
        // Arrange
        val movies = listOf(
            Movie("2", "Popular Title", "Overview", "2024", "/popular.jpg", 2.0, 9.0, 200, false)
        )
        `when`(getPopularMovieUseCase()).thenReturn(flowOf(Resource.Success(movies)))

        // Act
        homeViewModel = HomeViewModel(getAllPlayingMovieUseCase, getPopularMovieUseCase, getTopRatedMovieUseCase)
        homeViewModel.popularMovie.observeForever(observer)

        // Assert
        verify(observer).onChanged(UiState.Success(movies))
        homeViewModel.popularMovie.removeObserver(observer)
    }

    @Test
    fun `when refresh fails with cached movies, it should keep showing cached content`() = runTest {
        val cachedMovies = listOf(
            Movie("3", "Cached Title", "Overview", "2025", "/cached.jpg", 3.0, 8.5, 150, false)
        )
        `when`(getTopRatedMovieUseCase()).thenReturn(
            flowOf(Resource.Error("Network Error", cachedMovies))
        )

        homeViewModel = HomeViewModel(
            getAllPlayingMovieUseCase,
            getPopularMovieUseCase,
            getTopRatedMovieUseCase
        )
        homeViewModel.topRatedMovie.observeForever(observer)

        verify(observer).onChanged(UiState.Success(cachedMovies))
        homeViewModel.topRatedMovie.removeObserver(observer)
    }

    @Test
    fun `retry popular requests the use case again`() = runTest {
        homeViewModel.popularMovie.observeForever(observer)
        clearInvocations(getPopularMovieUseCase)

        homeViewModel.retryPopular()

        verify(getPopularMovieUseCase, times(1)).invoke()
        homeViewModel.popularMovie.removeObserver(observer)
    }
}
