package com.pubscale.basicvideoplayer.di

import android.content.Context
import com.pubscale.basicvideoplayer.BuildConfig
import com.pubscale.basicvideoplayer.data.remote.VideoApiService
import com.pubscale.basicvideoplayer.data.repository.VideoRepositoryImpl
import com.pubscale.basicvideoplayer.domain.repository.VideoRepository
import com.pubscale.basicvideoplayer.domain.usecase.GetVideoUrlUseCase
import com.pubscale.basicvideoplayer.domain.usecasestate.UseCaseState
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Provides an HttpLoggingInterceptor for detailed logging of network requests/responses
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    // Provides context for networkConnectionInterceptor
    @Provides
    @Singleton
    fun provideNetworkConnectionInterceptor(@ApplicationContext context: Context): NetworkConnectionInterceptor =
        NetworkConnectionInterceptor(context)

    // Provides a configured OkHttpClient with logging interceptor
    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        networkInterceptor: NetworkConnectionInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(networkInterceptor)
            .build()
    }

    // Provides Retrofit instance configured with OkHttpClient and Gson converter
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.VIDEO_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    // Provides VideoApiService instance
    @Provides
    @Singleton
    fun provideVideoApiService(retrofit: Retrofit): VideoApiService {
        return retrofit.create(VideoApiService::class.java)
    }

    // Provides VideoRepository implementation
    @Provides
    @Singleton
    fun provideVideoRepository(videoApiService: VideoApiService): VideoRepository {
        return VideoRepositoryImpl(videoApiService)
    }

    // Provides GetVideoUrlUseCase instance
    @Provides
    @Singleton
    fun provideGetVideoUrlUseCase(videoRepository: VideoRepository): GetVideoUrlUseCase {
        return GetVideoUrlUseCase(videoRepository)
    }

    // Provides UseCaseState instance for handling UI states
    @Provides
    @Singleton
    fun provideUseCaseState(getVideoUrlUseCase: GetVideoUrlUseCase): UseCaseState {
        return UseCaseState(getVideoUrlUseCase)
    }
}