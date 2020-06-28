package com.automattic.freshlypressed.data

import com.automattic.freshlypressed.Fixtures
import com.automattic.freshlypressed.domain.Result
import com.automattic.freshlypressed.domain.Site
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.io.IOException

internal class WordpressSiteRepositoryTest {

    private val service: SiteService = mockk()
    private val mapper: SiteMapper = mockk()

    private val sut = WordpressSiteRepository(service, mapper)

    @Test
    fun `when requesting site from the repository, is requests the site from the service`() {
        coEvery { service.getSite(any()) } returns Fixtures.siteData
        every { mapper.map(any()) } returns Fixtures.site

        runBlocking { sut.getSite("www.site.pt") }

        coVerify { service.getSite("www.site.pt") }
    }

    @Test
    fun `given service returns the site, when requesting a site from the repository, it maps them with the site mapper`() {
        coEvery { service.getSite(any()) } returns Fixtures.siteData
        every { mapper.map(any()) } returns Fixtures.site

        runBlocking { sut.getSite("www.site.pt") }

        coVerify { mapper.map(Fixtures.siteData) }
    }

    @Test
    fun `given service returns the site, when requesting a site from the repository, it returns a successful result`() {
        coEvery { service.getSite(any()) } returns Fixtures.siteData
        every { mapper.map(any()) } returns Fixtures.site

        val response = runBlocking { sut.getSite("www.site.pt") }

        assertThat(response).isInstanceOf(Result.Success::class.java)
    }

    @Test
    fun `given service throws exception, when requesting the site from the repository, it returns an error result`() {
        coEvery { service.getSite(any()) } throws IOException("boom!")

        val response = runBlocking { sut.getSite("www.site.pt") }

        assertThat(response).isInstanceOf(Result.Error::class.java)
    }
}
