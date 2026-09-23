package com.fhmsyhd.cinema.adapter

import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fhmsyhd.cinema.databinding.ItemReviewBinding
import com.fhmsyhd.cinema.data.domain.model.Review

class ReviewAdapter : ListAdapter<Review, ReviewAdapter.ListViewHolder>(DiffCallback) {

    var onReadFullReview: ((Review) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding, onReadFullReview)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)
    }

    class ListViewHolder(
        private val binding: ItemReviewBinding,
        private val onReadFullReview: ((Review) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Review) {
            with(binding) {
                val author = data.author.ifBlank { root.context.getString(com.fhmsyhd.cinema.R.string.unknown_author) }
                tvAuthor.text = author
                tvAuthorInitial.text = author.first().uppercaseChar().toString()
                tvRating.text = data.rating?.let {
                    root.context.getString(com.fhmsyhd.cinema.R.string.review_rating_format, it)
                } ?: root.context.getString(com.fhmsyhd.cinema.R.string.review_not_rated)
                tvContent.text = data.content
                tvReadFull.visibility = if (data.content.length > REVIEW_PREVIEW_LENGTH) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
                tvReadFull.setOnClickListener { onReadFullReview?.invoke(data) }
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Review>() {
        private const val REVIEW_PREVIEW_LENGTH = 160

        override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean {
            return oldItem.author == newItem.author && oldItem.content == newItem.content
        }

        override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean {
            return oldItem == newItem
        }
    }
}
