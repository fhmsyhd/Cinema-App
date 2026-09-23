package com.fhmsyhd.cinema.view.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.facebook.shimmer.ShimmerFrameLayout
import com.fhmsyhd.cinema.R
import com.fhmsyhd.cinema.adapter.MovieAdapter
import com.fhmsyhd.cinema.data.domain.model.Movie
import com.fhmsyhd.cinema.data.utils.Constant.BACKDROP_IMAGE_URL
import com.fhmsyhd.cinema.data.utils.Constant.IMAGE_URL
import com.fhmsyhd.cinema.databinding.ActivityHomeBinding
import com.fhmsyhd.cinema.databinding.ViewSectionStateBinding
import com.fhmsyhd.cinema.utils.UiState
import com.fhmsyhd.cinema.view.detail.DetailActivity
import com.fhmsyhd.cinema.view.detail.DetailActivity.Companion.EXTRA_DATA_MOVIE
import com.fhmsyhd.cinema.view.favorite.FavoriteActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    private val homeViewModel: HomeViewModel by viewModels()
    private val topRatedAdapter = MovieAdapter()
    private val popularAdapter = MovieAdapter()
    private val playingAdapter = MovieAdapter()

    private var featuredMovie: Movie? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerViews()
        setupActions()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.item_favorite -> {
                    startActivity(Intent(this, FavoriteActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun setupRecyclerViews() {
        val onItemClickAction: (Movie) -> Unit = ::openMovieDetails

        topRatedAdapter.onItemClick = onItemClickAction
        popularAdapter.onItemClick = onItemClickAction
        playingAdapter.onItemClick = onItemClickAction

        setupMovieRail(binding.rvMovieTop, topRatedAdapter)
        setupMovieRail(binding.rvMoviePopular, popularAdapter)
        setupMovieRail(binding.rvMoviePlaying, playingAdapter)
    }

    private fun setupMovieRail(recyclerView: RecyclerView, movieAdapter: MovieAdapter) {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            adapter = movieAdapter
        }
    }

    private fun setupActions() {
        binding.heroCard.setOnClickListener {
            featuredMovie?.let(::openMovieDetails)
        }
        binding.btnHeroDetails.setOnClickListener {
            featuredMovie?.let(::openMovieDetails)
        }
        binding.statePopular.btnRetry.setOnClickListener {
            homeViewModel.retryPopular()
        }
        binding.stateTop.btnRetry.setOnClickListener {
            homeViewModel.retryTopRated()
        }
        binding.statePlaying.btnRetry.setOnClickListener {
            homeViewModel.retryPlaying()
        }
    }

    private fun observeViewModel() {
        homeViewModel.topRatedMovie.observe(this) { state ->
            renderSection(
                state = state,
                loadingView = binding.loadingTop.root,
                recyclerView = binding.rvMovieTop,
                stateBinding = binding.stateTop,
                adapter = topRatedAdapter
            )
        }
        homeViewModel.popularMovie.observe(this) { state ->
            renderSection(
                state = state,
                loadingView = binding.loadingPopular.root,
                recyclerView = binding.rvMoviePopular,
                stateBinding = binding.statePopular,
                adapter = popularAdapter
            )
        }
        homeViewModel.playingMovie.observe(this) { state ->
            renderHero(state)
            renderSection(
                state = state,
                loadingView = binding.loadingPlaying.root,
                recyclerView = binding.rvMoviePlaying,
                stateBinding = binding.statePlaying,
                adapter = playingAdapter
            )
        }
    }

    private fun renderHero(state: UiState<List<Movie>>) {
        when (state) {
            is UiState.Loading -> {
                if (featuredMovie == null) {
                    binding.heroContainer.visibility = View.VISIBLE
                    binding.heroCard.visibility = View.GONE
                    binding.loadingHero.root.visibility = View.VISIBLE
                    binding.loadingHero.root.startShimmer()
                }
            }
            is UiState.Success -> {
                binding.loadingHero.root.stopShimmer()
                binding.loadingHero.root.visibility = View.GONE

                state.data.firstOrNull()?.let { movie ->
                    featuredMovie = movie
                    bindHeroMovie(movie)
                    binding.heroContainer.visibility = View.VISIBLE
                    binding.heroCard.visibility = View.VISIBLE
                } ?: run {
                    featuredMovie = null
                    binding.heroContainer.visibility = View.GONE
                }
            }
            is UiState.Error -> {
                binding.loadingHero.root.stopShimmer()
                binding.loadingHero.root.visibility = View.GONE
                if (featuredMovie == null) {
                    binding.heroContainer.visibility = View.GONE
                } else {
                    binding.heroContainer.visibility = View.VISIBLE
                    binding.heroCard.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun bindHeroMovie(movie: Movie) {
        val backdropUrl = movie.backdropPath
            ?.takeIf(String::isNotBlank)
            ?.let { BACKDROP_IMAGE_URL + it }
            ?: movie.posterPath?.takeIf(String::isNotBlank)?.let { IMAGE_URL + it }

        Glide.with(this)
            .load(backdropUrl)
            .placeholder(R.drawable.bg_image_placeholder)
            .error(R.drawable.bg_image_placeholder)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.imgHeroBackdrop)

        val releaseYear = movie.releaseDate
            .take(4)
            .takeIf { it.length == 4 }
            ?: getString(R.string.release_unknown)

        binding.imgHeroBackdrop.contentDescription = getString(
            R.string.movie_backdrop_content_description,
            movie.title
        )
        binding.tvHeroTitle.text = movie.title
        binding.tvHeroMetadata.text = getString(
            R.string.hero_metadata,
            movie.voteAverage,
            releaseYear
        )
    }

    private fun renderSection(
        state: UiState<List<Movie>>,
        loadingView: ShimmerFrameLayout,
        recyclerView: RecyclerView,
        stateBinding: ViewSectionStateBinding,
        adapter: MovieAdapter
    ) {
        when (state) {
            is UiState.Loading -> {
                stateBinding.root.visibility = View.GONE
                if (adapter.currentList.isEmpty()) {
                    recyclerView.visibility = View.GONE
                    loadingView.visibility = View.VISIBLE
                    loadingView.startShimmer()
                } else {
                    loadingView.stopShimmer()
                    loadingView.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }
            }
            is UiState.Success -> {
                loadingView.stopShimmer()
                loadingView.visibility = View.GONE
                adapter.submitList(state.data)

                if (state.data.isEmpty()) {
                    recyclerView.visibility = View.GONE
                    showSectionState(
                        stateBinding = stateBinding,
                        message = getString(R.string.section_empty),
                        showRetry = false
                    )
                } else {
                    stateBinding.root.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }
            }
            is UiState.Error -> {
                loadingView.stopShimmer()
                loadingView.visibility = View.GONE

                if (adapter.currentList.isNotEmpty()) {
                    stateBinding.root.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                } else {
                    recyclerView.visibility = View.GONE
                    showSectionState(
                        stateBinding = stateBinding,
                        message = getString(R.string.section_error),
                        showRetry = true
                    )
                }
            }
        }
    }

    private fun showSectionState(
        stateBinding: ViewSectionStateBinding,
        message: String,
        showRetry: Boolean
    ) {
        stateBinding.root.visibility = View.VISIBLE
        stateBinding.tvStateMessage.text = message
        stateBinding.btnRetry.visibility = if (showRetry) View.VISIBLE else View.GONE
    }

    private fun openMovieDetails(movie: Movie) {
        startActivity(
            Intent(this, DetailActivity::class.java).apply {
                putExtra(EXTRA_DATA_MOVIE, movie)
            }
        )
    }
}
