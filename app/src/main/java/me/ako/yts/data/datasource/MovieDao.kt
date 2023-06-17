package me.ako.yts.data.datasource

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import me.ako.yts.data.datasource.model.MovieDetailEntity
import me.ako.yts.data.datasource.model.MovieListEntity

@Dao
interface MovieDao {
    @Query("SELECT * FROM movie_list ORDER BY id DESC LIMIT :limit OFFSET :offset")
    fun getMovieListFlow(limit: Int?, offset: Int?): Flow<List<MovieListEntity>>

    @Query("SELECT * FROM movie_list ORDER BY id DESC")
    fun getMovieListFlow(): Flow<List<MovieListEntity>>

    @Query("SELECT * FROM movie_detail WHERE id = :id")
    fun getMovieDetailFlow(id: Int): Flow<MovieDetailEntity?>

    @Query("SELECT * FROM movie_detail WHERE id = :id")
    fun getMovie(id: Int): MovieDetailEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg movies: MovieListEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(movies: List<MovieListEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMovieDetail(movie: MovieDetailEntity)

    @Update
    suspend fun updateMovieDetail(movie: MovieDetailEntity)

    @Delete
    suspend fun deleteMovieList(vararg movies: MovieListEntity)

    @Delete
    suspend fun deleteMovieDetail(vararg movies: MovieDetailEntity)

    @Query("DELETE FROM movie_list WHERE id = :id")
    suspend fun deleteMovieList(id: Int)

    @Query("DELETE FROM movie_detail WHERE id = :id")
    suspend fun deleteMovieDetail(id: Int)

    @Query("DELETE FROM movie_list")
    suspend fun deleteAllMovieList()

    @Query("DELETE FROM movie_detail")
    suspend fun deleteAllMovieDetail()
}