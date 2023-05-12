package me.ako.yts.domain.controller

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import me.ako.yts.data.datasource.MovieDao
import me.ako.yts.data.datasource.model.MovieEntity
import me.ako.yts.data.network.MovieApiService
import me.ako.yts.data.network.model.asDatabaseModel
import me.ako.yts.domain.controller.DataRepository

class DataRepositoryImpl(private val dao: MovieDao, private val retrofit: MovieApiService) :
    DataRepository {

    override fun getMovies(): Flow<List<MovieEntity>> {
        return dao.getMovies()
    }

    override fun getMovie(id: Int): Flow<MovieEntity> {
        return dao.getMovie(id)
    }

    override suspend fun refreshMovies() {
        withContext(Dispatchers.IO) {
            try {
                val response = retrofit.getMovies()
                dao.insertAll(*response.asDatabaseModel())
            } catch (e: Exception) {
                Log.d("DataRepositoryImpl", "refreshMovies: ${e.localizedMessage}")
            }
        }
    }
}