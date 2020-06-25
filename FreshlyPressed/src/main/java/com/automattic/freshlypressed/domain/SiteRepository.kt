package com.automattic.freshlypressed.domain

interface SiteRepository {
    suspend fun getSite(url: String): Result<Site>
}
