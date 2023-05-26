package me.ako.yts.presentation.view

import android.os.Bundle
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import me.ako.yts.R
import me.ako.yts.data.network.MovieApi.ApiStatus
import me.ako.yts.databinding.FragmentMovieDetailBinding
import me.ako.yts.domain.util.Utils
import me.ako.yts.domain.viewmodel.AppViewModel
import me.ako.yts.presentation.presenter.CastAdapter
import me.ako.yts.presentation.presenter.MovieSuggestionAdapter
import javax.inject.Inject

@AndroidEntryPoint
class FragmentMovieDetail : Fragment() {
    private val viewModel: AppViewModel by activityViewModels()
    private var _binding: FragmentMovieDetailBinding? = null
    private val binding get() = _binding!!
    private val args: FragmentMovieDetailArgs by navArgs()

    @Inject
    lateinit var utils: Utils

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            appViewModel = viewModel

            viewModel.loadMovie(args.movieId).observe(viewLifecycleOwner) { movie ->
                this.movie = movie

                val year = movie.year.toString()
                val language = "[${movie.language.uppercase()}]"
                val runtime = "${movie.runtime / 60}h ${movie.runtime % 60}m"
                val span = SpannableString("$year \u2022 $language \u2022 $runtime")
                txtYear.text = span

                txtGenre.text = movie.genres.joinToString(separator = " / ")
                txtLikeCount.text = utils.shortenNumber(movie.like_count, 10)
                txtDownloadCount.text = utils.shortenNumber(movie.download_count, 10)
                txtImdbRating.text = movie.rating.toString()

                imgCover.load(movie.medium_cover_image) {
                    error(R.drawable.no_poster)
                }

                val uploaded = "Uploaded: ${movie.date_uploaded}"
                txtUploadedDate.text = uploaded

                txtImdbRating.setOnClickListener {
                    if (movie.imdb_code.isNotBlank()) {
                        utils.imdbTitle(movie.imdb_code)
                    } else {
                        utils.toast("IMDB movie not available")
                    }
                }

                txtYoutube.setOnClickListener {
                    if (movie.yt_trailer_code.isNotBlank()) {
                        utils.youtube(movie.yt_trailer_code)
                    } else {
                        utils.toast("Youtube trailer not available")
                    }
                }

                val castAdapter = CastAdapter(movie.cast) {
                    if (movie.yt_trailer_code.isNotBlank()) {
                        utils.imdbName(it.imdb_code)
                    } else {
                        utils.toast("IMDB profile not available")
                    }
                }
                recyclerCast.adapter = castAdapter
            }

            viewModel.statusMovieDetail.observe(viewLifecycleOwner) {
                when (it) {
                    is ApiStatus.Done -> {
                        layoutMovieDetail.visibility = View.VISIBLE
                        //layoutEmpty.visibility = View.GONE
                    }

                    is ApiStatus.Error -> {
                        layoutMovieDetail.visibility = View.GONE
                        //layoutEmpty.visibility = View.GONE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }

                    is ApiStatus.Loading -> {
                        layoutMovieDetail.visibility = View.GONE
                        //layoutEmpty.visibility = View.VISIBLE
                    }
                }
            }

            val adapter = MovieSuggestionAdapter {
                val action =
                    FragmentMovieDetailDirections.actionFragmentMovieDetailSelf(it.id, it.title)
                findNavController().navigate(action)
            }
            recyclerMovieSuggestions.adapter = adapter

            viewModel.loadMovieSuggestions(args.movieId).observe(viewLifecycleOwner) {
                adapter.submitList(it)
            }
        }
    }
}