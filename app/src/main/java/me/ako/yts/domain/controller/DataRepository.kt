package me.ako.yts.domain.controller


import kotlinx.coroutines.flow.Flow
import me.ako.yts.data.datasource.model.MovieEntity
import me.ako.yts.data.network.model.MovieListResponse

interface DataRepository {
    fun loadMovies(limit: Int?, offset: Int?): Flow<List<MovieEntity>>
    fun loadMovies(): Flow<List<MovieEntity>>
    fun loadMovie(id: Int): Flow<MovieEntity>
    suspend fun refreshMovies(limit: Int, page: Int)
}