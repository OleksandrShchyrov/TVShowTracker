package com.oshchyrov.tvshowtracker.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class FlowWrapperTest {

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun collectEmitsLatestValue() = runTest {
        val flow = MutableStateFlow("initial")
        val wrapper = FlowWrapper(flow)
        var received = ""
        wrapper.collect(
            onEach = { received = it },
            onComplete = {},
            onError = {},
        )
        advanceUntilIdle()
        assertEquals("initial", received)
    }

    @Test
    fun collectUpdatesOnFlowChange() = runTest {
        val flow = MutableStateFlow(1)
        val wrapper = FlowWrapper(flow)
        val values = mutableListOf<Int>()
        wrapper.collect(
            onEach = { values.add(it) },
            onComplete = {},
            onError = {},
        )
        advanceUntilIdle()
        flow.value = 2
        advanceUntilIdle()
        assertTrue(values.contains(1))
        assertTrue(values.contains(2))
    }

    @Test
    fun stateFlowWrapExtensionCreatesWrapper() = runTest {
        val stateFlow = MutableStateFlow(42)
        val wrapper = stateFlow.wrap()
        var received = 0
        wrapper.collect(
            onEach = { received = it },
            onComplete = {},
            onError = {},
        )
        advanceUntilIdle()
        assertEquals(42, received)
    }

    @Test
    fun cancelStopsCollection() = runTest {
        val flow = MutableStateFlow(1)
        val wrapper = FlowWrapper(flow)
        val values = mutableListOf<Int>()
        val cancellable = wrapper.collect(
            onEach = { values.add(it) },
            onComplete = {},
            onError = {},
        )
        advanceUntilIdle()
        cancellable.cancel()
        flow.value = 99
        advanceUntilIdle()
        assertEquals(listOf(1), values)
    }
}
