package com.fhmsyhd.cinema.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.fhmsyhd.cinema.R
import com.fhmsyhd.cinema.data.domain.model.Movie
import com.fhmsyhd.cinema.data.utils.Constant.IMAGE_URL
import com.fhmsyhd.cinema.databinding.ItemFavoriteMovieBinding
import com.fhmsyhd.cinema.databinding.ItemMovieBinding

class MovieAdapter(private val viewType: Int = VIEW_TYPE_DEFAULT) : ListAdapter<Movie, MovieAdapter.ListViewHolder>(DiffCallback) {

    var onItemClick: ((Movie) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = when (this.viewType) {
            VIEW_TYPE_FAVORITE -> ItemFavoriteMovieBinding.inflate(layoutInflater, parent, false)
            else -> ItemMovieBinding.inflate(layoutInflater, parent, false)
        }
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)
    }

    inner class ListViewHolder(private val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(data: Movie) {
            when (binding) {
                is ItemMovieBinding -> {
                    Glide.with(itemView.context)
                        .load(data.posterPath?.takeIf { it.isNotEmpty() }?.let { IMAGE_URL + it })
                        .placeholder(R.drawable.bg_image_placeholder)
                        .error(R.drawable.bg_image_placeholder)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(binding.imgMovie)
                    binding.tvTitle.text = data.title
                    binding.tvRating.text = itemView.context.getString(
                        R.string.rating_short_format,
                        data.voteAverage
                    )
                    binding.imgMovie.contentDescription = itemView.context.getString(
                        R.string.movie_poster_content_description,
                        data.title
                    )
                }
                is ItemFavoriteMovieBinding -> {
                    Glide.with(itemView.context)
                        .load(data.posterPath?.takeIf { it.isNotEmpty() }?.let { IMAGE_URL + it })
                        .placeholder(R.drawable.bg_image_placeholder)
                        .error(R.drawable.bg_image_placeholder)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(binding.imgMovie)
                    binding.imgMovie.contentDescription = itemView.context.getString(
                        R.string.movie_poster_content_description,
                        data.title
                    )
                    binding.tvTitle.text = data.title
                    binding.tvRelease.text = data.releaseDate
                    binding.tvRating.text = "Rating : ${data.voteAverage}"
                    binding.tvOverview.text = data.overview
                }
            }
        }

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(getItem(position))
                }
            }
        }
    }

    companion object {
        const val VIEW_TYPE_DEFAULT = 0
        const val VIEW_TYPE_FAVORITE = 1

        private val DiffCallback = object : DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem.movieId == newItem.movieId
            }

            override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem == newItem
            }
        }
    }
}
