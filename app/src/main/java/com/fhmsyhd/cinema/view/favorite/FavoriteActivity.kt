package com.fhmsyhd.cinema.view.favorite

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.fhmsyhd.cinema.R
import com.fhmsyhd.cinema.adapter.FavoriteMovieAdapter
import com.fhmsyhd.cinema.data.domain.model.Movie
import com.fhmsyhd.cinema.databinding.ActivityFavoriteBinding
import com.fhmsyhd.cinema.utils.UiState
import com.fhmsyhd.cinema.view.detail.DetailActivity
import com.fhmsyhd.cinema.view.detail.DetailActivity.Companion.EXTRA_DATA_MOVIE
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteActivity : AppCompatActivity() {

    private enum class SortMode {
        TITLE,
        RATING,
        RELEASE_YEAR
    }

    private lateinit var binding: ActivityFavoriteBinding
    private val favoriteViewModel: FavoriteViewModel by viewModels()
    private val favoriteAdapter = FavoriteMovieAdapter()

    private var favoriteMovies: List<Movie> = emptyList()
    private var sortMode = SortMode.TITLE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupMovieGrid()
        setupEmptyState()
        observeFavorites()
    }

    private fun setupToolbar() {
        binding.topAppBar.setNavigationOnClickListener { finish() }
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.item_sort -> {
                    showSortDialog()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupMovieGrid() {
        favoriteAdapter.onMovieClick = ::openMovieDetails
        favoriteAdapter.onFavoriteClick = ::removeFavorite

        binding.rvFavoriteMovies.apply {
            layoutManager = GridLayoutManager(context, 2)
            setHasFixedSize(true)
            adapter = favoriteAdapter
        }
    }

    private fun setupEmptyState() {
        binding.viewEmpty.btnBrowseMovies.setOnClickListener { finish() }
    }

    private fun observeFavorites() {
        favoriteViewModel.favoriteMovie.observe(this) { state ->
            when (state) {
                is UiState.Loading -> showLoading()
                is UiState.Success -> renderFavorites(state.data)
                is UiState.Error -> renderFavorites(emptyList())
            }
        }
    }

    private fun showLoading() {
        binding.tvFavoriteCount.visibility = View.INVISIBLE
        binding.rvFavoriteMovies.visibility = View.GONE
        binding.viewEmpty.root.visibility = View.GONE
        binding.loadingFavorites.root.visibility = View.VISIBLE
        binding.loadingFavorites.root.startShimmer()
    }

    private fun renderFavorites(movies: List<Movie>) {
        favoriteMovies = movies
        binding.loadingFavorites.root.stopShimmer()
        binding.loadingFavorites.root.visibility = View.GONE
        binding.tvFavoriteCount.visibility = View.VISIBLE
        binding.tvFavoriteCount.text = resources.getQuantityString(
            R.plurals.favorite_count,
            movies.size,
            movies.size
        )
        binding.topAppBar.menu.findItem(R.id.item_sort).isVisible = movies.size > 1

        if (movies.isEmpty()) {
            favoriteAdapter.submitList(emptyList())
            binding.rvFavoriteMovies.visibility = View.GONE
            binding.viewEmpty.root.visibility = View.VISIBLE
        } else {
            binding.viewEmpty.root.visibility = View.GONE
            binding.rvFavoriteMovies.visibility = View.VISIBLE
            submitSortedMovies()
        }
    }

    private fun submitSortedMovies() {
        val sortedMovies = when (sortMode) {
            SortMode.TITLE -> favoriteMovies.sortedBy { it.title.lowercase() }
            SortMode.RATING -> favoriteMovies.sortedByDescending { it.voteAverage }
            SortMode.RELEASE_YEAR -> favoriteMovies.sortedByDescending {
                it.releaseDate.take(4).toIntOrNull() ?: 0
            }
        }
        favoriteAdapter.submitList(sortedMovies)
    }

    private fun showSortDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.sort_by)
            .setSingleChoiceItems(
                R.array.favorite_sort_options,
                sortMode.ordinal
            ) { dialog, selectedPosition ->
                sortMode = SortMode.entries[selectedPosition]
                submitSortedMovies()
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun removeFavorite(movie: Movie) {
        favoriteViewModel.removeFavorite(movie)
        Snackbar.make(
            binding.root,
            R.string.removed_from_favorites,
            Snackbar.LENGTH_LONG
        ).setAction(R.string.undo) {
            favoriteViewModel.restoreFavorite(movie)
        }.show()
    }

    private fun openMovieDetails(movie: Movie) {
        startActivity(
            Intent(this, DetailActivity::class.java).apply {
                putExtra(EXTRA_DATA_MOVIE, movie)
            }
        )
    }

    override fun onDestroy() {
        binding.loadingFavorites.root.stopShimmer()
        super.onDestroy()
    }
}
