package me.ako.yts.data.datasource.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import me.ako.yts.data.network.model.Torrent

@Entity(
    tableName = "movie"
)
data class MovieEntity(
    @PrimaryKey
    val id: Int,
    val url: String?,
    val imdb_code: String?,
    val title: String?,
    val title_english: String?,
    val title_long: String?,
    val slug: String?,
    val year: Int?,
    val rating: Double?,
    val runtime: Long?,
    val genres: List<String>?,
    val summary: String?,
    val description_full: String?,
    val synopsis: String?,
    val yt_trailer_code: String?,
    val language: String?,
    val mpa_rating: String?,
    val background_image: String?,
    val background_image_original: String?,
    val small_cover_image: String?,
    val medium_cover_image: String?,
    val large_cover_image: String?,
    val state: String?,
    val torrents: List<Torrent>?,
    val date_uploaded: String?,
    val date_uploaded_unix: Long?,
)