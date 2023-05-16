package me.ako.yts.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import me.ako.yts.data.network.MovieApi.ApiStatus
import me.ako.yts.databinding.FragmentMovieDetailBinding
import me.ako.yts.domain.util.Utils
import me.ako.yts.domain.viewmodel.AppViewModel
import me.ako.yts.presentation.presenter.CastAdapter
import me.ako.yts.presentation.presenter.MovieSuggestionAdapter
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.round
import kotlin.math.roundToLong

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

            viewModel.loadMovie(args.movieId).observe(viewLifecycleOwner) { movie ->
                this.movie = movie
                txtYear.text = movie.year.toString()
                txtGenre.text = movie.genres?.joinToString(separator = " / ")
                val uploaded = "Uploaded: ${movie.date_uploaded}"
                txtUploadedDate.text = uploaded

                movie.like_count?.let { count ->
                    txtLikeCount.text = utils.shortenNumber(count, 10)
                }

                movie.download_count?.let { count ->
                    txtDownloadCount.text = utils.shortenNumber(count, 10)
                }

                txtImdbRating.text = movie.rating.toString()

                txtImdbRating.setOnClickListener {
                    utils.imdbTitle(movie.imdb_code)
                }

                movie.cast?.let { list ->
                    val castAdapter = CastAdapter(list) {
                        utils.imdbName(it.imdb_code)
                    }
                    recyclerCast.adapter = castAdapter
                }
            }

            viewModel.statusMovieDetail.observe(viewLifecycleOwner) {
                when (it) {
                    is ApiStatus.Done -> {
                        progressMovieDetail.hide()
                        layoutMovieDetail.visibility = View.VISIBLE
                    }

                    is ApiStatus.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                        progressMovieDetail.hide()
                        layoutMovieDetail.visibility = View.INVISIBLE
                    }

                    is ApiStatus.Loading -> {
                        progressMovieDetail.show()
                        layoutMovieDetail.visibility = View.INVISIBLE
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