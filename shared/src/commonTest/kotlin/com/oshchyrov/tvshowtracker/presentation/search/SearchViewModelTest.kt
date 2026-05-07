package com.oshchyrov.tvshowtracker.presentation.search

import com.oshchyrov.tvshowtracker.domain.model.Show
import com.oshchyrov.tvshowtracker.domain.repository.ShowRepository
import com.oshchyrov.tvshowtracker.domain.model.Episode
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
        viewModel.onCleared()
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
        advanceTimeBy(500) // debounce
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
    fun emptyQueryClearsResults() = runTest {
        fakeRepository.showsToReturn = listOf(testShow(1, "Test"))
        viewModel.handleIntent(SearchIntent.QueryChanged("test"))
        advanceTimeBy(500)
        advanceUntilIdle()
        viewModel.handleIntent(SearchIntent.QueryChanged(""))
        advanceUntilIdle()
        assertTrue(viewModel.state.value.results.isEmpty())
    }
}

private fun testShow(id: Int, name: String) = Show(
    id = id, name = name, summary = null, imageUrl = null, imageMediumUrl = null,
    genres = emptyList(), rating = null, runtime = null, language = null,
    status = null, premiered = null, network = null, webChannel = null, officialSite = null,
)

class FakeShowRepository : ShowRepository {
    var initialShowsToReturn: List<Show> = emptyList()
    var showsToReturn: List<Show> = emptyList()
    var shouldFail = false

    override suspend fun getInitialShows(page: Int): Result<List<Show>> {
        return if (shouldFail) Result.failure(RuntimeException("Network error"))
        else Result.success(initialShowsToReturn)
    }

    override suspend fun searchShows(query: String): Result<List<Show>> {
        return if (shouldFail) Result.failure(RuntimeException("Network error"))
        else Result.success(showsToReturn)
    }

    override suspend fun getShowDetails(showId: Int): Result<Show> {
        return showsToReturn.find { it.id == showId }?.let { Result.success(it) }
            ?: Result.failure(RuntimeException("Not found"))
    }

    override suspend fun getEpisodes(showId: Int): Result<List<Episode>> {
        return Result.success(emptyList())
    }
}

