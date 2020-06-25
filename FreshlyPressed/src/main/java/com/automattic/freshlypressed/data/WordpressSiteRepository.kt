package com.automattic.freshlypressed.data

import com.automattic.freshlypressed.domain.Site
import com.automattic.freshlypressed.domain.SiteRepository

class WordpressSiteRepository(
    private val siteService: SiteService
) : SiteRepository {

    override suspend fun getSite(url: String): Site {
        val data = siteService.getSite(url)
        return Site(data.name, data.description, data.subscriberCount)
    }
}
