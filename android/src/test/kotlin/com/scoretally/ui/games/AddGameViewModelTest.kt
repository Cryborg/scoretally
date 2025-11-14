package com.scoretally.ui.games

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
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
class AddGameViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var gameRepository: GameRepository
    private lateinit var viewModel: AddGameViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        gameRepository = mockk(relaxed = true)
        viewModel = AddGameViewModel(gameRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onNameChange updates name in state`() = runTest {
        viewModel.uiState.test {
            awaitItem() // initial state

            viewModel.onNameChange("Chess")

            val state = awaitItem()
            assertThat(state.name).isEqualTo("Chess")
        }
    }

    @Test
    fun `onMinPlayersChange updates minPlayers in state`() = runTest {
        viewModel.uiState.test {
            awaitItem() // initial state

            viewModel.onMinPlayersChange("2")

            val state = awaitItem()
            assertThat(state.minPlayers).isEqualTo("2")
        }
    }

    @Test
    fun `saveGame with valid data saves game successfully`() = runTest {
        // Given
        coEvery { gameRepository.insertGame(any()) } returns 1L

        viewModel.uiState.test {
            awaitItem() // initial state

            // When
            viewModel.onNameChange("Chess")
            awaitItem() // name changed

            viewModel.saveGame()
            testDispatcher.scheduler.advanceUntilIdle()

            // Then - isSaving = true
            val savingState = awaitItem()
            assertThat(savingState.isSaving).isTrue()

            // Then - isSaved = true
            val savedState = awaitItem()
            assertThat(savedState.isSaving).isFalse()
            assertThat(savedState.isSaved).isTrue()

            coVerify {
                gameRepository.insertGame(
                    match { it.name == "Chess" }
                )
            }
        }
    }

    @Test
    fun `saveGame with blank name does nothing`() = runTest {
        // Given - name is blank by default

        // When
        viewModel.saveGame()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.isSaving).isFalse()
            assertThat(state.isSaved).isFalse()
        }

        coVerify(exactly = 0) { gameRepository.insertGame(any()) }
    }

    @Test
    fun `saveGame handles exception`() = runTest {
        // Given
        coEvery { gameRepository.insertGame(any()) } throws Exception("Database error")

        viewModel.uiState.test {
            awaitItem() // initial state

            viewModel.onNameChange("Chess")
            awaitItem() // name changed

            // When
            viewModel.saveGame()
            testDispatcher.scheduler.advanceUntilIdle()

            // Then - isSaving = true
            awaitItem()

            // Then - isSaving = false, isSaved = false
            val errorState = awaitItem()
            assertThat(errorState.isSaving).isFalse()
            assertThat(errorState.isSaved).isFalse()
        }
    }

    @Test
    fun `saveGame uses GameFormParser for parsing`() = runTest {
        // Given
        coEvery { gameRepository.insertGame(any()) } returns 1L

        viewModel.uiState.test {
            awaitItem() // initial state

            // When - set various fields
            viewModel.onNameChange("Poker")
            awaitItem()

            viewModel.onMinPlayersChange("2")
            awaitItem()

            viewModel.onMaxPlayersChange("10")
            awaitItem()

            viewModel.onAverageDurationChange("90")
            awaitItem()

            viewModel.saveGame()
            testDispatcher.scheduler.advanceUntilIdle()

            awaitItem() // isSaving
            awaitItem() // isSaved

            // Then - verify the parser correctly parsed the values
            coVerify {
                gameRepository.insertGame(
                    match {
                        it.name == "Poker" &&
                        it.minPlayers == 2 &&
                        it.maxPlayers == 10 &&
                        it.averageDuration == 90
                    }
                )
            }
        }
    }

    @Test
    fun `onAllowNegativeScoresChange updates allowNegativeScores in state`() = runTest {
        viewModel.uiState.test {
            val initialState = awaitItem()
            assertThat(initialState.allowNegativeScores).isTrue()

            viewModel.onAllowNegativeScoresChange(false)

            val updatedState = awaitItem()
            assertThat(updatedState.allowNegativeScores).isFalse()
        }
    }
}
