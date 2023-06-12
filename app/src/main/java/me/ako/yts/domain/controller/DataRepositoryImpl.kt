package me.ako.yts.domain.controller

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import me.ako.yts.data.datasource.MovieDao
import me.ako.yts.data.datasource.model.MovieEntity
import me.ako.yts.data.network.MovieApiService
import me.ako.yts.data.network.model.asDatabaseModel

class DataRepositoryImpl(private val dao: MovieDao, private val retrofit: MovieApiService) :
    DataRepository {

    override fun loadMovies(limit: Int?, offset: Int?): Flow<List<MovieEntity>> {
        return dao.getMovies(limit, offset)
    }
    override fun loadMovies(): Flow<List<MovieEntity>> {
        return dao.getMovies()
    }

    override fun loadMovie(id: Int): Flow<MovieEntity> {
        return dao.getMovie(id)
    }

    override suspend fun deleteMovies(vararg movies: MovieEntity) {
        dao.deleteMovies(*movies)
    }

    override suspend fun deleteMovie(id: Int) {
        dao.deleteMovie(id)
    }

    override suspend fun refreshMovies(limit: Int, page: Int) {
        withContext(Dispatchers.IO) {
            try {
                retrofit.getMovies(limit, page).body()?.let { response ->
                    response.asDatabaseModel()?.let { list ->
                        dao.insertMovies(list)
                    }
                }
            } catch (e: Exception) {
                Log.d("DataRepositoryImpl", "refreshMovies: ${e.message}")
            }
        }
    }

    override suspend fun refreshMovie(id: Int) {
        withContext(Dispatchers.IO) {
            try {
                retrofit.getMovie(id).body()?.let { response ->
                    response.asDatabaseModel().let { movie ->
                        dao.updateMovie(movie)
                    }
                }
            } catch (e: Exception) {
                Log.d("DataRepositoryImpl", "refreshMovie: ${e.message}")
            }
        }
    }
}