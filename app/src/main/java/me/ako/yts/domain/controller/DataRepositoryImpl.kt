package me.ako.yts.domain.controller

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import me.ako.yts.data.datasource.MovieDao
import me.ako.yts.data.datasource.model.MovieDetailEntity
import me.ako.yts.data.datasource.model.MovieListEntity
import me.ako.yts.data.network.MovieApiService
import me.ako.yts.data.network.model.asDatabaseModel

class DataRepositoryImpl(private val dao: MovieDao, private val retrofit: MovieApiService) :
    DataRepository {

    override fun loadMovies(limit: Int?, offset: Int?): Flow<List<MovieListEntity>> {
        return dao.getMovieListFlow(limit, offset)
    }

    override fun loadMovies(): Flow<List<MovieListEntity>> {
        return dao.getMovieListFlow()
    }

    override fun loadMovie(id: Int): Flow<MovieDetailEntity?> {
        return dao.getMovieDetailFlow(id)
    }

    override suspend fun deleteMovieList(vararg movies: MovieListEntity) {
        dao.deleteMovieList(*movies)
    }

    override suspend fun deleteMovieList(id: Int) {
        dao.deleteMovieList(id)
    }

    override suspend fun deleteMovieDetail(vararg movies: MovieDetailEntity) {
        dao.deleteMovieDetail(*movies)
    }

    override suspend fun deleteMovieDetail(id: Int) {
        dao.deleteMovieDetail(id)
    }

    override suspend fun deleteAllMovieList() {
        dao.deleteAllMovieList()
    }

    override suspend fun deleteAllMovieDetail() {
        dao.deleteAllMovieDetail()
    }

    override suspend fun refreshMovieList(limit: Int, page: Int) {
        withContext(Dispatchers.IO) {
            retrofit.getMovies(limit, page).body()?.let { response ->
                response.asDatabaseModel()?.let { list ->
                    dao.insertAll(list)
                }
            }
        }
    }

    override suspend fun refreshMovieDetail(id: Int) {
        withContext(Dispatchers.IO) {
            retrofit.getMovie(id).body()?.let { response ->
                response.asDatabaseModel().let { movie ->
                    val movieDb = dao.getMovie(id)
                    if (movieDb != null) {
                        dao.updateMovieDetail(movie)
                    } else {
                        dao.insertMovieDetail(movie)
                    }
                }
            }
        }
    }
}