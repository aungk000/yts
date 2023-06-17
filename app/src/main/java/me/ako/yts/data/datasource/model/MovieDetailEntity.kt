package me.ako.yts.data.datasource.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import me.ako.yts.data.network.model.Cast
import me.ako.yts.data.network.model.Torrent

@Entity(tableName = "movie_detail")
data class MovieDetailEntity(
    @PrimaryKey
    var id: Int,
    @ColumnInfo(name = "url", defaultValue = "NULL")
    var url: String? = null,
    @ColumnInfo(name = "imdb_code", defaultValue = "NULL")
    var imdb_code: String? = null,
    @ColumnInfo(name = "title", defaultValue = "NULL")
    var title: Title? = null,
    @ColumnInfo(name = "slug", defaultValue = "NULL")
    var slug: String? = null,
    @ColumnInfo(name = "year", defaultValue = "NULL")
    var year: Int? = null,
    @ColumnInfo(name = "rating", defaultValue = "NULL")
    var rating: Double? = null,
    @ColumnInfo(name = "runtime", defaultValue = "NULL")
    var runtime: Long? = null,
    @ColumnInfo(name = "genres", defaultValue = "NULL")
    var genres: List<String>? = null,
    @ColumnInfo(name = "download_count", defaultValue = "NULL")
    var download_count: Long? = null,
    @ColumnInfo(name = "like_count", defaultValue = "NULL")
    var like_count: Long? = null,
    @ColumnInfo(name = "description", defaultValue = "NULL")
    var description: Description? = null,
    @ColumnInfo(name = "yt_trailer_code", defaultValue = "NULL")
    var yt_trailer_code: String? = null,
    @ColumnInfo(name = "language", defaultValue = "NULL")
    var language: String? = null,
    @ColumnInfo(name = "mpa_rating", defaultValue = "NULL")
    var mpa_rating: String? = null,
    @ColumnInfo(name = "image", defaultValue = "NULL")
    var image: Image? = null,
    @ColumnInfo(name = "cast", defaultValue = "NULL")
    var cast: List<Cast>? = null,
    @ColumnInfo(name = "torrents", defaultValue = "NULL")
    var torrents: List<Torrent>? = null,
    @ColumnInfo(name = "date_uploaded", defaultValue = "NULL")
    var date_uploaded: String? = null,
    @ColumnInfo(name = "date_uploaded_unix", defaultValue = "NULL")
    var date_uploaded_unix: Long? = null,
) {
    data class Image(
        var backgroundImage: String?,
        var backgroundImageOriginal: String?,
        var smallCoverImage: String?,
        var mediumCoverImage: String?,
        var largeCoverImage: String?,
        var mediumScreenshotImage1: String?,
        var mediumScreenshotImage2: String?,
        var mediumScreenshotImage3: String?,
        var largeScreenshotImage1: String?,
        var largeScreenshotImage2: String?,
        var largeScreenshotImage3: String?
    )

    data class Title(var title: String?, var titleEnglish: String?, var titleLong: String?)

    data class Description(
        var descriptionFull: String?,
        var descriptionIntro: String?
    )
}