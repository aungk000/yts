package me.ako.yts.data.network.model

import com.squareup.moshi.JsonClass
import me.ako.yts.data.datasource.model.MovieListEntity

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

fun MovieListResponse.asDatabaseModel(): List<MovieListEntity>? {
    return data.movies?.map {
        MovieListEntity(
            id = it.id,
            url = it.url,
            imdb_code = it.imdb_code,
            title = MovieListEntity.Title(
                title = it.title,
                titleEnglish = it.title_english,
                titleLong = it.title_long
            ),
            slug = it.slug,
            year = it.year,
            rating = it.rating,
            runtime = it.runtime,
            genres = it.genres,
            description = MovieListEntity.Description(
                descriptionFull = it.description_full,
                synopsis = it.synopsis,
                summary = it.summary
            ),
            yt_trailer_code = it.yt_trailer_code,
            language = it.language,
            mpa_rating = it.mpa_rating,
            image = MovieListEntity.Image(
                backgroundImage = it.background_image,
                backgroundImageOriginal = it.background_image_original,
                smallCoverImage = it.small_cover_image,
                mediumCoverImage = it.medium_cover_image,
                largeCoverImage = it.large_cover_image
            ),
            state = it.state,
            torrents = it.torrents,
            date_uploaded = it.date_uploaded,
            date_uploaded_unix = it.date_uploaded_unix
        )
    }
}