package me.ako.yts.data.network.model

import com.squareup.moshi.JsonClass
import me.ako.yts.data.datasource.model.MovieEntity

@JsonClass(generateAdapter = true)
data class MovieListResponse(
    val status: String,
    val status_message: String,
    val data: MovieListData
)

@JsonClass(generateAdapter = true)
data class MovieListData(
    val movie_count: Long,
    val limit: Int,
    val page_number: Int,
    val movies: List<MovieList>?
)

@JsonClass(generateAdapter = true)
data class MovieList(
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
    val summary: String = "",
    val description_full: String = "",
    val synopsis: String = "",
    val yt_trailer_code: String = "",
    val language: String = "Unknown",
    val mpa_rating: String = "",
    val background_image: String = "",
    val background_image_original: String = "",
    val small_cover_image: String = "",
    val medium_cover_image: String = "",
    val large_cover_image: String = "",
    val state: String = "",
    val torrents: List<Torrent> = listOf(),
    val date_uploaded: String = "",
    val date_uploaded_unix: Long = 0,
)

fun MovieListResponse.asDatabaseModel(): List<MovieEntity>? {
    return data.movies?.map {
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
            summary = it.summary,
            description_full = it.description_full,
            synopsis = it.synopsis,
            yt_trailer_code = it.yt_trailer_code,
            language = it.language,
            mpa_rating = it.mpa_rating,
            background_image = it.background_image,
            background_image_original = it.background_image_original,
            small_cover_image = it.small_cover_image,
            medium_cover_image = it.medium_cover_image,
            large_cover_image = it.large_cover_image,
            state = it.state,
            torrents = it.torrents,
            date_uploaded = it.date_uploaded,
            date_uploaded_unix = it.date_uploaded_unix
        )
    }
}

fun MovieListResponse.asDatabaseModelArray(): Array<MovieEntity>? {
    return data.movies?.map {
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
            summary = it.summary,
            description_full = it.description_full,
            synopsis = it.synopsis,
            yt_trailer_code = it.yt_trailer_code,
            language = it.language,
            mpa_rating = it.mpa_rating,
            background_image = it.background_image,
            background_image_original = it.background_image_original,
            small_cover_image = it.small_cover_image,
            medium_cover_image = it.medium_cover_image,
            large_cover_image = it.large_cover_image,
            state = it.state,
            torrents = it.torrents,
            date_uploaded = it.date_uploaded,
            date_uploaded_unix = it.date_uploaded_unix
        )
    }?.toTypedArray()
}