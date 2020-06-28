package com.automattic.freshlypressed

import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.jupiter.api.extension.RegisterExtension

abstract class AsyncTest {
    companion object {
        val dispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()
        val testScope: TestCoroutineScope = TestCoroutineScope(dispatcher)

        @JvmField
        @RegisterExtension
        val instantExecutorExtension = InstantExecutorExtension(dispatcher, testScope)
    }
}
