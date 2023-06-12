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
    val character_name: String = "Unknown",
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

fun MovieDetailResponse.asDatabaseModel(): MovieEntity {
    return data.movie.let {
        MovieEntity(
            id = it.id,
            url = it.url,
            imdb_code = it.imdb_code,
            title = it.title,
            title_english = it.title_english,
            title_long = it.title_long,
            slug = it.slug,
            year = it.year,
            rating = it.rating,
            runtime = it.runtime,
            genres = it.genres,
            description_full = it.description_full,
            yt_trailer_code = it.yt_trailer_code,
            language = it.language,
            mpa_rating = it.mpa_rating,
            background_image = it.background_image,
            background_image_original = it.background_image_original,
            small_cover_image = it.small_cover_image,
            medium_cover_image = it.medium_cover_image,
            large_cover_image = it.large_cover_image,
            torrents = it.torrents,
            date_uploaded = it.date_uploaded,
            date_uploaded_unix = it.date_uploaded_unix,
            download_count = it.download_count,
            like_count = it.like_count,
            medium_screenshot_image1 = it.medium_screenshot_image1,
            medium_screenshot_image2 = it.medium_screenshot_image2,
            medium_screenshot_image3 = it.medium_screenshot_image3,
            large_screenshot_image1 = it.large_screenshot_image1,
            large_screenshot_image2 = it.large_screenshot_image2,
            large_screenshot_image3 = it.large_screenshot_image3,
            cast = it.cast,
            description_intro = it.description_intro
        )
    }
}