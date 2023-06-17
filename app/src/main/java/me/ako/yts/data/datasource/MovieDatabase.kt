package me.ako.yts.data.datasource

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import me.ako.yts.data.datasource.model.MovieDetailEntity
import me.ako.yts.data.datasource.model.MovieListEntity
import me.ako.yts.data.network.model.Cast
import me.ako.yts.data.network.model.Torrent

@Database(
    entities = [MovieListEntity::class, MovieDetailEntity::class],
    version = 2,
    exportSchema = true,
    autoMigrations = [
        //AutoMigration(1, 2, spec = MovieDatabase.DatabaseAutoMigration::class)
    ]
)
@TypeConverters(MovieDatabase.Converters::class)
abstract class MovieDatabase : RoomDatabase() {
    abstract val dao: MovieDao
    //private val moshiParser = MovieApi.moshiParser
    private val gson = Gson()

    val migration = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Add new column
            //database.execSQL("ALTER TABLE movie ADD COLUMN title TEXT")
            // Rename a column
            //database.execSQL("ALTER TABLE movie RENAME COLUMN old_title TO title")

            val cursor = database.query("SELECT id, title FROM movie")
            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
                val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))

                // Convert oldTitle to Title object
                val titleObject = MovieDetailEntity.Title(
                    title = title,
                    titleLong = null,
                    titleEnglish = null
                )

                // Update the new title column with the converted value
                val contentValues = ContentValues().apply {
                    val json = gson.toJson(titleObject)
                    put("title", json)
                }

                database.update(
                    "movie",
                    SQLiteDatabase.CONFLICT_REPLACE,
                    contentValues,
                    "id=?",
                    arrayOf(id.toString())
                )
            }

            cursor.close()
            // Drop old column name
            //database.execSQL("ALTER TABLE movie DROP COLUMN title")
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: MovieDatabase? = null

        fun getInstance(context: Context): MovieDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    MovieDatabase::class.java,
                    "movie_db"
                ).build()

                INSTANCE = instance

                instance
            }
        }
    }

    @DeleteColumn.Entries()
    class DatabaseAutoMigration : AutoMigrationSpec {
        override fun onPostMigrate(db: SupportSQLiteDatabase) {
            super.onPostMigrate(db)
            Log.d("MovieDatabase", "onPostMigrate: DatabaseAutoMigration ${db.version}")
        }
    }

    class Converters {
        private val gson = Gson()

        @TypeConverter
        fun stringToList(json: String?): List<String>? {
            return gson.fromJson<List<String>>(
                json,
                object : TypeToken<List<String>>() {}.type
            )
        }

        @TypeConverter
        fun listToString(obj: List<String>?): String? {
            return obj?.let {
                gson.toJson(it)
            }
        }

        @TypeConverter
        fun stringToMovieDetailImage(json: String?): MovieDetailEntity.Image? {
            return gson.fromJson<MovieDetailEntity.Image>(
                json,
                object : TypeToken<MovieDetailEntity.Image>() {}.type
            )
        }

        @TypeConverter
        fun movieDetailImageToString(obj: MovieDetailEntity.Image?): String? {
            return obj?.let {
                gson.toJson(it)
            }
        }

        @TypeConverter
        fun stringToMovieListImage(json: String?): MovieListEntity.Image? {
            return gson.fromJson<MovieListEntity.Image>(
                json,
                object : TypeToken<MovieListEntity.Image>() {}.type
            )
        }

        @TypeConverter
        fun movieListImageToString(obj: MovieListEntity.Image?): String? {
            return obj?.let {
                gson.toJson(it)
            }
        }

        @TypeConverter
        fun stringToMovieDetailTitle(json: String?): MovieDetailEntity.Title? {
            return gson.fromJson<MovieDetailEntity.Title>(
                json,
                object : TypeToken<MovieDetailEntity.Title>() {}.type
            )
        }

        @TypeConverter
        fun movieDetailTitleToString(obj: MovieDetailEntity.Title?): String? {
            return obj?.let {
                gson.toJson(it)
            }
        }

        @TypeConverter
        fun stringToMovieListTitle(json: String?): MovieListEntity.Title? {
            return gson.fromJson<MovieListEntity.Title>(
                json,
                object : TypeToken<MovieListEntity.Title>() {}.type
            )
        }

        @TypeConverter
        fun movieListTitleToString(obj: MovieListEntity.Title?): String? {
            return obj?.let {
                gson.toJson(it)
            }
        }

        @TypeConverter
        fun stringToMovieDetailDescription(json: String?): MovieDetailEntity.Description? {
            return gson.fromJson<MovieDetailEntity.Description>(
                json,
                object : TypeToken<MovieDetailEntity.Description>() {}.type
            )
        }

        @TypeConverter
        fun movieDetailDescriptionToString(obj: MovieDetailEntity.Description?): String? {
            return obj?.let {
                gson.toJson(it)
            }
        }

        @TypeConverter
        fun stringToMovieListDescription(json: String?): MovieListEntity.Description? {
            return gson.fromJson<MovieListEntity.Description>(
                json,
                object : TypeToken<MovieListEntity.Description>() {}.type
            )
        }

        @TypeConverter
        fun movieListDescriptionToString(obj: MovieListEntity.Description?): String? {
            return obj?.let {
                gson.toJson(it)
            }
        }

        @TypeConverter
        fun stringToTorrentList(json: String?): List<Torrent>? {
            return gson.fromJson<List<Torrent>>(
                json,
                object : TypeToken<List<Torrent>>() {}.type
            )
        }

        @TypeConverter
        fun torrentListToString(obj: List<Torrent>?): String? {
            return obj?.let {
                gson.toJson(it)
            }
        }

        @TypeConverter
        fun stringToCastList(json: String?): List<Cast>? {
            return gson.fromJson(
                json,
                object : TypeToken<List<Cast>>() {}.type
            )
        }

        @TypeConverter
        fun castListToString(obj: List<Cast>?): String? {
            return obj?.let {
                gson.toJson(it)
            }
        }
    }
}