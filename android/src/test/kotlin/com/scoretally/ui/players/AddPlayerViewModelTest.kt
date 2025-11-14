package com.scoretally.ui.players

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
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
class AddPlayerViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var playerRepository: PlayerRepository
    private lateinit var validatePlayerNameUseCase: ValidatePlayerNameUseCase
    private lateinit var viewModel: AddPlayerViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        playerRepository = mockk(relaxed = true)
        validatePlayerNameUseCase = mockk()
        viewModel = AddPlayerViewModel(playerRepository, validatePlayerNameUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() = runTest {
        // Given
        viewModel.uiState.test {
            // When
            val state = awaitItem()

            // Then
            assertThat(state.name).isEmpty()
            assertThat(state.preferredColor).isEqualTo("#6750A4")
            assertThat(state.isSaving).isFalse()
            assertThat(state.isSaved).isFalse()
            assertThat(state.nameError).isNull()
        }
    }

    @Test
    fun `onNameChange updates name in state`() = runTest {
        // Given
        viewModel.uiState.test {
            awaitItem() // initial state

            // When
            viewModel.onNameChange("John")

            // Then
            val state = awaitItem()
            assertThat(state.name).isEqualTo("John")
        }
    }

    @Test
    fun `onColorChange updates color in state`() = runTest {
        // Given
        viewModel.uiState.test {
            awaitItem() // initial state

            // When
            viewModel.onColorChange("#FF0000")

            // Then
            val state = awaitItem()
            assertThat(state.preferredColor).isEqualTo("#FF0000")
        }
    }

    @Test
    fun `savePlayer with blank name does nothing`() = runTest {
        // Given
        viewModel.onNameChange("   ")

        // When
        viewModel.savePlayer()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify(exactly = 0) { playerRepository.insertPlayer(any()) }
        coVerify(exactly = 0) { validatePlayerNameUseCase(any(), any()) }
    }

    @Test
    fun `savePlayer with valid name saves player successfully`() = runTest {
        // Given
        viewModel.onNameChange("John")
        viewModel.onColorChange("#FF0000")
        coEvery { validatePlayerNameUseCase("John", null) } returns ValidationResult.Valid
        coEvery { playerRepository.insertPlayer(any()) } returns 1L

        viewModel.uiState.test {
            awaitItem() // initial state

            // When
            viewModel.savePlayer()
            testDispatcher.scheduler.advanceUntilIdle()

            // Then - isSaving = true
            val savingState = awaitItem()
            assertThat(savingState.isSaving).isTrue()
            assertThat(savingState.nameError).isNull()

            // Then - isSaved = true
            val savedState = awaitItem()
            assertThat(savedState.isSaving).isFalse()
            assertThat(savedState.isSaved).isTrue()

            coVerify {
                playerRepository.insertPlayer(
                    match {
                        it.name == "John" && it.preferredColor == "#FF0000"
                    }
                )
            }
        }
    }

    @Test
    fun `savePlayer with duplicate name shows error`() = runTest {
        // Given
        viewModel.onNameChange("John")
        val errorMessage = "Player already exists"
        coEvery { validatePlayerNameUseCase("John", null) } returns ValidationResult.Invalid(errorMessage)

        viewModel.uiState.test {
            awaitItem() // initial state

            // When
            viewModel.savePlayer()
            testDispatcher.scheduler.advanceUntilIdle()

            // Then - isSaving = true
            val savingState = awaitItem()
            assertThat(savingState.isSaving).isTrue()

            // Then - error displayed
            val errorState = awaitItem()
            assertThat(errorState.isSaving).isFalse()
            assertThat(errorState.isSaved).isFalse()
            assertThat(errorState.nameError).isEqualTo(errorMessage)

            coVerify(exactly = 0) { playerRepository.insertPlayer(any()) }
        }
    }

    @Test
    fun `savePlayer trims whitespace from name`() = runTest {
        // Given
        viewModel.onNameChange("  John  ")
        coEvery { validatePlayerNameUseCase("  John  ", null) } returns ValidationResult.Valid
        coEvery { playerRepository.insertPlayer(any()) } returns 1L

        // When
        viewModel.savePlayer()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify {
            playerRepository.insertPlayer(
                match { it.name == "John" } // trimmed
            )
        }
    }

    @Test
    fun `savePlayer handles repository exception`() = runTest {
        // Given
        viewModel.onNameChange("John")
        coEvery { validatePlayerNameUseCase("John", null) } returns ValidationResult.Valid
        coEvery { playerRepository.insertPlayer(any()) } throws Exception("Database error")

        viewModel.uiState.test {
            awaitItem() // initial state

            // When
            viewModel.savePlayer()
            testDispatcher.scheduler.advanceUntilIdle()

            // Then - isSaving = true
            awaitItem()

            // Then - isSaving = false, isSaved = false
            val errorState = awaitItem()
            assertThat(errorState.isSaving).isFalse()
            assertThat(errorState.isSaved).isFalse()
        }
    }
}
