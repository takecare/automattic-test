package com.automattic.freshlypressed.data

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

// https://public-api.wordpress.com/rest/v1.1/sites/discover.wordpress.com
// https://public-api.wordpress.com/rest/v1.1/sites/$url

interface SiteService {

    @GET("{url}")
    suspend fun getSite(@Path("url") url: String): SiteData

    companion object {
        fun createService(okHttpClient: OkHttpClient, moshiConverterFactory: MoshiConverterFactory): SiteService =
            Retrofit.Builder()
                .baseUrl("https://public-api.wordpress.com/rest/v1.1/sites/")
                .addConverterFactory(moshiConverterFactory)
                .client(okHttpClient)
                .build()
                .create(SiteService::class.java)
    }
}
