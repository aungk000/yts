package me.ako.yts.domain.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import me.ako.yts.domain.controller.DataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.ako.yts.data.datasource.model.MovieEntity
import me.ako.yts.data.network.MovieApi
import me.ako.yts.data.network.MovieApi.ApiStatus
import me.ako.yts.data.network.MovieApiService
import me.ako.yts.data.network.model.MovieDetail
import me.ako.yts.data.network.model.MovieList
import me.ako.yts.data.network.model.MovieSuggestion
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val repository: DataRepository,
    private val retrofit: MovieApiService
) : ViewModel() {
    private val _status = MutableLiveData<ApiStatus>()
    private val _statusSearch = MutableLiveData<ApiStatus>()
    private val _moviesSearch = MutableLiveData<List<MovieList>>()
    val status: LiveData<ApiStatus> = _status
    val statusSearch: LiveData<ApiStatus> = _statusSearch
    val moviesSearch: LiveData<List<MovieList>> = _moviesSearch
    val movies: LiveData<List<MovieEntity>> = repository.getMovies().asLiveData(Dispatchers.IO)

    init {
        refreshMovies()
        Log.d("AppViewModel", "AppViewModel created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("AppViewModel", "AppViewModel destroyed")
    }

    fun refreshMovies() {
        viewModelScope.launch {
            _status.value = ApiStatus.Loading
            try {
                repository.refreshMovies()
                _status.value = ApiStatus.Done
            } catch (e: Exception) {
                Log.d("AppViewModel", "refreshMovies Error: ${e.localizedMessage}")
                _status.value = ApiStatus.Error
            }
        }
    }

    fun searchMovies(query: String) {
        viewModelScope.launch {
            _statusSearch.value = ApiStatus.Loading
            try {
                val response = retrofit.searchMovies(query)
                Log.d("AppViewModel", "searchMovies Status: ${response.status}")
                Log.d("AppViewModel", "searchMovies Status Message: ${response.status_message}")
                _moviesSearch.value = response.data.movies
                _statusSearch.value = ApiStatus.Done
            } catch (e: Exception) {
                Log.d("AppViewModel", "searchMovies Error: ${e.localizedMessage}")
                _moviesSearch.value = listOf()
                _statusSearch.value = ApiStatus.Error
            }
        }
    }

    fun loadMovie(movie_id: Int): Pair<LiveData<MovieDetail>, LiveData<ApiStatus>> {
        val movie = MutableLiveData<MovieDetail>()
        val status = MutableLiveData<ApiStatus>(ApiStatus.Loading)
        viewModelScope.launch {
            try {
                val response = MovieApi.retrofitService.getMovie(movie_id)
                Log.d("AppViewModel", "loadMovie Status: ${response.status}")
                Log.d("AppViewModel", "loadMovie Status Message: ${response.status_message}")
                movie.value = response.data.movie
                status.value = ApiStatus.Done
            } catch (e: Exception) {
                Log.d("AppViewModel", "loadMovie Error: ${e.localizedMessage}")
                status.value = ApiStatus.Error
            }
        }

        return Pair(movie, status)
    }

    fun loadMovieSuggestion(movie_id: Int): LiveData<List<MovieSuggestion>> {
        val movies = MutableLiveData<List<MovieSuggestion>>()
        viewModelScope.launch {
            try {
                val response = MovieApi.retrofitService.getSuggestedMovies(movie_id)
                Log.d("AppViewModel", "loadMovieSuggestion Status: ${response.status}")
                Log.d(
                    "AppViewModel",
                    "loadMovieSuggestion Status Message: ${response.status_message}"
                )
                movies.value = response.data.movies
            } catch (e: Exception) {
                Log.d("AppViewModel", "loadMovieSuggestion Error: ${e.localizedMessage}")
            }
        }

        return movies
    }
}