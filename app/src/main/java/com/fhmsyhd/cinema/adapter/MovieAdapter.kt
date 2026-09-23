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
import com.fhmsyhd.cinema.databinding.ItemMovieBinding

class MovieAdapter : ListAdapter<Movie, MovieAdapter.ListViewHolder>(DiffCallback) {

    var onItemClick: ((Movie) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemMovieBinding.inflate(layoutInflater, parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)
    }

    inner class ListViewHolder(
        private val binding: ItemMovieBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Movie) {
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
