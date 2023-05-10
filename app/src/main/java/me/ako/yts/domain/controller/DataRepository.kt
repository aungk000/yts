package me.ako.yts.domain.controller


import kotlinx.coroutines.flow.Flow
import me.ako.yts.data.datasource.model.MovieEntity
import me.ako.yts.data.network.model.MovieListResponse

interface DataRepository {
    fun getMovies(): Flow<List<MovieEntity>>
    fun getMovie(id: Int): Flow<MovieEntity>
    suspend fun refreshMovies()
}