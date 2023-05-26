package me.ako.yts.data.network.model

import com.squareup.moshi.JsonClass
import me.ako.yts.data.datasource.model.MovieEntity

@JsonClass(generateAdapter = true)
data class MovieDetailResponse(
    val status: String,
    val status_message: String,
    val data: MovieDetailData
)

@JsonClass(generateAdapter = true)
data class MovieDetailData(val movie: MovieDetail)

@JsonClass(generateAdapter = true)
data class MovieDetail(
    val id: Int = 0,
    val url: String = "",
    val imdb_code: String = "",
    val title: String = "",
    val title_english: String = "",
    val title_long: String = "",
    val slug: String = "",
    val year: Int = 0,
    val rating: Double = 0.0,
    val runtime: Long = 0,
    val genres: List<String> = listOf(),
    val download_count: Long = 0,
    val like_count: Long = 0,
    val description_intro: String = "",
    val description_full: String = "",
    val yt_trailer_code: String = "",
    val language: String = "Unknown",
    val mpa_rating: String = "",
    val background_image: String = "",
    val background_image_original: String = "",
    val small_cover_image: String = "",
    val medium_cover_image: String = "",
    val large_cover_image: String = "",
    val medium_screenshot_image1: String = "",
    val medium_screenshot_image2: String = "",
    val medium_screenshot_image3: String = "",
    val large_screenshot_image1: String = "",
    val large_screenshot_image2: String = "",
    val large_screenshot_image3: String = "",
    val cast: List<Cast> = listOf(),
    val torrents: List<Torrent> = listOf(),
    val date_uploaded: String = "",
    val date_uploaded_unix: Long = 0,
)

@JsonClass(generateAdapter = true)
data class Cast(
    val name: String = "",
    val character_name: String = "",
    val url_small_image: String = "",
    val imdb_code: String = ""
)

@JsonClass(generateAdapter = true)
data class Torrent(
    val url: String = "",
    val hash: String = "",
    val quality: String = "",
    val type: String = "",
    val seeds: Long = 0,
    val peers: Long = 0,
    val size: String = "",
    val size_bytes: Long = 0,
    val date_uploaded: String = "",
    val date_uploaded_unix: Long = 0,
)