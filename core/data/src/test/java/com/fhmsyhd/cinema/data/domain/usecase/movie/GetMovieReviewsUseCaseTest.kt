package com.fhmsyhd.cinema.data.domain.usecase.movie

import com.fhmsyhd.cinema.data.data.Resource
import com.fhmsyhd.cinema.data.domain.model.Review
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

class GetMovieReviewsUseCaseTest {

    @Mock
    private lateinit var movieRepository: IMovieRepository

    private lateinit var getMovieReviewsUseCase: GetMovieReviewsUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        getMovieReviewsUseCase = GetMovieReviewsUseCase(movieRepository)
    }

    @Test
    fun `invoke should call repository with movieId and return flow of Resource`() = runTest {
        val movieId = "123"
        val reviews = listOf(Review("Author", "Content", 5.0))
        val expectedResource = Resource.Success(reviews)
        `when`(movieRepository.getMovieReviews(movieId)).thenReturn(flowOf(expectedResource))

        val resultFlow = getMovieReviewsUseCase(movieId)

        resultFlow.collect { resource ->
            assertEquals(expectedResource, resource)
        }
        verify(movieRepository).getMovieReviews(movieId)
    }
}
