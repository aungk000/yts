package me.ako.yts.presentation.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.ako.yts.data.network.MovieApi.ApiStatus
import me.ako.yts.databinding.FragmentMovieListBinding
import me.ako.yts.domain.util.Utils
import me.ako.yts.domain.viewmodel.AppViewModel
import me.ako.yts.presentation.presenter.MovieAdapter
import javax.inject.Inject

@AndroidEntryPoint
class FragmentMovieList : Fragment() {
    private val viewModel: AppViewModel by activityViewModels()
    private var _binding: FragmentMovieListBinding? = null
    private val binding get() = _binding!!
    @Inject lateinit var utils: Utils
    private var moviesSize = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = MovieAdapter {
            val action = FragmentMovieListDirections.actionFragmentMovieListToFragmentMovieDetail(
                it.id,
                it.title
            )
            findNavController().navigate(action)
        }

        binding.apply {
            recyclerMovieList.adapter = adapter
            searchView.setupWithSearchBar(binding.searchBar)
            swipeRefresh.setOnRefreshListener {
                viewModel.refreshMovies()
            }
        }

        viewModel.status.observe(viewLifecycleOwner) {
            when (it) {
                ApiStatus.Done -> {
                    binding.apply {
                        if(swipeRefresh.isRefreshing) {
                            swipeRefresh.isRefreshing = false
                        }
                    }
                }

                ApiStatus.Error -> {
                    Toast.makeText(requireContext(), "Error loading movies", Toast.LENGTH_LONG)
                        .show()
                    binding.apply {
                        if(swipeRefresh.isRefreshing) {
                            swipeRefresh.isRefreshing = false
                        }
                    }
                }

                ApiStatus.Loading -> {
                    binding.apply {
                        if(!swipeRefresh.isRefreshing) {
                            swipeRefresh.isRefreshing = true
                        }
                    }
                }
            }
        }

        utils.moviesSizeFlow.asLiveData(Dispatchers.IO).observe(viewLifecycleOwner) {
            Log.d("FragmentMovieList", "moviesSizeFlow: $it")
            moviesSize = it
        }

        viewModel.movies.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            if(it.size > moviesSize && moviesSize == 0) {
                lifecycleScope.launch {
                    utils.updateMoviesSize(it.size)
                }
            } else if(it.size > moviesSize) {
                lifecycleScope.launch {
                    utils.updateMoviesSize(it.size)
                }

                binding.apply {
                    fabNewMovies.visibility = View.VISIBLE
                    fabNewMovies.setOnClickListener {
                        recyclerMovieList.smoothScrollToPosition(0)
                        fabNewMovies.visibility = View.GONE
                    }
                }
            }
            Log.d("FragmentMovieList", "loadMovies: ${it.size}")
        }
    }
}