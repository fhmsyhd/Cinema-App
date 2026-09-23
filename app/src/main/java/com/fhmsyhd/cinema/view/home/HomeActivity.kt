package com.fhmsyhd.cinema.view.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.fhmsyhd.cinema.R
import com.fhmsyhd.cinema.adapter.MovieAdapter
import com.fhmsyhd.cinema.data.domain.model.Movie
import com.fhmsyhd.cinema.databinding.ActivityHomeBinding
import com.fhmsyhd.cinema.utils.UiState
import com.fhmsyhd.cinema.view.detail.DetailActivity
import com.fhmsyhd.cinema.view.detail.DetailActivity.Companion.EXTRA_DATA_MOVIE
import com.fhmsyhd.cinema.view.favorite.FavoriteActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerViews()
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
        supportActionBar?.elevation = 0f
    }

    private val topRatedAdapter = MovieAdapter()
    private val popularAdapter = MovieAdapter()
    private val playingAdapter = MovieAdapter()

    private fun setupRecyclerViews() {
        val onItemClickAction: (Movie) -> Unit = { selectedData ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra(EXTRA_DATA_MOVIE, selectedData)
            startActivity(intent)
        }

        topRatedAdapter.onItemClick = onItemClickAction
        popularAdapter.onItemClick = onItemClickAction
        playingAdapter.onItemClick = onItemClickAction

        with(binding.rvMovieTop) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            adapter = topRatedAdapter
        }
        with(binding.rvMoviePopular) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            adapter = popularAdapter
        }
        with(binding.rvMoviePlaying) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            adapter = playingAdapter
        }
    }

    private fun observeViewModel() {
        homeViewModel.topRatedMovie.observe(this) { state ->
            handleUiState(
                state,
                binding.loadingTop.root,
                binding.tvTitleTop,
                binding.rvMovieTop,
                topRatedAdapter
            )
        }
        homeViewModel.popularMovie.observe(this) { state ->
            handleUiState(
                state,
                binding.loadingPopular.root,
                binding.tvTitlePopular,
                binding.rvMoviePopular,
                popularAdapter
            )
        }
        homeViewModel.playingMovie.observe(this) { state ->
            handleUiState(
                state,
                binding.loadingPlaying.root,
                binding.tvTitlePlaying,
                binding.rvMoviePlaying,
                playingAdapter
            )
        }
    }

    private fun handleUiState(
        state: UiState<List<Movie>>,
        loadingView: View,
        titleView: View,
        recyclerView: View,
        adapter: MovieAdapter
    ) {
        when (state) {
            is UiState.Loading -> {
                loadingView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                binding.viewError.root.visibility = View.GONE
            }
            is UiState.Success -> {
                loadingView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                adapter.submitList(state.data)
            }
            is UiState.Error -> {
                loadingView.visibility = View.GONE
                titleView.visibility = View.GONE
                recyclerView.visibility = View.GONE
                binding.viewError.root.visibility = View.VISIBLE
            }
        }
    }
}
