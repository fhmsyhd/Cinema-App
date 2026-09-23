package com.fhmsyhd.cinema.view.favorite

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.fhmsyhd.cinema.adapter.MovieAdapter
import com.fhmsyhd.cinema.databinding.ActivityFavoriteBinding
import com.fhmsyhd.cinema.utils.UiState
import com.fhmsyhd.cinema.view.detail.DetailActivity
import com.fhmsyhd.cinema.view.detail.DetailActivity.Companion.EXTRA_DATA_MOVIE
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoriteBinding

    private val favoriteMovieVieModel: FavoriteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topAppBar.setNavigationOnClickListener { finish() }

        val movieAdapter = MovieAdapter(MovieAdapter.VIEW_TYPE_FAVORITE)
        movieAdapter.onItemClick = { selectedData ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra(EXTRA_DATA_MOVIE, selectedData)
            startActivity(intent)
        }

        favoriteMovieVieModel.favoriteMovie.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.rvMoviePlaying.visibility = View.GONE
                    binding.viewEmpty.root.visibility = View.GONE
                }
                is UiState.Success -> {
                    binding.rvMoviePlaying.visibility = View.VISIBLE
                    binding.viewEmpty.root.visibility = View.GONE
                    movieAdapter.submitList(state.data)
                }
                is UiState.Error -> {
                    binding.rvMoviePlaying.visibility = View.GONE
                    binding.viewEmpty.root.visibility = View.VISIBLE
                }
            }
        }

        with(binding.rvMoviePlaying) {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = movieAdapter
        }

        supportActionBar?.elevation = 0f
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
