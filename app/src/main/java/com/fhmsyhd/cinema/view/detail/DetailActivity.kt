package com.fhmsyhd.cinema.view.detail

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.fhmsyhd.cinema.R
import com.fhmsyhd.cinema.adapter.MovieAdapter
import com.fhmsyhd.cinema.adapter.ReviewAdapter
import com.fhmsyhd.cinema.data.domain.model.Movie
import com.fhmsyhd.cinema.data.domain.model.Review
import com.fhmsyhd.cinema.data.utils.Constant.IMAGE_URL
import com.fhmsyhd.cinema.databinding.ActivityDetailBinding
import com.fhmsyhd.cinema.utils.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_DATA_MOVIE = "extra_data_movie"
    }

    private lateinit var binding: ActivityDetailBinding
    private val detailViewModel: DetailViewModel by viewModels()

    private val movieAdapter = MovieAdapter()
    private val reviewAdapter = ReviewAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val detailMovie = intent.getParcelableExtra<Movie>(EXTRA_DATA_MOVIE)

        setupUI()
        setupRecyclerViews()
        showDetailMovie(detailMovie)
        observeViewModel(detailMovie?.movieId)
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun setupRecyclerViews() {
        movieAdapter.onItemClick = { selectedData ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra(EXTRA_DATA_MOVIE, selectedData)
            startActivity(intent)
        }

        with(binding.rvMoviePlaying) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            adapter = movieAdapter
        }

        with(binding.rvMovieReview) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            adapter = reviewAdapter
        }
    }

    private fun observeViewModel(movieId: String?) {
        movieId?.let { id ->
            detailViewModel.getSimilarMovie(id).observe(this) { state ->
                handleSimilarMoviesState(state)
            }

            detailViewModel.getMovieReviews(id).observe(this) { state ->
                handleReviewsState(state)
            }
        }
    }

    private fun handleSimilarMoviesState(state: UiState<List<Movie>>) {
        when (state) {
            is UiState.Loading -> {
                binding.loadingSimilar.root.visibility = View.VISIBLE
                binding.rvMoviePlaying.visibility = View.GONE
            }
            is UiState.Success -> {
                binding.loadingSimilar.root.visibility = View.GONE
                if (state.data.isNotEmpty()) {
                    binding.layoutSimilar.visibility = View.VISIBLE
                    binding.rvMoviePlaying.visibility = View.VISIBLE
                    movieAdapter.submitList(state.data)
                } else {
                    binding.layoutSimilar.visibility = View.GONE
                }
            }
            is UiState.Error -> {
                binding.loadingSimilar.root.visibility = View.GONE
                binding.layoutSimilar.visibility = View.GONE
            }
        }
    }

    private fun handleReviewsState(state: UiState<List<Review>>) {
        when (state) {
            is UiState.Loading -> {
                binding.loadingReview.root.visibility = View.VISIBLE
                binding.rvMovieReview.visibility = View.GONE
            }
            is UiState.Success -> {
                binding.loadingReview.root.visibility = View.GONE
                if (state.data.isNotEmpty()) {
                    binding.layoutReview.visibility = View.VISIBLE
                    binding.rvMovieReview.visibility = View.VISIBLE
                    reviewAdapter.submitList(state.data)
                } else {
                    binding.layoutReview.visibility = View.GONE
                }
            }
            is UiState.Error -> {
                binding.loadingReview.root.visibility = View.GONE
                binding.layoutReview.visibility = View.GONE
            }
        }
    }

    @SuppressLint("SetTextI18n", "StringFormatInvalid")
    private fun showDetailMovie(detailMovie: Movie?) {
        detailMovie?.let { movie ->
            Glide.with(this)
                .load(movie.posterPath?.takeIf { it.isNotEmpty() }?.let { IMAGE_URL + it })
                .into(binding.imgMovie)

            binding.tvTitle.text = movie.title
            binding.tvRating.text = getString(R.string.rating_format, movie.voteAverage)
            binding.tvRelease.text = movie.releaseDate
            binding.tvPopularity.text = movie.popularity.toString()
            binding.tvAgeRating.text = movie.voteCount.toString()
            binding.tvOverview.text = movie.overview

            var statusFavorite = movie.isFavorite
            setStatusFavorite(statusFavorite)

            binding.fabFavorite.setOnClickListener {
                statusFavorite = !statusFavorite
                detailViewModel.setFavoriteMovie(movie, statusFavorite)
                setStatusFavorite(statusFavorite)
            }

            binding.btnShare.setOnClickListener {
                ShareCompat.IntentBuilder.from(this)
                    .setType("text/plain")
                    .setChooserTitle("Share to friend")
                    .setText(getString(R.string.share_movie, movie.title))
                    .startChooser()
            }
        }
    }

    private fun setStatusFavorite(statusFavorite: Boolean) {
        val icon = if (statusFavorite) R.drawable.ic_loved else R.drawable.ic_love
        binding.fabFavorite.setImageDrawable(ContextCompat.getDrawable(this, icon))
    }
}