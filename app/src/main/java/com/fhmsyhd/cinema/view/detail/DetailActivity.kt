package com.fhmsyhd.cinema.view.detail

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.facebook.shimmer.ShimmerFrameLayout
import com.fhmsyhd.cinema.R
import com.fhmsyhd.cinema.adapter.MovieAdapter
import com.fhmsyhd.cinema.adapter.ReviewAdapter
import com.fhmsyhd.cinema.data.domain.model.Movie
import com.fhmsyhd.cinema.data.domain.model.Review
import com.fhmsyhd.cinema.data.utils.Constant.BACKDROP_IMAGE_URL
import com.fhmsyhd.cinema.data.utils.Constant.IMAGE_URL
import com.fhmsyhd.cinema.databinding.ActivityDetailBinding
import com.fhmsyhd.cinema.databinding.ViewSectionStateBinding
import com.fhmsyhd.cinema.utils.UiState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_DATA_MOVIE = "extra_data_movie"
        private const val COLLAPSED_OVERVIEW_LINES = 5
    }

    private lateinit var binding: ActivityDetailBinding
    private val detailViewModel: DetailViewModel by viewModels()

    private val movieAdapter = MovieAdapter()
    private val reviewAdapter = ReviewAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActions()
        setupRecyclerViews()
        observeViewModel()

        val movie = getMovieExtra()
        if (movie == null) {
            showUnavailableState()
        } else {
            showMovie(movie)
            detailViewModel.load(movie.movieId)
        }
    }

    private fun setupActions() {
        binding.btnBack.setOnClickListener { finish() }
        binding.stateDetail.btnRetry.apply {
            text = getString(R.string.go_back)
            setOnClickListener { finish() }
        }
        binding.stateSimilar.btnRetry.setOnClickListener {
            detailViewModel.retrySimilarMovies()
        }
        binding.stateReview.btnRetry.setOnClickListener {
            detailViewModel.retryReviews()
        }
    }

    private fun setupRecyclerViews() {
        movieAdapter.onItemClick = { movie ->
            startActivity(
                Intent(this, DetailActivity::class.java).apply {
                    putExtra(EXTRA_DATA_MOVIE, movie)
                }
            )
        }
        reviewAdapter.onReadFullReview = ::showFullReview

        setupHorizontalRail(binding.rvMoviePlaying, movieAdapter)
        setupHorizontalRail(binding.rvMovieReview, reviewAdapter)
    }

    private fun setupHorizontalRail(recyclerView: RecyclerView, railAdapter: RecyclerView.Adapter<*>) {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            adapter = railAdapter
        }
    }

    private fun observeViewModel() {
        detailViewModel.similarMovies.observe(this, ::renderSimilarMovies)
        detailViewModel.reviews.observe(this, ::renderReviews)
    }

    private fun renderSimilarMovies(state: UiState<List<Movie>>) {
        when (state) {
            is UiState.Loading -> showLoading(
                binding.loadingSimilar.root,
                binding.rvMoviePlaying,
                binding.stateSimilar
            )
            is UiState.Success -> {
                stopLoading(binding.loadingSimilar.root)
                movieAdapter.submitList(state.data)
                if (state.data.isEmpty()) {
                    binding.rvMoviePlaying.visibility = View.GONE
                    showSectionState(
                        binding.stateSimilar,
                        getString(R.string.similar_movies_empty),
                        showRetry = false
                    )
                } else {
                    binding.stateSimilar.root.visibility = View.GONE
                    binding.rvMoviePlaying.visibility = View.VISIBLE
                }
            }
            is UiState.Error -> {
                stopLoading(binding.loadingSimilar.root)
                if (movieAdapter.currentList.isEmpty()) {
                    binding.rvMoviePlaying.visibility = View.GONE
                    showSectionState(
                        binding.stateSimilar,
                        getString(R.string.similar_movies_error),
                        showRetry = true
                    )
                }
            }
        }
    }

    private fun renderReviews(state: UiState<List<Review>>) {
        when (state) {
            is UiState.Loading -> showLoading(
                binding.loadingReview.root,
                binding.rvMovieReview,
                binding.stateReview
            )
            is UiState.Success -> {
                stopLoading(binding.loadingReview.root)
                reviewAdapter.submitList(state.data)
                if (state.data.isEmpty()) {
                    binding.rvMovieReview.visibility = View.GONE
                    showSectionState(
                        binding.stateReview,
                        getString(R.string.reviews_empty),
                        showRetry = false
                    )
                } else {
                    binding.stateReview.root.visibility = View.GONE
                    binding.rvMovieReview.visibility = View.VISIBLE
                }
            }
            is UiState.Error -> {
                stopLoading(binding.loadingReview.root)
                binding.rvMovieReview.visibility = View.GONE
                showSectionState(
                    binding.stateReview,
                    getString(R.string.reviews_error),
                    showRetry = true
                )
            }
        }
    }

    private fun showLoading(
        loadingView: ShimmerFrameLayout,
        recyclerView: RecyclerView,
        stateBinding: ViewSectionStateBinding
    ) {
        stateBinding.root.visibility = View.GONE
        recyclerView.visibility = View.GONE
        loadingView.visibility = View.VISIBLE
        loadingView.startShimmer()
    }

    private fun stopLoading(loadingView: ShimmerFrameLayout) {
        loadingView.stopShimmer()
        loadingView.visibility = View.GONE
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

    private fun showMovie(movie: Movie) {
        binding.detailContent.visibility = View.VISIBLE
        binding.stateDetail.root.visibility = View.GONE
        binding.tvTitle.text = movie.title
        binding.tvRating.text = getString(R.string.rating_short_format, movie.voteAverage)
        binding.tvRelease.text = formatReleaseDate(movie.releaseDate)
        binding.tvVoteCount.text = getString(R.string.votes_format, formatVoteCount(movie.voteCount))
        binding.tvOverview.text = movie.overview.ifBlank {
            getString(R.string.overview_unavailable)
        }

        setupOverviewToggle()
        loadHero(movie)

        var isFavorite = movie.isFavorite
        setFavoriteState(isFavorite)
        binding.fabFavorite.setOnClickListener {
            isFavorite = !isFavorite
            detailViewModel.setFavoriteMovie(movie, isFavorite)
            setFavoriteState(isFavorite)
        }
        binding.btnShare.setOnClickListener { shareMovie(movie) }
    }

    private fun loadHero(movie: Movie) {
        val imageUrl = movie.backdropPath
            ?.takeIf(String::isNotBlank)
            ?.let { BACKDROP_IMAGE_URL + it }
            ?: movie.posterPath?.takeIf(String::isNotBlank)?.let { IMAGE_URL + it }

        binding.imgMovie.contentDescription = getString(
            R.string.movie_backdrop_content_description,
            movie.title
        )
        binding.loadingHero.visibility = View.VISIBLE
        binding.loadingHero.startShimmer()

        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.bg_image_placeholder)
            .error(R.drawable.bg_image_placeholder)
            .transition(DrawableTransitionOptions.withCrossFade())
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    hideHeroLoading()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    hideHeroLoading()
                    return false
                }
            })
            .into(binding.imgMovie)
    }

    private fun hideHeroLoading() {
        binding.loadingHero.stopShimmer()
        binding.loadingHero.visibility = View.GONE
    }

    private fun setupOverviewToggle() {
        var expanded = false
        binding.tvOverview.maxLines = COLLAPSED_OVERVIEW_LINES
        binding.tvOverview.post {
            val lastLine = binding.tvOverview.lineCount - 1
            val isEllipsized = lastLine >= 0 &&
                binding.tvOverview.layout?.getEllipsisCount(lastLine)?.let { it > 0 } == true
            binding.btnOverviewToggle.visibility = if (isEllipsized) View.VISIBLE else View.GONE
        }
        binding.btnOverviewToggle.setOnClickListener {
            expanded = !expanded
            binding.tvOverview.maxLines = if (expanded) Int.MAX_VALUE else COLLAPSED_OVERVIEW_LINES
            binding.tvOverview.ellipsize = if (expanded) null else android.text.TextUtils.TruncateAt.END
            binding.btnOverviewToggle.text = getString(
                if (expanded) R.string.read_less else R.string.read_more
            )
        }
    }

    private fun setFavoriteState(isFavorite: Boolean) {
        binding.fabFavorite.setImageResource(
            if (isFavorite) R.drawable.ic_loved else R.drawable.ic_love
        )
        binding.fabFavorite.contentDescription = getString(
            if (isFavorite) R.string.remove_from_favorites else R.string.add_to_favorites
        )
    }

    private fun shareMovie(movie: Movie) {
        ShareCompat.IntentBuilder(this)
            .setType("text/plain")
            .setChooserTitle(getString(R.string.share))
            .setText(getString(R.string.share_movie, movie.title))
            .startChooser()
    }

    private fun showFullReview(review: Review) {
        MaterialAlertDialogBuilder(this)
            .setTitle(review.author.ifBlank { getString(R.string.unknown_author) })
            .setMessage(review.content)
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }

    private fun showUnavailableState() {
        binding.detailContent.visibility = View.GONE
        showSectionState(
            binding.stateDetail,
            getString(R.string.movie_unavailable),
            showRetry = true
        )
    }

    private fun formatReleaseDate(rawDate: String): String {
        if (rawDate.isBlank()) return getString(R.string.release_unknown)
        return runCatching {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { isLenient = false }
            val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            formatter.format(requireNotNull(parser.parse(rawDate)))
        }.getOrDefault(rawDate)
    }

    private fun formatVoteCount(voteCount: Int): String = when {
        voteCount >= 1_000_000 -> compactNumber(voteCount / 1_000_000.0, "M")
        voteCount >= 1_000 -> compactNumber(voteCount / 1_000.0, "K")
        else -> voteCount.toString()
    }

    private fun compactNumber(value: Double, suffix: String): String {
        val formatted = String.format(Locale.US, "%.1f", value).removeSuffix(".0")
        return formatted + suffix
    }

    @Suppress("DEPRECATION")
    private fun getMovieExtra(): Movie? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        intent.getParcelableExtra(EXTRA_DATA_MOVIE, Movie::class.java)
    } else {
        intent.getParcelableExtra(EXTRA_DATA_MOVIE)
    }

    override fun onDestroy() {
        binding.loadingHero.stopShimmer()
        binding.loadingSimilar.root.stopShimmer()
        binding.loadingReview.root.stopShimmer()
        super.onDestroy()
    }
}
