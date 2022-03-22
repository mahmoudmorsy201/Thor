package com.example.thorweather.di

import android.app.Application
import androidx.room.Room
import com.example.thorweather.data.local.AppDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesDataBase(
        app: Application,
        callback: AppDataBase.Callback
    ) = Room.databaseBuilder(app, AppDataBase::class.java, "thor_database")
        .fallbackToDestructiveMigration()
        .addCallback(callback)
        .build()

    @Provides
    fun providesWeatherDao(db: AppDataBase) = db.weatherDao()

    @Provides
    fun providesAlertDao(db: AppDataBase) = db.alertDao()

    @ApplicationScope
    @Provides
    @Singleton
    fun providesApplicationScope() =
        CoroutineScope(SupervisorJob())//when child of coroutine failed keep other child run

    @Retention(AnnotationRetention.RUNTIME)
    @Qualifier
    annotation class ApplicationScope
}