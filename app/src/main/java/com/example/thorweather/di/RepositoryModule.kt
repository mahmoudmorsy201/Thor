package com.example.thorweather.di

import com.example.thorweather.data.local.AlertDao
import com.example.thorweather.data.local.WeatherDao
import com.example.thorweather.data.remote.RetrofitService
import com.example.thorweather.data.repository.Repository
import com.example.thorweather.data.repository.RepositoryInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideMainRepository(
        retrofit: RetrofitService,
        weatherDao: WeatherDao,
        alertDao: AlertDao
    ): RepositoryInterface {
        return Repository(retrofit, weatherDao,alertDao)
    }
}


