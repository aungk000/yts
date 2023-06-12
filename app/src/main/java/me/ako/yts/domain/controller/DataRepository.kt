package me.ako.yts.domain.controller


import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import me.ako.yts.data.datasource.model.MovieEntity
import me.ako.yts.data.network.ApiStatus
import me.ako.yts.data.network.model.MovieListResponse

interface DataRepository {
    fun loadMovies(limit: Int?, offset: Int?): Flow<List<MovieEntity>>
    fun loadMovies(): Flow<List<MovieEntity>>
    fun loadMovie(id: Int): Flow<MovieEntity?>
    suspend fun deleteMovies(vararg movies: MovieEntity)
    suspend fun deleteMovie(id: Int)

    suspend fun deleteAll()
    suspend fun refreshMovies(limit: Int, page: Int)
    suspend fun refreshMovie(id: Int)
}