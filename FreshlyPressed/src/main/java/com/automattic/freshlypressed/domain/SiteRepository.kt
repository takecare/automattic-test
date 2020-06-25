package com.automattic.freshlypressed.domain

interface SiteRepository {
    suspend fun getSite(url: String): Site
}
