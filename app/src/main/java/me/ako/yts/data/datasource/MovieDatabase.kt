package me.ako.yts.data.datasource

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import me.ako.yts.data.datasource.model.MovieEntity
import me.ako.yts.data.network.model.Torrent


@Database(
    entities = [MovieEntity::class],
    version = 1,
    exportSchema = true,
    /*autoMigrations = [
        AutoMigration(1, 2)
    ]*/
)
@TypeConverters(MovieDatabase.Converters::class)
abstract class MovieDatabase : RoomDatabase() {
    abstract val dao: MovieDao

    companion object {
        @Volatile
        private var INSTANCE: MovieDatabase? = null

        fun getInstance(context: Context): MovieDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    MovieDatabase::class.java,
                    "movie_db"
                )
                    .build()

                INSTANCE = instance

                instance
            }
        }
    }

    object Converters {
        @TypeConverter
        fun stringToList(value: String?): List<String>? {
            val listType = object : TypeToken<List<String>>() {}.type
            return Gson().fromJson(value, listType)
        }

        @TypeConverter
        fun listToString(list: List<String>?): String? {
            return Gson().toJson(list)
        }

        @TypeConverter
        fun stringToTorrent(value: String?): Torrent? {
            val listType = object : TypeToken<Torrent>() {}.type
            return Gson().fromJson(value, listType)
        }

        @TypeConverter
        fun torrentToString(value: Torrent?): String? {
            return Gson().toJson(value)
        }

        @TypeConverter
        fun stringToTorrentList(value: String?): List<Torrent>? {
            val listType = object : TypeToken<List<Torrent>>() {}.type
            return Gson().fromJson(value, listType)
        }

        @TypeConverter
        fun torrentListToString(list: List<Torrent>?): String? {
            return Gson().toJson(list)
        }
    }
}