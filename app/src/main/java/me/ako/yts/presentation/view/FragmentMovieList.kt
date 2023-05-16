package me.ako.yts.presentation.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.google.android.material.appbar.MaterialToolbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import me.ako.yts.R
import me.ako.yts.data.datasource.model.MovieEntity
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
    private var lastScrollPosition = 0
    private var once = true
    private var isLoading = false
    private var movies = listOf<MovieEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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
        addMenuProvider()

        utils.moviesSizeFlow.asLiveData(Dispatchers.IO).observe(viewLifecycleOwner) {
            moviesSize = it
        }

        utils.lastScrollPosition.asLiveData(Dispatchers.IO).observe(viewLifecycleOwner) {
            lastScrollPosition = it
        }

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            val adapter = MovieAdapter {
                val action =
                    FragmentMovieListDirections.actionFragmentMovieListToFragmentMovieDetail(
                        it.id,
                        it.title
                    )
                findNavController().navigate(action)
            }

            recyclerMovieList.adapter = adapter
            recyclerMovieList.addOnScrollListener(object : OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager as GridLayoutManager
                    if (!isLoading &&
                        layoutManager.findLastCompletelyVisibleItemPosition() == movies.size - 1
                    ) {
                        isLoading = true
                        viewModel.loadMore()
                    }
                }
            })

            swipeRefresh.setOnRefreshListener {
                viewModel.refreshMovies(1)
            }

            fabNewMovies.setOnClickListener {
                recyclerMovieList.smoothScrollToPosition(0)
                fabNewMovies.hide()
            }

            viewModel.movies.observe(viewLifecycleOwner) {
                movies = it
                adapter.submitList(it)

                /*Log.d("FragmentMovieList", "onViewCreated: list: ${list.size}")
                if (list.isEmpty()) {
                    Toast.makeText(requireContext(), "No more data", Toast.LENGTH_LONG).show()
                } else {
                    val set = HashSet(list).sortedByDescending {
                        it.date_uploaded_unix
                    }

                    Log.d("FragmentMovieList", "onViewCreated: movieSet: ${viewModel.movieSet.size}")
                    viewModel.movieSet.addAll(set)
                    adapter.submitList(viewModel.movieSet.toList())
                }*/

                /*if (once) {
                    recyclerMovieList.scrollToPosition(lastScrollPosition)
                    once = !once
                }*/

                /*if (it.size > moviesSize && moviesSize == 0) {
                    lifecycleScope.launch {
                        utils.updateMoviesSize(it.size)
                    }
                } else if (it.size > moviesSize) {
                    lifecycleScope.launch {
                        utils.updateMoviesSize(it.size)
                    }

                    fabNewMovies.show()
                }*/
            }

            viewModel.statusMovies.observe(viewLifecycleOwner) { status ->
                when (status) {
                    is ApiStatus.Done -> {
                        if (swipeRefresh.isRefreshing) {
                            swipeRefresh.isRefreshing = false
                        }
                        isLoading = false
                        status.message?.let { message ->
                            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                        }
                    }

                    is ApiStatus.Error -> {
                        if (swipeRefresh.isRefreshing) {
                            swipeRefresh.isRefreshing = false
                        }
                        isLoading = false
                        Toast.makeText(requireContext(), status.message, Toast.LENGTH_LONG).show()
                    }

                    is ApiStatus.Loading -> {
                        if (!swipeRefresh.isRefreshing) {
                            swipeRefresh.isRefreshing = true
                        }
                    }
                }
            }
        }
    }

    private fun addMenuProvider() {
        val toolbar: MaterialToolbar = requireActivity().findViewById(R.id.toolbar)
        toolbar.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_settings -> {
                        val action =
                            FragmentMovieListDirections.actionFragmentMovieListToSettingsFragment()
                        findNavController().navigate(action)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupSearch() {
        val adapter = SearchAdapter {
            val action = FragmentMovieListDirections.actionFragmentMovieListToFragmentMovieDetail(
                it.id, it.title
            )
            findNavController().navigate(action)
        }

        binding.apply {
            searchView.setupWithSearchBar(searchBar)
            recyclerSearch.adapter = adapter

            searchView.editText.setOnEditorActionListener { v, actionId, event ->
                if (v.text.isNotEmpty() && actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val query = v.text.toString().lowercase()

                    viewModel.searchMovies(query)
                }

                searchView.clearFocusAndHideKeyboard()
                true
            }

            viewModel.moviesSearch.observe(viewLifecycleOwner) {
                adapter.submitList(it)
            }

            viewModel.statusSearch.observe(viewLifecycleOwner) {
                when (it) {
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
    }

    private fun handleBackPressed() {
        val layoutManager = binding.recyclerMovieList.layoutManager as GridLayoutManager

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (binding.searchView.isShowing) {
                binding.searchView.hide()
            } else if(layoutManager.findFirstVisibleItemPosition() != 0) {
                binding.recyclerMovieList.scrollToPosition(0)
            }
            else {
                /*val scrollPosition = utils.getScrollPosition(binding.recyclerMovieList)

                lifecycleScope.launch {
                    utils.updateLastScrollPosition(scrollPosition)
                }*/

                requireActivity().finish()
            }
        }
    }
}