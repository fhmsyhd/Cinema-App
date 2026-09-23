package com.fhmsyhd.cinema.view.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.fhmsyhd.cinema.data.data.Resource
import com.fhmsyhd.cinema.data.domain.model.Movie
import com.fhmsyhd.cinema.data.domain.model.Review
import com.fhmsyhd.cinema.data.domain.usecase.movie.GetMovieReviewsUseCase
import com.fhmsyhd.cinema.data.domain.usecase.movie.GetSimilarMovieUseCase
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
class DetailViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var getSimilarMovieUseCase: GetSimilarMovieUseCase
    @Mock
    private lateinit var setFavoriteMovieUseCase: SetFavoriteMovieUseCase
    @Mock
    private lateinit var getMovieReviewsUseCase: GetMovieReviewsUseCase

    @Mock
    private lateinit var movieObserver: Observer<UiState<List<Movie>>>
    @Mock
    private lateinit var reviewObserver: Observer<UiState<List<Review>>>

    private lateinit var detailViewModel: DetailViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        MockitoAnnotations.openMocks(this)
        detailViewModel = DetailViewModel(getSimilarMovieUseCase, setFavoriteMovieUseCase, getMovieReviewsUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getSimilarMovie should return Success from UseCase`() = runTest {
        // Arrange
        val movieId = "1"
        val movies = listOf(
            Movie(movieId, "Similar", "Overview", "2024", "/path.jpg", 1.0, 8.0, 100, false)
        )
        `when`(getSimilarMovieUseCase(movieId)).thenReturn(flowOf(Resource.Success(movies)))

        // Act
        val result = detailViewModel.getSimilarMovie(movieId)
        result.observeForever(movieObserver)

        // Assert
        verify(movieObserver).onChanged(UiState.Success(movies))
        result.removeObserver(movieObserver)
    }

    @Test
    fun `getMovieReviews should return Success from UseCase`() = runTest {
        // Arrange
        val movieId = "1"
        val reviews = listOf(Review("Author", "Content", 10.0))
        `when`(getMovieReviewsUseCase(movieId)).thenReturn(flowOf(Resource.Success(reviews)))

        // Act
        val result = detailViewModel.getMovieReviews(movieId)
        result.observeForever(reviewObserver)

        // Assert
        verify(reviewObserver).onChanged(UiState.Success(reviews))
        result.removeObserver(reviewObserver)
    }

    @Test
    fun `setFavoriteMovie should call UseCase`() {
        // Arrange
        val movie = Movie("1", "Title", "Overview", "2024", "/path.jpg", 1.0, 8.0, 100, false)
        val newStatus = true

        // Act
        detailViewModel.setFavoriteMovie(movie, newStatus)

        // Assert
        verify(setFavoriteMovieUseCase).invoke(movie, newStatus)
    }
}
