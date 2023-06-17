package me.ako.yts.data.network.model

import com.squareup.moshi.JsonClass
import me.ako.yts.data.datasource.model.MovieDetailEntity

@JsonClass(generateAdapter = true)
data class MovieDetailResponse(
    val status: String,
    val status_message: String,
    val data: MovieDetailData
) {
    fun asDatabaseModel(): MovieDetailEntity {
        return data.movie.let {
            MovieDetailEntity(
                id = it.id,
                url = it.url,
                imdb_code = it.imdb_code,
                title = MovieDetailEntity.Title(
                    title = it.title,
                    titleEnglish = it.title_english,
                    titleLong = it.title_long
                ),
                slug = it.slug,
                year = it.year,
                rating = it.rating,
                runtime = it.runtime,
                genres = it.genres,
                yt_trailer_code = it.yt_trailer_code,
                language = it.language,
                mpa_rating = it.mpa_rating,
                image = MovieDetailEntity.Image(
                    backgroundImage = it.background_image,
                    backgroundImageOriginal = it.background_image_original,
                    smallCoverImage = it.small_cover_image,
                    mediumCoverImage = it.medium_cover_image,
                    largeCoverImage = it.large_cover_image,
                    mediumScreenshotImage1 = it.medium_screenshot_image1,
                    mediumScreenshotImage2 = it.medium_screenshot_image2,
                    mediumScreenshotImage3 = it.medium_screenshot_image3,
                    largeScreenshotImage1 = it.large_screenshot_image1,
                    largeScreenshotImage2 = it.large_screenshot_image2,
                    largeScreenshotImage3 = it.large_screenshot_image3
                ),
                torrents = it.torrents,
                date_uploaded = it.date_uploaded,
                date_uploaded_unix = it.date_uploaded_unix,
                download_count = it.download_count,
                like_count = it.like_count,
                cast = it.cast,
                description = MovieDetailEntity.Description(
                    descriptionFull = it.description_full,
                    descriptionIntro = it.description_intro
                ),
            )
        }
    }
}

@JsonClass(generateAdapter = true)
data class MovieDetailData(val movie: MovieDetail)

@JsonClass(generateAdapter = true)
data class MovieDetail(
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
    val download_count: Long?,
    val like_count: Long?,
    val description_intro: String?,
    val description_full: String?,
    val yt_trailer_code: String?,
    val language: String?,
    val mpa_rating: String?,
    val background_image: String?,
    val background_image_original: String?,
    val small_cover_image: String?,
    val medium_cover_image: String?,
    val large_cover_image: String?,
    val medium_screenshot_image1: String?,
    val medium_screenshot_image2: String?,
    val medium_screenshot_image3: String?,
    val large_screenshot_image1: String?,
    val large_screenshot_image2: String?,
    val large_screenshot_image3: String?,
    val cast: List<Cast>?,
    val torrents: List<Torrent>?,
    val date_uploaded: String?,
    val date_uploaded_unix: Long?,
)

@JsonClass(generateAdapter = true)
data class Cast(
    val name: String?,
    val character_name: String?,
    val url_small_image: String?,
    val imdb_code: String?
)

@JsonClass(generateAdapter = true)
data class Torrent(
    val url: String?,
    val hash: String?,
    val quality: String?,
    val type: String?,
    val seeds: Long?,
    val peers: Long?,
    val size: String?,
    val size_bytes: Long?,
    val date_uploaded: String?,
    val date_uploaded_unix: Long?,
)