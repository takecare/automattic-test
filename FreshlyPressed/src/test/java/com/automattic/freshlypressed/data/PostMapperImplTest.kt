package com.automattic.freshlypressed.data

import com.automattic.freshlypressed.Fixtures
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

internal class PostMapperImplTest {

    private val dateMapper: DateMapper = mockk()
    private val hostExtractor: HostExtractor = mockk()

    private val sut = PostMapperImpl(dateMapper, hostExtractor)

    @Test
    fun `when mapping PostData it relies on DateMapper to map a date`() {
        val data = Fixtures.postDataList[0]
        every { dateMapper.map(any()) } returns Date()
        every { hostExtractor.extract(any()) } returns "host"

        sut.map(data)

        verify { dateMapper.map(data.date) }
    }

    @Test
    fun `when mapping PostData it relies on HostExtractor to extract the url's host`() {
        val data = Fixtures.postDataList[0]
        every { dateMapper.map(any()) } returns Date()
        every { hostExtractor.extract(any()) } returns "host"

        sut.map(data)

        verify { hostExtractor.extract(data.author.url) }
    }
}
