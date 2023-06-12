package me.ako.yts.data.datasource.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import me.ako.yts.data.network.model.Cast
import me.ako.yts.data.network.model.Torrent

@Entity(
    tableName = "movie"
)
data class MovieEntity(
    @PrimaryKey
    val id: Int,
    val url: String? = "",
    val imdb_code: String? = "",
    val title: String? = "",
    val title_english: String? = "",
    val title_long: String? = "",
    val slug: String? = "",
    val year: Int? = 0,
    val rating: Double? = 0.0,
    val runtime: Long? = 0,
    val genres: List<String>? = listOf(),
    val summary: String? = "",
    @ColumnInfo(name = "download_count", defaultValue = "0")
    val download_count: Long? = 0,
    @ColumnInfo(name = "like_count", defaultValue = "0")
    val like_count: Long? = 0,
    @ColumnInfo(name = "description_intro", defaultValue = "")
    val description_intro: String? = "",
    val description_full: String? = "",
    val synopsis: String? = "",
    val yt_trailer_code: String? = "",
    val language: String? = "",
    val mpa_rating: String? = "",
    val background_image: String? = "",
    val background_image_original: String? = "",
    val small_cover_image: String? = "",
    val medium_cover_image: String? = "",
    val large_cover_image: String? = "",
    @ColumnInfo(name = "medium_screenshot_image1", defaultValue = "")
    val medium_screenshot_image1: String? = "",
    @ColumnInfo(name = "medium_screenshot_image2", defaultValue = "")
    val medium_screenshot_image2: String? = "",
    @ColumnInfo(name = "medium_screenshot_image3", defaultValue = "")
    val medium_screenshot_image3: String? = "",
    @ColumnInfo(name = "large_screenshot_image1", defaultValue = "")
    val large_screenshot_image1: String? = "",
    @ColumnInfo(name = "large_screenshot_image2", defaultValue = "")
    val large_screenshot_image2: String? = "",
    @ColumnInfo(name = "large_screenshot_image3", defaultValue = "")
    val large_screenshot_image3: String? = "",
    @ColumnInfo(name = "cast", defaultValue = "")
    val cast: List<Cast>? = listOf(),
    val state: String? = "",
    val torrents: List<Torrent>? = listOf(),
    val date_uploaded: String? = "",
    val date_uploaded_unix: Long? = 0,
)