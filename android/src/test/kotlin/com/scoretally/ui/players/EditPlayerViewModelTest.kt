package com.scoretally.ui.players

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.scoretally.domain.model.Player
import com.scoretally.domain.repository.PlayerRepository
import com.scoretally.domain.usecase.ValidatePlayerNameUseCase
import com.scoretally.domain.usecase.ValidationResult
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
class EditPlayerViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var playerRepository: PlayerRepository
    private lateinit var validatePlayerNameUseCase: ValidatePlayerNameUseCase
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: EditPlayerViewModel

    private val testPlayer = Player(
        id = 1L,
        name = "John",
        preferredColor = "#FF0000"
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        playerRepository = mockk(relaxed = true)
        validatePlayerNameUseCase = mockk()
        savedStateHandle = SavedStateHandle(mapOf("playerId" to 1L))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loads player successfully on init`() = runTest {
        // Given
        coEvery { playerRepository.getPlayerById(1L) } returns testPlayer

        // When
        viewModel = EditPlayerViewModel(savedStateHandle, playerRepository, validatePlayerNameUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.playerId).isEqualTo(1L)
            assertThat(state.name).isEqualTo("John")
            assertThat(state.preferredColor).isEqualTo("#FF0000")
            assertThat(state.isLoading).isFalse()
            assertThat(state.error).isNull()
        }
    }

    @Test
    fun `sets error when player not found`() = runTest {
        // Given
        coEvery { playerRepository.getPlayerById(1L) } returns null

        // When
        viewModel = EditPlayerViewModel(savedStateHandle, playerRepository, validatePlayerNameUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.isLoading).isFalse()
            assertThat(state.error).isEqualTo("Player not found")
        }
    }

    @Test
    fun `handles loading exception`() = runTest {
        // Given
        coEvery { playerRepository.getPlayerById(1L) } throws Exception("Database error")

        // When
        viewModel = EditPlayerViewModel(savedStateHandle, playerRepository, validatePlayerNameUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.isLoading).isFalse()
            assertThat(state.error).isEqualTo("Database error")
        }
    }

    @Test
    fun `onNameChange updates name in state`() = runTest {
        // Given
        coEvery { playerRepository.getPlayerById(1L) } returns testPlayer
        viewModel = EditPlayerViewModel(savedStateHandle, playerRepository, validatePlayerNameUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem() // loaded state

            // When
            viewModel.onNameChange("Jane")

            // Then
            val state = awaitItem()
            assertThat(state.name).isEqualTo("Jane")
        }
    }

    @Test
    fun `savePlayer with same name succeeds`() = runTest {
        // Given
        coEvery { playerRepository.getPlayerById(1L) } returns testPlayer
        viewModel = EditPlayerViewModel(savedStateHandle, playerRepository, validatePlayerNameUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        coEvery { validatePlayerNameUseCase("John", 1L) } returns ValidationResult.Valid
        coEvery { playerRepository.updatePlayer(any()) } returns Unit

        viewModel.uiState.test {
            awaitItem() // loaded state

            // When
            viewModel.savePlayer()
            testDispatcher.scheduler.advanceUntilIdle()

            // Then - isSaving = true
            awaitItem()

            // Then - isSaved = true
            val savedState = awaitItem()
            assertThat(savedState.isSaving).isFalse()
            assertThat(savedState.isSaved).isTrue()

            coVerify {
                playerRepository.updatePlayer(
                    match { it.id == 1L && it.name == "John" }
                )
            }
        }
    }

    @Test
    fun `savePlayer with new valid name succeeds`() = runTest {
        // Given
        coEvery { playerRepository.getPlayerById(1L) } returns testPlayer
        viewModel = EditPlayerViewModel(savedStateHandle, playerRepository, validatePlayerNameUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        coEvery { validatePlayerNameUseCase("Jane", 1L) } returns ValidationResult.Valid
        coEvery { playerRepository.updatePlayer(any()) } returns Unit

        viewModel.uiState.test {
            awaitItem() // loaded state

            // When - change name
            viewModel.onNameChange("Jane")
            awaitItem() // name changed state

            // When - save
            viewModel.savePlayer()
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            awaitItem() // isSaving = true
            val savedState = awaitItem()
            assertThat(savedState.isSaved).isTrue()

            coVerify {
                playerRepository.updatePlayer(
                    match { it.id == 1L && it.name == "Jane" }
                )
            }
        }
    }

    @Test
    fun `savePlayer with duplicate name shows error`() = runTest {
        // Given
        coEvery { playerRepository.getPlayerById(1L) } returns testPlayer
        viewModel = EditPlayerViewModel(savedStateHandle, playerRepository, validatePlayerNameUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        val errorMessage = "Player already exists"
        coEvery { validatePlayerNameUseCase("Jane", 1L) } returns ValidationResult.Invalid(errorMessage)

        viewModel.uiState.test {
            awaitItem() // loaded state

            // When - change name
            viewModel.onNameChange("Jane")
            awaitItem() // name changed state

            // When - save
            viewModel.savePlayer()
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            awaitItem() // isSaving = true
            val errorState = awaitItem()
            assertThat(errorState.isSaving).isFalse()
            assertThat(errorState.nameError).isEqualTo(errorMessage)

            coVerify(exactly = 0) { playerRepository.updatePlayer(any()) }
        }
    }

    @Test
    fun `deletePlayer succeeds`() = runTest {
        // Given
        coEvery { playerRepository.getPlayerById(1L) } returns testPlayer
        viewModel = EditPlayerViewModel(savedStateHandle, playerRepository, validatePlayerNameUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        coEvery { playerRepository.deletePlayerById(1L) } returns Unit

        viewModel.uiState.test {
            awaitItem() // loaded state

            // When
            viewModel.deletePlayer()
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val state = awaitItem()
            assertThat(state.isSaved).isTrue()

            coVerify { playerRepository.deletePlayerById(1L) }
        }
    }

    @Test
    fun `deletePlayer handles exception`() = runTest {
        // Given
        coEvery { playerRepository.getPlayerById(1L) } returns testPlayer
        viewModel = EditPlayerViewModel(savedStateHandle, playerRepository, validatePlayerNameUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        coEvery { playerRepository.deletePlayerById(1L) } throws Exception("Delete failed")

        viewModel.uiState.test {
            awaitItem() // loaded state

            // When
            viewModel.deletePlayer()
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val state = awaitItem()
            assertThat(state.error).isEqualTo("Delete failed")
        }
    }
}
