package com.automattic.freshlypressed.data

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttp(): OkHttpClient = OkHttpClient()

    @Provides
    fun provideMoshiConverterFactory(): MoshiConverterFactory = MoshiConverterFactory.create()

    @Provides
    @Singleton
    fun providePostsService(okHttpClient: OkHttpClient, moshiConverterFactory: MoshiConverterFactory): PostsService =
        PostsService.createService(okHttpClient, moshiConverterFactory)

    @Provides
    @Singleton
    fun provideSiteService(okHttpClient: OkHttpClient, moshiConverterFactory: MoshiConverterFactory): SiteService =
        SiteService.createService(okHttpClient, moshiConverterFactory)
}
