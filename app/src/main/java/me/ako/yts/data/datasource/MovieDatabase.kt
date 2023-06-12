package me.ako.yts.data.datasource

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import com.squareup.moshi.Types
import me.ako.yts.data.datasource.model.MovieEntity
import me.ako.yts.data.network.MovieApi
import me.ako.yts.data.network.model.Cast
import me.ako.yts.data.network.model.Torrent


@Database(
    entities = [MovieEntity::class],
    version = 2,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(1, 2)
    ]
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

    class MyAutoMigration : AutoMigrationSpec

    class Converters {
        private val moshiParser = MovieApi.moshiParser

        @TypeConverter
        fun stringToList(json: String): List<String>? {
            return moshiParser.fromJson(
                json,
                Types.newParameterizedType(List::class.java, String::class.java)
            )
        }

        @TypeConverter
        fun listToString(list: List<String>): String? {
            return moshiParser.toJson(
                list,
                Types.newParameterizedType(List::class.java, String::class.java)
            )
        }

        @TypeConverter
        fun stringToTorrentList(json: String): List<Torrent>? {
            return moshiParser.fromJson(
                json,
                Types.newParameterizedType(List::class.java, Torrent::class.java)
            )
        }

        @TypeConverter
        fun torrentListToString(list: List<Torrent>): String? {
            return moshiParser.toJson(
                list,
                Types.newParameterizedType(List::class.java, Torrent::class.java)
            )
        }

        @TypeConverter
        fun stringToCastList(json: String): List<Cast>? {
            return moshiParser.fromJson(
                json,
                Types.newParameterizedType(List::class.java, Cast::class.java)
            )
        }

        @TypeConverter
        fun castListToString(list: List<Cast>?): String? {
            return moshiParser.toJson(
                list,
                Types.newParameterizedType(List::class.java, Cast::class.java)
            )
        }
    }
}