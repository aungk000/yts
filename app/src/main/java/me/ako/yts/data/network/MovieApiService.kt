package me.ako.yts.data.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import me.ako.yts.data.network.model.MovieDetailResponse
import me.ako.yts.data.network.model.MovieListResponse
import me.ako.yts.data.network.model.MovieSuggestionResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface MovieApiService {
    @GET("list_movies.json")
    suspend fun getMovies(
        @Query("limit") limit: Int = 50,
        @Query("page") page: Int = 1
    ): Response<MovieListResponse>

    @GET("list_movies.json?sort_by=title&order_by=asc")
    suspend fun searchMovies(
        @Query(
            "query_term",
            encoded = true
        ) query: String
    ): Response<MovieListResponse>

    @GET("movie_details.json?with_cast=true&with_images=true")
    suspend fun getMovie(@Query("movie_id") movie_id: Int): Response<MovieDetailResponse>

    @GET("movie_suggestions.json")
    suspend fun getMovieSuggestions(@Query("movie_id") movie_id: Int): Response<MovieSuggestionResponse>
}

object MovieApi {
    private const val url = "https://yts.mx/api/v2/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okhttp: OkHttpClient
        get() {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.HEADERS
            return OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build()
        }

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(okhttp)
        .baseUrl(url)
        .build()

    val retrofitService: MovieApiService by lazy {
        retrofit.create(MovieApiService::class.java)
    }

    sealed class ApiStatus(val message: String?) {
        class Loading(message: String?) : ApiStatus(message)
        class Error(message: String?) : ApiStatus(message)
        class Done(message: String?) : ApiStatus(message)
    }
}