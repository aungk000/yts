package me.ako.yts.domain.controller


import kotlinx.coroutines.flow.Flow
import me.ako.yts.data.datasource.model.MovieDetailEntity
import me.ako.yts.data.datasource.model.MovieListEntity

interface DataRepository {
    fun loadMovies(limit: Int?, offset: Int?): Flow<List<MovieListEntity>>
    fun loadMovies(): Flow<List<MovieListEntity>>
    fun loadMovie(id: Int): Flow<MovieDetailEntity?>
    suspend fun deleteMovieList(vararg movies: MovieListEntity)
    suspend fun deleteMovieDetail(vararg movies: MovieDetailEntity)
    suspend fun deleteMovieList(id: Int)
    suspend fun deleteMovieDetail(id: Int)

    suspend fun deleteAllMovieList()
    suspend fun deleteAllMovieDetail()
    suspend fun refreshMovieList(limit: Int, page: Int)
    suspend fun refreshMovieDetail(id: Int)
}