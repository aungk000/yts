package me.ako.yts.domain.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import me.ako.yts.data.datasource.model.MovieEntity
import me.ako.yts.data.network.ApiStatus
import me.ako.yts.data.network.MovieApiService
import me.ako.yts.data.network.model.MovieList
import me.ako.yts.data.network.model.MovieSuggestion
import me.ako.yts.domain.controller.DataRepository
import me.ako.yts.domain.util.Base
import me.ako.yts.domain.util.Utils
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val repository: DataRepository,
    private val retrofit: MovieApiService,
    private val utils: Utils
) : ViewModel() {
    private val page = MutableStateFlow(1)
    private var limit = MutableStateFlow(20)
    private var offset = MutableStateFlow(0)
    private val movieSet = mutableSetOf<MovieEntity>()
    private var isEmptyList = false

    private val _statusMovies = MutableLiveData<ApiStatus>()
    private val _movies = MutableLiveData<List<MovieEntity>>()
    private val _statusSearch = MutableLiveData<ApiStatus>()
    private val _moviesSearch = MutableLiveData<List<MovieList>?>()
    @OptIn(ExperimentalCoroutinesApi::class)
    private val _moviesFlow = offset.flatMapLatest {
        repository.loadMovies(limit.value, it)
    }
    private val _statusMovieDetailNetwork = MutableLiveData<ApiStatus>()
    private val _statusMovieDetailDatabase = MutableLiveData<ApiStatus>()
    private val _statusMovieSuggestions = MutableLiveData<ApiStatus>()
    private val _newMovies = MutableLiveData(false)

    val statusMovieSuggestions: LiveData<ApiStatus> = _statusMovieSuggestions
    val statusMovieDetail = Base.CombineLiveData<ApiStatus, ApiStatus, ApiStatus>(
        _statusMovieDetailNetwork,
        _statusMovieDetailDatabase
    ) { network, database ->
        if(network is ApiStatus.Done || database is ApiStatus.Done) {
            ApiStatus.Done("")
        } else if (network is ApiStatus.Error && database is ApiStatus.Error) {
            ApiStatus.Error(network.message)
        } else {
            ApiStatus.Loading("")
        }
    }
    val statusMovies: LiveData<ApiStatus> = _statusMovies
    val movies: LiveData<List<MovieEntity>> = _movies
    val statusSearch: LiveData<ApiStatus> = _statusSearch
    val moviesSearch: LiveData<List<MovieList>?> = _moviesSearch
    val sort = utils.sortFlow.asLiveData()
    val order = utils.orderFlow.asLiveData()
    val newMovies: LiveData<Boolean> = _newMovies

    init {
        refreshMovies(1)
        loadMovies()

        Log.d("AppViewModel", "AppViewModel: created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("AppViewModel", "AppViewModel: destroyed")
    }

    fun refreshMovies(page: Int) {
        _statusMovies.value = ApiStatus.Loading("Loading movies")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.refreshMovies(limit.value, page)
                _statusMovies.postValue(ApiStatus.Done(null))
            } catch (e: Exception) {
                Log.d("AppViewModel", "refreshMovies: ${e.message}")
                _statusMovies.postValue(ApiStatus.Error(e.message))
            }
        }
    }

    private fun loadMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _moviesFlow.collectLatest { list ->
                    if (list.isEmpty()) {
                        isEmptyList = true
                    } else {
                        isEmptyList = false

                        filterNewMovies(list)

                        /*
                            NOTICE: Data are retrieved by increment offset so if sorting by
                            other property may result in lost of items in previous page.
                            Another way to solve this is to increase the item limit of API.
                        */

                        movieSet.addAll(list)

                        val sorted = movieSet.distinctBy {
                            it.id
                        }.sortedByDescending {
                            it.id
                        }
                        _movies.postValue(sorted)
                    }
                }
            } catch (e: Exception) {
                Log.e("AppViewModel", "loadMovies: ${e.message}")
            }
        }
    }

    fun refreshMovie(id: Int) {
        _statusMovieDetailNetwork.value = ApiStatus.Loading("Loading movie detail")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.refreshMovie(id)
                _statusMovieDetailNetwork.postValue(ApiStatus.Done("Finished loading movie detail"))
            } catch (e: Exception) {
                Log.d("AppViewModel", "refreshMovie: ${e.message}")
                _statusMovieDetailNetwork.postValue(ApiStatus.Error(e.message))
            }
        }
    }

    fun loadMovie(movie_id: Int): LiveData<MovieEntity?> {
        val movie = MutableLiveData<MovieEntity>()
        _statusMovieDetailDatabase.value = ApiStatus.Loading("Loading movie detail")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.loadMovie(movie_id).collectLatest {
                    if (it != null) {
                        movie.postValue(it)
                        _statusMovieDetailDatabase.postValue(ApiStatus.Done("Finished loading movie detail"))
                    } else {
                        _statusMovieDetailDatabase.postValue(ApiStatus.Error("Error no movie found"))
                    }
                }
            } catch (e: Exception) {
                Log.d("AppViewModel", "loadMovie: ${e.message}")
                _statusMovieDetailDatabase.postValue(ApiStatus.Error(e.message))
            }
        }
        return movie
    }

    fun setNewMovies(isNew: Boolean) {
        _newMovies.value = isNew
    }

    fun clearMovies() {
        movieSet.clear()
        _movies.value = listOf()
        page.value = 1
        offset.value = 0
    }

    private fun filterNewMovies(list: List<MovieEntity>) {
        if (movieSet.isNotEmpty()) {
            val currentList = movieSet.distinctBy {
                it.id
            }.sortedByDescending {
                it.id
            }
            val lastId = currentList.first().id
            val newIds = list.map {
                it.id
            }.filter {
                it > lastId
            }

            if (newIds.isNotEmpty()) {
                _newMovies.postValue(true)
            }
        }
    }

    fun loadMore() {
        if (isEmptyList) {
            _statusMovies.value = ApiStatus.Done("No more data")
        } else {
            page.value += 1
            offset.value += 20
        }

        refreshMovies(page.value)
    }

    fun searchMovies(query: String) {
        _statusSearch.value = ApiStatus.Loading("Searching movies")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = retrofit.searchMovies(query, sort.value!!, order.value!!)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _moviesSearch.postValue(it.data.movies)
                        _statusSearch.postValue(ApiStatus.Done(it.status_message))
                    }
                } else {
                    throw Exception(response.message())
                }
            } catch (e: Exception) {
                Log.e("AppViewModel", "searchMovies: ${e.localizedMessage}")
                _moviesSearch.postValue(listOf())
                _statusSearch.postValue(ApiStatus.Error(e.localizedMessage))
            }
        }
    }

    fun loadMovieSuggestions(movie_id: Int): LiveData<List<MovieSuggestion>> {
        val list = MutableLiveData<List<MovieSuggestion>>()

        _statusMovieSuggestions.value = ApiStatus.Loading(null)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                retrofit.getMovieSuggestions(movie_id).body()?.let { response ->
                    list.postValue(response.data.movies)
                    _statusMovieSuggestions.postValue(ApiStatus.Done(response.status_message))
                }
            } catch (e: Exception) {
                Log.e("AppViewModel", "loadMovieSuggestions: ${e.message}")
                list.postValue(listOf())
                _statusMovieSuggestions.postValue(ApiStatus.Error(e.message))
            }
        }

        return list
    }
}