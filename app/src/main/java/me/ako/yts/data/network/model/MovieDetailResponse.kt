package me.ako.yts.data.network.model

import com.squareup.moshi.JsonClass

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
    val id: Int,
    val url: String,
    val imdb_code: String,
    val title: String,
    val title_english: String,
    val title_long: String,
    val slug: String,
    val year: Int,
    val rating: Double,
    val runtime: Int,
    val genres: List<String>,
    val download_count: Long,
    val like_count: Int,
    val description_intro: String,
    val description_full: String,
    val yt_trailer_code: String,
    val language: String,
    val mpa_rating: String,
    val background_image: String,
    val background_image_original: String,
    val small_cover_image: String,
    val medium_cover_image: String,
    val large_cover_image: String,
    val torrents: List<Torrent>,
    val date_uploaded: String,
    val date_uploaded_unix: Long,
)

@JsonClass(generateAdapter = true)
data class Torrent(
    val url: String,
    val hash: String,
    val quality: String,
    val type: String,
    val seeds: Int,
    val peers: Int,
    val size: String,
    val size_bytes: Long,
    val date_uploaded: String,
    val date_uploaded_unix: Long,
)