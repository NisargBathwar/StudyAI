package com.example.studyai.di

import com.example.studyai.data.Repo.AiRepoImpl
import com.example.studyai.data.remote.GeminiApi
import com.example.studyai.data.remote.RetrofitInstance
import com.example.studyai.domain.Repo.AiRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideHttpClient() : OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(30 , TimeUnit.SECONDS)
            .readTimeout(60  , TimeUnit.SECONDS)
            .writeTimeout(60 , TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    fun provideGeminiApi(provideHttpClient: OkHttpClient) : GeminiApi{
        return RetrofitInstance.api(provideHttpClient)
    }

    @Provides
    @Singleton
    fun provideAiRepo(api: GeminiApi) : AiRepository{
        return AiRepoImpl(api)
    }

}