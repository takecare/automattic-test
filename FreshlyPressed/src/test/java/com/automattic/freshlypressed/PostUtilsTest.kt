package com.automattic.freshlypressed

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.util.Date

class PostUtilsTest {

    @Test
    fun printsDateCorrectly() {
        val result = PostUtils.printDate(Date(1230000000000))

        assertThat(result).isEqualTo("Dec 23, 2008")
    }
}
