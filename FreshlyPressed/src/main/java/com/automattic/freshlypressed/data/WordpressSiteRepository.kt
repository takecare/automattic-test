package com.automattic.freshlypressed.data

import com.automattic.freshlypressed.domain.Result
import com.automattic.freshlypressed.domain.Site
import com.automattic.freshlypressed.domain.SiteRepository
import java.io.IOException

class WordpressSiteRepository(
    private val siteService: SiteService,
    private val mapper: SiteMapper
) : SiteRepository {

    override suspend fun getSite(url: String): Result<Site> {
        return try {
            val data = siteService.getSite(url)
            Result.Success(mapper.map(data))
        } catch (exception: IOException) {
            Result.Error(exception)
        }
    }
}

interface SiteMapper {
    fun map(data: SiteData): Site
}

class SiteMapperImpl : SiteMapper {
    override fun map(data: SiteData) = Site(data.name, data.description, data.subscriberCount)
}
