package com.scoretally.di

import com.scoretally.data.remote.bgg.BggApiService
import com.scoretally.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.simpleframework.xml.convert.AnnotationStrategy
import org.simpleframework.xml.core.Persister
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val authInterceptor = okhttp3.Interceptor { chain ->
            val originalRequest = chain.request()

            val requestBuilder = originalRequest.newBuilder()
                .header("User-Agent", "ScoreTally/1.0 (https://github.com/scoretally)")

            if (Constants.BGG_API_TOKEN.isNotEmpty()) {
                requestBuilder.header("Authorization", "Bearer ${Constants.BGG_API_TOKEN}")
            }

            chain.proceed(requestBuilder.build())
        }

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val strategy = AnnotationStrategy()
        val serializer = Persister(strategy)

        return Retrofit.Builder()
            .baseUrl("https://boardgamegeek.com/")
            .client(okHttpClient)
            .addConverterFactory(SimpleXmlConverterFactory.create(serializer))
            .build()
    }

    @Provides
    @Singleton
    fun provideBggApiService(retrofit: Retrofit): BggApiService {
        return retrofit.create(BggApiService::class.java)
    }
}
