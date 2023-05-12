package me.ako.yts.domain.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
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
import me.ako.yts.domain.util.Base
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val repository: DataRepository,
    private val retrofit: MovieApiService
) : ViewModel() {
    private val _status = MutableLiveData<ApiStatus>()
    private val _moviesSearch = MutableLiveData<List<MovieList>>()
    val status: LiveData<ApiStatus> = _status
    val moviesSearch: LiveData<List<MovieList>> = _moviesSearch
    val movies: LiveData<List<MovieEntity>> = repository.getMovies().asLiveData(Dispatchers.IO)

    init {
        refreshMovies()
    }

    fun refreshMovies() {
        viewModelScope.launch {
            _status.value = ApiStatus.Loading("Loading movies")
            try {
                repository.refreshMovies()
                _status.value = ApiStatus.Done("Finished loading movies")
            } catch (e: Exception) {
                _status.value = ApiStatus.Error(e.localizedMessage)
            }
        }
    }

    fun searchMovies(query: String): LiveData<ApiStatus> {
        val status = MutableLiveData<ApiStatus>()

        viewModelScope.launch {
            status.value = ApiStatus.Loading("Searching movies")
            try {
                val response = retrofit.searchMovies(query)
                _moviesSearch.value = response.data.movies
                status.value = ApiStatus.Done(response.status_message)
            } catch (e: Exception) {
                _moviesSearch.value = listOf()
                status.value = ApiStatus.Error("Try different query")
            }
        }

        return status
    }

    fun loadMovie(movie_id: Int): Base.PairLiveData<ApiStatus, MovieDetail> {
        val status = MutableLiveData<ApiStatus>()
        val movie = MutableLiveData<MovieDetail>()

        viewModelScope.launch {
            status.value = ApiStatus.Loading("Loading movie detail")
            try {
                val response = MovieApi.retrofitService.getMovie(movie_id)
                movie.value = response.data.movie
                status.value = ApiStatus.Done(response.status_message)
            } catch (e: Exception) {
                Log.d("AppViewModel", "loadMovie: ${e.localizedMessage}")
                status.value = ApiStatus.Error(e.localizedMessage)
            }
        }

        return Base.PairLiveData<ApiStatus, MovieDetail>(status, movie)
    }

    fun loadMovieSuggestions(movie_id: Int): Base.PairLiveData<ApiStatus, List<MovieSuggestion>> {
        val status = MutableLiveData<ApiStatus>()
        val movies = MutableLiveData<List<MovieSuggestion>>()
        viewModelScope.launch {
            status.value = ApiStatus.Loading("Loading similar movies")
            try {
                val response = MovieApi.retrofitService.getMovieSuggestions(movie_id)
                movies.value = response.data.movies
                status.value = ApiStatus.Done(response.status_message)
            } catch (e: Exception) {
                Log.d("AppViewModel", "loadMovieSuggestions: ${e.localizedMessage}")
                movies.value = listOf()
                status.value = ApiStatus.Error(e.localizedMessage)
            }
        }

        return Base.PairLiveData<ApiStatus, List<MovieSuggestion>>(status, movies)
    }
}