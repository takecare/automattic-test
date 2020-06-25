package com.automattic.freshlypressed.data

import com.automattic.freshlypressed.domain.Site
import com.automattic.freshlypressed.domain.SiteRepository
import java.io.IOException

class WordpressSiteRepository(
    private val siteService: SiteService
) : SiteRepository {

    override suspend fun getSite(url: String): Result<Site> {
        return try {
            val data = siteService.getSite(url)
            Result.Success(Site(data.name, data.description, data.subscriberCount))
        } catch (exception: IOException) {
            Result.Error(exception)
        }
    }
}
