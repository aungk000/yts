package me.ako.yts.data.datasource

import androidx.room.*
import me.ako.yts.data.datasource.model.MovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query("SELECT * FROM movie ORDER BY id DESC LIMIT :limit OFFSET :offset")
    fun getMoviesFlow(limit: Int?, offset: Int?): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movie ORDER BY id DESC")
    fun getMoviesFlow(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movie WHERE id = :id")
    fun getMovieFlow(id: Int): Flow<MovieEntity?>

    @Query("SELECT * FROM movie WHERE id = :id")
    fun getMovie(id: Int): MovieEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg movies: MovieEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMovies(movies: List<MovieEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMovie(movie: MovieEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateMovie(movie: MovieEntity)

    @Delete
    suspend fun deleteMovies(vararg movies: MovieEntity)

    @Query("DELETE FROM movie WHERE id = :id")
    suspend fun deleteMovie(id: Int)

    @Query("DELETE FROM movie")
    suspend fun deleteAll()
}