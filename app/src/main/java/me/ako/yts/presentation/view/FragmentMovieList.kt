package me.ako.yts.presentation.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.addCallback
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
import me.ako.yts.presentation.presenter.SearchAdapter
import javax.inject.Inject

@AndroidEntryPoint
class FragmentMovieList : Fragment() {
    private val viewModel: AppViewModel by activityViewModels()
    private var _binding: FragmentMovieListBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var utils: Utils
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

        handleBackPressed()
        setupSearch()

        val adapter = MovieAdapter {
            val action = FragmentMovieListDirections.actionFragmentMovieListToFragmentMovieDetail(
                it.id,
                it.title
            )
            findNavController().navigate(action)
        }

        utils.moviesSizeFlow.asLiveData(Dispatchers.IO).observe(viewLifecycleOwner) {
            Log.d("FragmentMovieList", "moviesSizeFlow: $it")
            moviesSize = it
        }

        binding.apply {
            recyclerMovieList.adapter = adapter
            swipeRefresh.setOnRefreshListener {
                viewModel.refreshMovies()
            }
            fabNewMovies.setOnClickListener {
                recyclerMovieList.smoothScrollToPosition(0)
                fabNewMovies.hide()
            }

            viewModel.status.observe(viewLifecycleOwner) {
                when (it) {
                    is ApiStatus.Done -> {
                        if (swipeRefresh.isRefreshing) {
                            swipeRefresh.isRefreshing = false
                        }
                    }

                    is ApiStatus.Error -> {
                        if (swipeRefresh.isRefreshing) {
                            swipeRefresh.isRefreshing = false
                        }
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }

                    is ApiStatus.Loading -> {
                        if (!swipeRefresh.isRefreshing) {
                            swipeRefresh.isRefreshing = true
                        }
                    }
                }
            }

            viewModel.movies.observe(viewLifecycleOwner) { list ->
                adapter.submitList(list)
                if (list.size > moviesSize && moviesSize == 0) {
                    lifecycleScope.launch {
                        utils.updateMoviesSize(list.size)
                    }
                } else if (list.size > moviesSize) {
                    lifecycleScope.launch {
                        utils.updateMoviesSize(list.size)
                    }

                    fabNewMovies.show()
                }
            }
        }
    }

    private fun setupSearch() {
        val adapter = SearchAdapter {
            val action = FragmentMovieListDirections.actionFragmentMovieListToFragmentMovieDetail(
                it.id,
                it.title
            )
            findNavController().navigate(action)
        }

        binding.apply {
            searchView.setupWithSearchBar(searchBar)
            recyclerSearch.adapter = adapter

            searchView.editText.setOnEditorActionListener { v, actionId, event ->
                if (v.text.isNotEmpty() && actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val query = v.text.toString().lowercase()

                    viewModel.searchMovies(query).observe(viewLifecycleOwner) {
                        when(it) {
                            is ApiStatus.Done -> {
                                progressSearch.hide()
                            }
                            is ApiStatus.Error -> {
                                progressSearch.hide()
                                Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                            }
                            is ApiStatus.Loading -> {
                                progressSearch.show()
                            }
                        }
                    }
                }

                searchView.clearFocusAndHideKeyboard()
                true
            }

            viewModel.moviesSearch.observe(viewLifecycleOwner) {
                adapter.submitList(it)
            }
        }
    }

    private fun handleBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (binding.searchView.isShowing) {
                binding.searchView.hide()
            } else {
                requireActivity().finish()
            }
        }
    }
}