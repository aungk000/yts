package me.ako.yts.data.datasource

import androidx.room.*
import me.ako.yts.data.datasource.model.MovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query("SELECT * FROM movie ORDER BY date_uploaded_unix DESC LIMIT :limit OFFSET :offset")
    fun getMovies(limit: Int?, offset: Int?): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movie ORDER BY date_uploaded_unix DESC")
    fun getMovies(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movie WHERE id = :id")
    fun getMovie(id: Int): Flow<MovieEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg movie: MovieEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMovies(movies: List<MovieEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMovie(movie: MovieEntity)

    @Update
    suspend fun updateMovie(movie: MovieEntity)

    @Delete
    suspend fun deleteMovie(movie: MovieEntity)
}