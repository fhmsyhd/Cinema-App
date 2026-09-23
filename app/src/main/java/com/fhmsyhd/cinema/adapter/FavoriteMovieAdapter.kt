package com.fhmsyhd.cinema.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.fhmsyhd.cinema.R
import com.fhmsyhd.cinema.data.domain.model.Movie
import com.fhmsyhd.cinema.data.utils.Constant.IMAGE_URL
import com.fhmsyhd.cinema.databinding.ItemFavoriteMovieBinding

class FavoriteMovieAdapter :
    ListAdapter<Movie, FavoriteMovieAdapter.FavoriteMovieViewHolder>(DiffCallback) {

    var onMovieClick: ((Movie) -> Unit)? = null
    var onFavoriteClick: ((Movie) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteMovieViewHolder {
        val binding = ItemFavoriteMovieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FavoriteMovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteMovieViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FavoriteMovieViewHolder(
        private val binding: ItemFavoriteMovieBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) = with(binding) {
            Glide.with(root)
                .load(movie.posterPath?.takeIf(String::isNotBlank)?.let { IMAGE_URL + it })
                .placeholder(R.drawable.bg_image_placeholder)
                .error(R.drawable.bg_image_placeholder)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imgMovie)

            imgMovie.contentDescription = root.context.getString(
                R.string.movie_poster_content_description,
                movie.title
            )
            tvTitle.text = movie.title
            tvRating.text = root.context.getString(R.string.rating_short_format, movie.voteAverage)
            tvRelease.text = movie.releaseDate.take(4).takeIf { it.length == 4 }
                ?: root.context.getString(R.string.release_unknown)

            root.setOnClickListener { onMovieClick?.invoke(movie) }
            btnRemoveFavorite.setOnClickListener { onFavoriteClick?.invoke(movie) }
        }
    }

    private companion object DiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean =
            oldItem.movieId == newItem.movieId

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean =
            oldItem == newItem
    }
}
