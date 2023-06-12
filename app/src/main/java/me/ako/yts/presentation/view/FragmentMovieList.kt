package me.ako.yts.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.ako.yts.R
import me.ako.yts.data.datasource.model.MovieEntity
import me.ako.yts.data.network.ApiStatus
import me.ako.yts.data.network.model.Api
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

    private var isLoading = false
    private var movies = listOf<MovieEntity>()
    private var searchSort = Api.Endpoint.Parameter.Sort.DownloadCount.type
    private var searchOrder = Api.Endpoint.Parameter.Order.Desc.type

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

        //viewModel.refreshMovies(1)

        handleBackPressed()
        setupSearch()

        viewModel.sort.observe(viewLifecycleOwner) {
            searchSort = it
        }

        viewModel.order.observe(viewLifecycleOwner) {
            searchOrder = it
        }

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            appViewModel = viewModel

            val adapter = MovieAdapter {
                findNavController().navigate(
                    FragmentMovieListDirections.actionFragmentMovieListToFragmentMovieDetail(
                        it.id,
                        it.title
                    )
                )
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

                    if (!isLoading &&
                        fabNewMovies.isVisible &&
                        layoutManager.findFirstCompletelyVisibleItemPosition() == 0
                    ) {
                        fabNewMovies.hide()
                    }
                }
            })

            swipeRefresh.setOnRefreshListener {
                viewModel.refreshMovies(1)
            }

            viewModel.movies.observe(viewLifecycleOwner) {
                movies = it
                adapter.submitList(it)
            }

            viewModel.statusMovies.observe(viewLifecycleOwner) { status ->
                when (status) {
                    is ApiStatus.Done -> {
                        isLoading = false
                    }

                    is ApiStatus.Error -> {
                        isLoading = false
                        //Toast.makeText(requireContext(), status.message, Toast.LENGTH_LONG).show()
                    }

                    is ApiStatus.Loading -> {}
                }
            }

            fabNewMovies.setOnClickListener {
                viewModel.setNewMovies(false)
                recyclerMovieList.smoothScrollToPosition(0)
            }

            viewModel.newMovies.observe(viewLifecycleOwner) { isNew ->
                if (isNew) {
                    fabNewMovies.show()
                } else {
                    fabNewMovies.hide()
                }
            }
        }
    }

    private fun setupSearch() {
        binding.apply {
            searchView.apply {
                setupWithSearchBar(searchBar)

                editText.setOnEditorActionListener { v, actionId, event ->
                    if (v.text.isNotEmpty() && actionId == EditorInfo.IME_ACTION_SEARCH) {
                        searchBar.text = searchView.text
                        val query = v.text.toString().lowercase()
                        viewModel.searchMovies(query)
                    }

                    searchView.clearFocusAndHideKeyboard()
                    true
                }

                inflateMenu(R.menu.menu_search)
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.menu_search_sort -> {
                            sortDialog()
                            true
                        }

                        R.id.menu_search_order -> {
                            orderDialog()
                            true
                        }

                        else -> false
                    }
                }
            }

            searchBar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_settings -> {
                        findNavController().navigate(
                            FragmentMovieListDirections.actionFragmentMovieListToFragmentSettings()
                        )
                        true
                    }

                    else -> false
                }
            }

            val adapter = SearchAdapter {
                findNavController().navigate(
                    FragmentMovieListDirections.actionFragmentMovieListToFragmentMovieDetail(
                        it.id, it.title
                    )
                )
            }
            recyclerSearch.adapter = adapter

            viewModel.moviesSearch.observe(viewLifecycleOwner) {
                adapter.submitList(null)
                adapter.submitList(it)
            }

            viewModel.statusSearch.observe(viewLifecycleOwner) {
                when (it) {
                    is ApiStatus.Done -> {}

                    is ApiStatus.Error -> {
                        if (searchView.isShowing) {
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                        }
                    }

                    is ApiStatus.Loading -> {}
                }
            }
        }
    }

    private fun sortDialog() {
        val sortEntries = resources.getStringArray(R.array.sort_entries)
        val sortValues = resources.getStringArray(R.array.sort_values)
        val checkedItem = sortValues.indexOf(searchSort)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Sort by")
            .setSingleChoiceItems(sortEntries, checkedItem) { v, which ->
                lifecycleScope.launch(Dispatchers.IO) {
                    utils.updateSort(sortValues[which])
                }

                v.dismiss()
            }
            .show()
    }

    private fun orderDialog() {
        val orderEntries = resources.getStringArray(R.array.order_entries)
        val orderValues = resources.getStringArray(R.array.order_values)
        val checkedItem = orderValues.indexOf(searchOrder)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Order by")
            .setSingleChoiceItems(orderEntries, checkedItem) { v, which ->
                lifecycleScope.launch(Dispatchers.IO) {
                    utils.updateOrder(orderValues[which])
                }

                v.dismiss()
            }
            .show()
    }

    private fun handleBackPressed() {
        val layoutManager = binding.recyclerMovieList.layoutManager as GridLayoutManager

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (binding.searchView.isShowing) {
                binding.searchView.hide()
            } else if (movies.isNotEmpty() && layoutManager.findFirstVisibleItemPosition() != 0) {
                binding.recyclerMovieList.scrollToPosition(0)
            } else {
                requireActivity().finish()
            }
        }
    }
}