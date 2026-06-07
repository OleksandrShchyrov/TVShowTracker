package com.oshchyrov.tvshowtracker.presentation.base

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class BaseViewModelTest {

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun updateStateChangesValue() = runTest {
        val viewModel = TestViewModel()
        viewModel.increment()
        assertEquals(1, viewModel.state.value.count)
    }

    @Test
    fun handleIntentRoutesToHandler() = runTest {
        val viewModel = TestViewModel()
        viewModel.handleIntent(TestIntent.Add(3))
        assertEquals(3, viewModel.state.value.count)
    }

    private data class TestState(val count: Int = 0)

    private sealed interface TestIntent {
        data class Add(val amount: Int) : TestIntent
    }

    private class TestViewModel : BaseViewModel<TestState, TestIntent>(TestState()) {
        fun increment() {
            updateState { copy(count = count + 1) }
        }

        override fun handleIntent(intent: TestIntent) {
            when (intent) {
                is TestIntent.Add -> updateState { copy(count = count + intent.amount) }
            }
        }
    }
}
