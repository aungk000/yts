package me.ako.yts.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import me.ako.yts.data.network.MovieApi.ApiStatus
import me.ako.yts.databinding.FragmentMovieDetailBinding
import me.ako.yts.domain.viewmodel.AppViewModel
import me.ako.yts.presentation.presenter.MovieSuggestionAdapter

@AndroidEntryPoint
class FragmentMovieDetail : Fragment() {
    private val viewModel: AppViewModel by activityViewModels()
    private var _binding: FragmentMovieDetailBinding? = null
    private val binding get() = _binding!!
    private val args: FragmentMovieDetailArgs by navArgs()

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

        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.loadMovie(args.movieId).first.observe(viewLifecycleOwner) {
            binding.apply {
                movie = it
                txtYear.text = it.year.toString()
                txtGenre.text = it.genres.toString()
                txtUploadedDate.text = "Uploaded: ${it.date_uploaded}"
            }
        }

        viewModel.loadMovie(args.movieId).second.observe(viewLifecycleOwner) {
            when (it) {
                ApiStatus.Done -> {
                    binding.apply {
                        progressMovieDetail.visibility = View.GONE
                        layoutMovieDetail.visibility = View.VISIBLE
                    }
                }

                ApiStatus.Error -> {
                    Toast.makeText(
                        requireContext(),
                        "Error loading movie detail",
                        Toast.LENGTH_LONG
                    ).show()
                    binding.apply {
                        progressMovieDetail.visibility = View.GONE
                    }
                }

                ApiStatus.Loading -> {
                    binding.apply {
                        progressMovieDetail.visibility = View.VISIBLE
                        layoutMovieDetail.visibility = View.INVISIBLE
                    }
                }
            }
        }

        val adapter = MovieSuggestionAdapter {
            val action =
                FragmentMovieDetailDirections.actionFragmentMovieDetailSelf(it.id, it.title)
            findNavController().navigate(action)
        }
        binding.recyclerMovieSuggestion.adapter = adapter

        viewModel.loadMovieSuggestion(args.movieId).observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }
}