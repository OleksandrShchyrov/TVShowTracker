package com.oshchyrov.tvshowtracker.presentation.search

import com.oshchyrov.tvshowtracker.test.FakeShowRepository
import com.oshchyrov.tvshowtracker.test.testShow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private lateinit var viewModel: SearchViewModel
    private lateinit var fakeRepository: FakeShowRepository

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        fakeRepository = FakeShowRepository()
        viewModel = SearchViewModel(fakeRepository)
    }

    @AfterTest
    fun tearDown() {
        viewModel.clear()
        Dispatchers.resetMain()
    }

    @Test
    fun initialStateIsEmpty() {
        val state = viewModel.state.value
        assertEquals("", state.query)
        assertEquals(emptyList(), state.results)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun queryChangedUpdatesQuery() = runTest {
        viewModel.handleIntent(SearchIntent.QueryChanged("breaking"))
        assertEquals("breaking", viewModel.state.value.query)
    }

    @Test
    fun loadInitialShowsReturnsBrowseContent() = runTest {
        fakeRepository.initialShowsToReturn = listOf(testShow(1, "Under the Dome"))
        viewModel.loadInitialShows()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertTrue(state.isInitialContent)
        assertEquals(1, state.results.size)
        assertEquals("Under the Dome", state.results[0].name)
    }

    @Test
    fun searchReturnsResults() = runTest {
        fakeRepository.showsToReturn = listOf(testShow(1, "Breaking Bad"))
        viewModel.handleIntent(SearchIntent.QueryChanged("breaking"))
        advanceTimeBy(500)
        advanceUntilIdle()
        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(1, state.results.size)
        assertEquals("Breaking Bad", state.results[0].name)
    }

    @Test
    fun searchShowsErrorOnFailure() = runTest {
        fakeRepository.shouldFail = true
        viewModel.handleIntent(SearchIntent.QueryChanged("test"))
        advanceTimeBy(500)
        advanceUntilIdle()
        val state = viewModel.state.value
        assertNotNull(state.error)
        assertFalse(state.isLoading)
    }

    @Test
    fun emptyQueryReloadsInitialShows() = runTest {
        fakeRepository.initialShowsToReturn = listOf(testShow(1, "Browse Show"))
        fakeRepository.showsToReturn = listOf(testShow(2, "Search Show"))
        viewModel.handleIntent(SearchIntent.QueryChanged("test"))
        advanceTimeBy(500)
        advanceUntilIdle()
        viewModel.handleIntent(SearchIntent.QueryChanged(""))
        advanceUntilIdle()
        assertEquals("Browse Show", viewModel.state.value.results[0].name)
    }

    @Test
    fun retryReloadsAfterFailure() = runTest {
        fakeRepository.shouldFail = true
        viewModel.loadInitialShows()
        advanceUntilIdle()
        assertNotNull(viewModel.state.value.error)

        fakeRepository.shouldFail = false
        fakeRepository.initialShowsToReturn = listOf(testShow(1, "Recovered"))
        viewModel.retrySearch()
        advanceUntilIdle()

        assertNull(viewModel.state.value.error)
        assertEquals("Recovered", viewModel.state.value.results[0].name)
    }
}
