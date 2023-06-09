package me.ako.yts.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import me.ako.yts.data.datasource.MovieDatabase
import me.ako.yts.data.network.MovieApi
import me.ako.yts.data.network.MovieApiService
import me.ako.yts.domain.controller.DataRepository
import me.ako.yts.domain.controller.DataRepositoryImpl
import me.ako.yts.domain.util.FileDownloader
import me.ako.yts.domain.util.Utils
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideDatabase(app: Application): MovieDatabase {
        return MovieDatabase.getInstance(app)
    }

    @Singleton
    @Provides
    fun provideDataRepository(db: MovieDatabase, retrofit: MovieApiService): DataRepository {
        return DataRepositoryImpl(db.dao, retrofit)
    }

    @Singleton
    @Provides
    fun provideUtils(@ApplicationContext context: Context): Utils {
        return Utils(context)
    }

    @Singleton
    @Provides
    fun providePrefs(app: Application): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(app)
    }

    @Singleton
    @Provides
    fun provideRetrofit(): MovieApiService {
        return MovieApi.retrofitService
    }

    @Singleton
    @Provides
    fun provideDownloader(@ApplicationContext context: Context): FileDownloader {
        return FileDownloader(context)
    }
}