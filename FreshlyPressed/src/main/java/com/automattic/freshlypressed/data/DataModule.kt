package com.automattic.freshlypressed.data

import com.automattic.freshlypressed.domain.PostsRepository
import com.automattic.freshlypressed.domain.SiteRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class DataModule {

    companion object {
        @Provides
        @JvmStatic
        fun provideDateFormat(): DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault())
    }

    @Binds
    abstract fun bindHostExtractor(hostExtractor: HostExtractorImpl): HostExtractor

    @Binds
    abstract fun bindDateMapper(dateMapper: DateMapperImpl): DateMapper

    @Binds
    abstract fun bindPostMapper(postMapper: PostMapperImpl): PostMapper

    @Binds
    abstract fun bindPostsRepository(postsRepository: WordpressPostsRepository): PostsRepository

    @Binds
    abstract fun bindSiteMapper(siteMapper: SiteMapperImpl): SiteMapper

    @Binds
    abstract fun bindSiteRepository(siteRepository: WordpressSiteRepository): SiteRepository
}
