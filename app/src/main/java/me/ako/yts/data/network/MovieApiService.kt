package me.ako.yts.data.network

import me.ako.yts.data.network.model.MovieDetailResponse
import me.ako.yts.data.network.model.MovieListResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import me.ako.yts.data.network.model.MovieSuggestionResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface MovieApiService {
    @GET("list_movies.json")
    suspend fun getMovies(): MovieListResponse

    @GET("list_movies.json")
    suspend fun searchMovies(@Query("query_term") query: String): MovieListResponse

    @GET("movie_details.json")
    suspend fun getMovie(@Query("movie_id") movie_id: Int): MovieDetailResponse

    @GET("movie_suggestions.json")
    suspend fun getSuggestedMovies(@Query("movie_id") movie_id: Int): MovieSuggestionResponse
}

object MovieApi {
    private const val url = "https://yts.mx/api/v2/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okhttp: OkHttpClient
        get() {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
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

    sealed class ApiStatus {
        object Loading : ApiStatus()
        object Error : ApiStatus()
        object Done : ApiStatus()
    }
}