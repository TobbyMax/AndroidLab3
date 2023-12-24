package dev.ageev.lab3.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.ageev.lab3.api.NewsApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun createNewsApi(): NewsApi {
        return NewsApi.create();
    }

}