package com.example.thor.di

import com.example.thor.data.remote.OpenWeatherMapApi
import com.example.thor.data.repository.WeatherRepositoryImp
import com.example.thor.domain.repository.WeatherRepositoryInterface
import com.example.thor.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOpenWeatherMapApi(): OpenWeatherMapApi {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenWeatherMapApi::class.java)
    }

    @Provides
    @Singleton
    fun provideWeatherRepository(api: OpenWeatherMapApi): WeatherRepositoryInterface {
        return WeatherRepositoryImp(api)
    }
}