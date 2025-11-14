package com.scoretally.ui.games

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.scoretally.domain.model.Game
import com.scoretally.domain.model.GridType
import com.scoretally.domain.repository.GameRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EditGameViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var gameRepository: GameRepository
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: EditGameViewModel

    private val testGame = Game(
        id = 1L,
        name = "Chess",
        minPlayers = 2,
        maxPlayers = 2,
        averageDuration = 30,
        category = "Strategy",
        description = "Board game",
        scoreIncrement = 1,
        hasDice = false,
        diceCount = 1,
        diceFaces = 6,
        allowNegativeScores = false,
        gridType = GridType.STANDARD
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        gameRepository = mockk(relaxed = true)
        savedStateHandle = SavedStateHandle(mapOf("gameId" to 1L))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loads game successfully on init`() = runTest {
        // Given
        coEvery { gameRepository.getGameById(1L) } returns testGame

        // When
        viewModel = EditGameViewModel(gameRepository, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.gameId).isEqualTo(1L)
            assertThat(state.name).isEqualTo("Chess")
            assertThat(state.minPlayers).isEqualTo("2")
            assertThat(state.maxPlayers).isEqualTo("2")
            assertThat(state.averageDuration).isEqualTo("30")
            assertThat(state.isLoading).isFalse()
        }
    }

    @Test
    fun `sets loading false when game not found`() = runTest {
        // Given
        coEvery { gameRepository.getGameById(1L) } returns null

        // When
        viewModel = EditGameViewModel(gameRepository, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.isLoading).isFalse()
        }
    }

    @Test
    fun `handles loading exception`() = runTest {
        // Given
        coEvery { gameRepository.getGameById(1L) } throws Exception("Database error")

        // When
        viewModel = EditGameViewModel(gameRepository, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.isLoading).isFalse()
        }
    }

    @Test
    fun `onNameChange updates name in state`() = runTest {
        // Given
        coEvery { gameRepository.getGameById(1L) } returns testGame
        viewModel = EditGameViewModel(gameRepository, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem() // loaded state

            // When
            viewModel.onNameChange("Checkers")

            // Then
            val state = awaitItem()
            assertThat(state.name).isEqualTo("Checkers")
        }
    }

    @Test
    fun `updateGame with valid data updates game successfully`() = runTest {
        // Given
        coEvery { gameRepository.getGameById(1L) } returns testGame
        coEvery { gameRepository.updateGame(any()) } returns Unit

        viewModel = EditGameViewModel(gameRepository, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem() // loaded state

            viewModel.onNameChange("Checkers")
            awaitItem() // name changed

            // When
            viewModel.updateGame()
            testDispatcher.scheduler.advanceUntilIdle()

            // Then - isSaving = true
            val savingState = awaitItem()
            assertThat(savingState.isSaving).isTrue()

            // Then - isSaved = true
            val savedState = awaitItem()
            assertThat(savedState.isSaving).isFalse()
            assertThat(savedState.isSaved).isTrue()

            coVerify {
                gameRepository.updateGame(
                    match { it.name == "Checkers" && it.id == 1L }
                )
            }
        }
    }

    @Test
    fun `updateGame with blank name does nothing`() = runTest {
        // Given
        coEvery { gameRepository.getGameById(1L) } returns testGame
        viewModel = EditGameViewModel(gameRepository, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem() // loaded state

            viewModel.onNameChange("")
            awaitItem() // name changed to blank

            // When
            viewModel.updateGame()
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            coVerify(exactly = 0) { gameRepository.updateGame(any()) }
        }
    }

    @Test
    fun `updateGame handles game not found`() = runTest {
        // Given
        coEvery { gameRepository.getGameById(1L) } returns testGame
        viewModel = EditGameViewModel(gameRepository, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        // Game exists at first, then deleted before update
        coEvery { gameRepository.getGameById(1L) } returns null

        viewModel.uiState.test {
            awaitItem() // loaded state

            // When
            viewModel.updateGame()
            testDispatcher.scheduler.advanceUntilIdle()

            // Then - isSaving = true
            awaitItem()

            // Then - isSaving = false, game not found
            val errorState = awaitItem()
            assertThat(errorState.isSaving).isFalse()
            assertThat(errorState.isSaved).isFalse()
        }
    }

    @Test
    fun `updateGame handles exception`() = runTest {
        // Given
        coEvery { gameRepository.getGameById(1L) } returns testGame
        viewModel = EditGameViewModel(gameRepository, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        coEvery { gameRepository.updateGame(any()) } throws Exception("Update failed")

        viewModel.uiState.test {
            awaitItem() // loaded state

            // When
            viewModel.updateGame()
            testDispatcher.scheduler.advanceUntilIdle()

            // Then - isSaving = true
            awaitItem()

            // Then - error state
            val errorState = awaitItem()
            assertThat(errorState.isSaving).isFalse()
            assertThat(errorState.isSaved).isFalse()
        }
    }

    @Test
    fun `updateGame preserves isPredefined flag`() = runTest {
        // Given - predefined game
        val predefinedGame = testGame.copy(isPredefined = true)
        coEvery { gameRepository.getGameById(1L) } returns predefinedGame
        coEvery { gameRepository.updateGame(any()) } returns Unit

        viewModel = EditGameViewModel(gameRepository, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem() // loaded state

            // When
            viewModel.updateGame()
            testDispatcher.scheduler.advanceUntilIdle()

            awaitItem() // isSaving
            awaitItem() // isSaved

            // Then - verify isPredefined is preserved
            coVerify {
                gameRepository.updateGame(
                    match { it.isPredefined == true }
                )
            }
        }
    }
}
