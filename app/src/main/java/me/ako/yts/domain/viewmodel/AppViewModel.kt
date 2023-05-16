package me.ako.yts.domain.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.ako.yts.data.datasource.model.MovieEntity
import me.ako.yts.data.network.MovieApi.ApiStatus
import me.ako.yts.data.network.MovieApiService
import me.ako.yts.data.network.model.MovieDetail
import me.ako.yts.data.network.model.MovieList
import me.ako.yts.data.network.model.MovieSuggestion
import me.ako.yts.domain.controller.DataRepository
import me.ako.yts.domain.util.Base
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val repository: DataRepository,
    private val retrofit: MovieApiService
) : ViewModel() {
    private var page = 1
    private var limit = 20
    private var offset = 0
    private var movieSet = mutableSetOf<MovieEntity>()

    private val _statusMovies = MutableLiveData<ApiStatus>()
    private val _movies = MutableLiveData<List<MovieEntity>>()
    private val _statusSearch = MutableLiveData<ApiStatus>()
    private val _moviesSearch = MutableLiveData<List<MovieList>>()

    private val _statusMovieDetail = MutableLiveData<ApiStatus>()
    val statusMovieDetail: LiveData<ApiStatus> = _statusMovieDetail

    val statusMovies: LiveData<ApiStatus> = _statusMovies
    val movies: LiveData<List<MovieEntity>> = _movies
    val statusSearch: LiveData<ApiStatus> = _statusSearch
    val moviesSearch: LiveData<List<MovieList>> = _moviesSearch

    init {
        refreshMovies()
        Log.d("AppViewModel", "AppViewModel: created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("AppViewModel", "AppViewModel: destroyed")
    }

    private fun refreshMovies() {
        viewModelScope.launch {
            _statusMovies.value = ApiStatus.Loading("Loading movies")
            try {
                repository.refreshMovies(limit, page)
                loadMovies()
                _statusMovies.value = ApiStatus.Done(null)
            } catch (e: Exception) {
                Log.d("AppViewModel", "refreshMovies: ${e.localizedMessage}")
                _statusMovies.value = ApiStatus.Error(e.localizedMessage)
            }
        }
    }

    fun refreshMovies(page: Int) {
        viewModelScope.launch {
            _statusMovies.value = ApiStatus.Loading("Loading movies")
            try {
                repository.refreshMovies(limit, page)
                loadMovies()
                _statusMovies.value = ApiStatus.Done(null)
            } catch (e: Exception) {
                Log.d("AppViewModel", "refreshMovies: ${e.localizedMessage}")
                _statusMovies.value = ApiStatus.Error(e.localizedMessage)
            }
        }
    }

    private fun loadMovies() {
        viewModelScope.launch {
            try {
                repository.loadMovies(limit, offset).collectLatest { list ->
                    if(list.isEmpty()) {
                        page -= 1
                        offset -= 20
                        _statusMovies.value = ApiStatus.Done("No more data")
                    } else {
                        movieSet.addAll(list)
                        _movies.value = movieSet.toList()
                    }

                    Log.d("AppViewModel", "loadMovies: list: ${list.size}")
                }
            } catch (e: Exception) {
                Log.d("AppViewModel", "loadMovies: ${e.localizedMessage}")
            }
        }
    }

    fun loadMore() {
        page += 1
        // limit += 20
        offset += 20

        refreshMovies()
    }

    fun searchMovies(query: String) {
        viewModelScope.launch {
            _statusSearch.value = ApiStatus.Loading("Searching movies")
            try {
                retrofit.searchMovies(query).body()?.let { response ->
                    _moviesSearch.value = response.data.movies
                    _statusSearch.value = ApiStatus.Done(response.status_message)
                }
            } catch (e: Exception) {
                Log.d("AppViewModel", "searchMovies: ${e.localizedMessage}")
                _moviesSearch.value = listOf()
                _statusSearch.value = ApiStatus.Error(e.localizedMessage)
            }
        }
    }

    fun loadMovie(movie_id: Int): LiveData<MovieDetail> {
        val movie = MutableLiveData<MovieDetail>()

        viewModelScope.launch {
            _statusMovieDetail.value = ApiStatus.Loading(null)
            try {
                retrofit.getMovie(movie_id).body()?.let { response ->
                    response.data?.movie?.let {
                        movie.value = it
                    }
                }
                _statusMovieDetail.value = ApiStatus.Done(null)
            } catch (e: Exception) {
                Log.d("AppViewModel", "loadMovie: ${e.localizedMessage}")
                _statusMovieDetail.value = ApiStatus.Error(e.localizedMessage)
            }
        }

        return movie
    }

    fun loadMovieSuggestions(movie_id: Int): LiveData<List<MovieSuggestion>> {
        val list = MutableLiveData<List<MovieSuggestion>>()

        viewModelScope.launch {
            try {
                retrofit.getMovieSuggestions(movie_id).body()?.let { response ->
                    list.value = response.data.movies
                }
            } catch (e: Exception) {
                Log.d("AppViewModel", "loadMovieSuggestions: ${e.localizedMessage}")
                list.value = listOf()
            }
        }

        return list
    }
}