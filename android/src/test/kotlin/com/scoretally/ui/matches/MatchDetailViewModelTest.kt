package com.scoretally.ui.matches

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.scoretally.domain.model.*
import com.scoretally.domain.repository.MatchPlayerRepository
import com.scoretally.domain.usecase.GetMatchWithDetailsUseCase
import com.scoretally.util.Logger
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class MatchDetailViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getMatchWithDetailsUseCase: GetMatchWithDetailsUseCase
    private lateinit var matchPlayerRepository: MatchPlayerRepository
    private lateinit var logger: Logger
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: MatchDetailViewModel

    private val testGame = Game(
        id = 1L,
        name = "Test Game",
        minPlayers = 2,
        maxPlayers = 4,
        averageDuration = 60,
        allowNegativeScores = false,
        gridType = GridType.STANDARD
    )

    private val testPlayer = Player(
        id = 1L,
        name = "John",
        preferredColor = "#FF0000"
    )

    private val testMatchPlayer = MatchPlayer(
        id = 1L,
        matchId = 1L,
        playerId = 1L,
        score = 10,
        rank = 1
    )

    private val testMatch = Match(
        id = 1L,
        gameId = 1L,
        date = LocalDateTime.now(),
        duration = 60,
        isCompleted = false
    )

    private val testMatchWithDetails = MatchWithDetails(
        match = testMatch,
        game = testGame,
        playerScores = listOf(
            PlayerScore(testMatchPlayer, testPlayer)
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getMatchWithDetailsUseCase = mockk()
        matchPlayerRepository = mockk(relaxed = true)
        logger = mockk(relaxed = true)
        savedStateHandle = SavedStateHandle(mapOf("matchId" to 1L))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loads match details successfully on init`() = runTest {
        // Given
        coEvery { getMatchWithDetailsUseCase(1L) } returns flowOf(testMatchWithDetails)

        // When
        viewModel = MatchDetailViewModel(
            savedStateHandle,
            getMatchWithDetailsUseCase,
            matchPlayerRepository,
            logger
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.matchDetails).isEqualTo(testMatchWithDetails)
            assertThat(state.isLoading).isFalse()
            assertThat(state.error).isNull()
        }

        verify { logger.log("MatchDetailViewModel: Loading match 1") }
        verify { logger.log("MatchDetailViewModel: Match loaded successfully - Game: Test Game") }
    }

    @Test
    fun `sets error when match not found`() = runTest {
        // Given
        coEvery { getMatchWithDetailsUseCase(1L) } returns flowOf(null)

        // When
        viewModel = MatchDetailViewModel(
            savedStateHandle,
            getMatchWithDetailsUseCase,
            matchPlayerRepository,
            logger
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.matchDetails).isNull()
            assertThat(state.isLoading).isFalse()
            assertThat(state.error).isEqualTo("Match not found")
        }

        verify { logger.log("MatchDetailViewModel: Match 1 not found") }
    }

    @Test
    fun `handles loading exception`() = runTest {
        // Given
        coEvery { getMatchWithDetailsUseCase(1L) } throws Exception("Database error")

        // When
        viewModel = MatchDetailViewModel(
            savedStateHandle,
            getMatchWithDetailsUseCase,
            matchPlayerRepository,
            logger
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.isLoading).isFalse()
            assertThat(state.error).contains("Database error")
        }

        verify { logger.recordException(any()) }
    }

    @Test
    fun `updateScore updates player score`() = runTest {
        // Given
        coEvery { getMatchWithDetailsUseCase(1L) } returns flowOf(testMatchWithDetails)
        viewModel = MatchDetailViewModel(
            savedStateHandle,
            getMatchWithDetailsUseCase,
            matchPlayerRepository,
            logger
        )
        testDispatcher.scheduler.advanceUntilIdle()

        coEvery { matchPlayerRepository.updateMatchPlayer(any()) } returns Unit

        // When
        viewModel.updateScore(testMatchPlayer, 20)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify {
            matchPlayerRepository.updateMatchPlayer(
                match { it.id == 1L && it.score == 20 }
            )
        }
    }

    @Test
    fun `incrementScore increases score by default value`() = runTest {
        // Given
        coEvery { getMatchWithDetailsUseCase(1L) } returns flowOf(testMatchWithDetails)
        viewModel = MatchDetailViewModel(
            savedStateHandle,
            getMatchWithDetailsUseCase,
            matchPlayerRepository,
            logger
        )
        testDispatcher.scheduler.advanceUntilIdle()

        coEvery { matchPlayerRepository.updateMatchPlayer(any()) } returns Unit

        // When
        viewModel.incrementScore(testMatchPlayer)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify {
            matchPlayerRepository.updateMatchPlayer(
                match { it.score == 11 } // 10 + 1
            )
        }
    }

    @Test
    fun `incrementScore increases score by custom value`() = runTest {
        // Given
        coEvery { getMatchWithDetailsUseCase(1L) } returns flowOf(testMatchWithDetails)
        viewModel = MatchDetailViewModel(
            savedStateHandle,
            getMatchWithDetailsUseCase,
            matchPlayerRepository,
            logger
        )
        testDispatcher.scheduler.advanceUntilIdle()

        coEvery { matchPlayerRepository.updateMatchPlayer(any()) } returns Unit

        // When
        viewModel.incrementScore(testMatchPlayer, increment = 5)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify {
            matchPlayerRepository.updateMatchPlayer(
                match { it.score == 15 } // 10 + 5
            )
        }
    }

    @Test
    fun `decrementScore respects allowNegativeScores = false`() = runTest {
        // Given - game doesn't allow negative scores
        val gameNoNegative = testGame.copy(allowNegativeScores = false)
        val matchDetails = testMatchWithDetails.copy(game = gameNoNegative)
        val matchPlayerLowScore = testMatchPlayer.copy(score = 5)

        coEvery { getMatchWithDetailsUseCase(1L) } returns flowOf(matchDetails)
        viewModel = MatchDetailViewModel(
            savedStateHandle,
            getMatchWithDetailsUseCase,
            matchPlayerRepository,
            logger
        )
        testDispatcher.scheduler.advanceUntilIdle()

        coEvery { matchPlayerRepository.updateMatchPlayer(any()) } returns Unit

        // When - trying to decrement by 10 (would result in -5)
        viewModel.decrementScore(matchPlayerLowScore, decrement = 10)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then - score should be 0, not -5
        coVerify {
            matchPlayerRepository.updateMatchPlayer(
                match { it.score == 0 } // Clamped to 0
            )
        }
    }

    @Test
    fun `decrementScore allows negative when allowNegativeScores = true`() = runTest {
        // Given - game allows negative scores
        val gameAllowNegative = testGame.copy(allowNegativeScores = true)
        val matchDetails = testMatchWithDetails.copy(game = gameAllowNegative)
        val matchPlayerLowScore = testMatchPlayer.copy(score = 5)

        coEvery { getMatchWithDetailsUseCase(1L) } returns flowOf(matchDetails)
        viewModel = MatchDetailViewModel(
            savedStateHandle,
            getMatchWithDetailsUseCase,
            matchPlayerRepository,
            logger
        )
        testDispatcher.scheduler.advanceUntilIdle()

        coEvery { matchPlayerRepository.updateMatchPlayer(any()) } returns Unit

        // When - decrementing by 10
        viewModel.decrementScore(matchPlayerLowScore, decrement = 10)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then - score should be -5
        coVerify {
            matchPlayerRepository.updateMatchPlayer(
                match { it.score == -5 } // Allows negative
            )
        }
    }

    @Test
    fun `decrementScore with default value`() = runTest {
        // Given
        coEvery { getMatchWithDetailsUseCase(1L) } returns flowOf(testMatchWithDetails)
        viewModel = MatchDetailViewModel(
            savedStateHandle,
            getMatchWithDetailsUseCase,
            matchPlayerRepository,
            logger
        )
        testDispatcher.scheduler.advanceUntilIdle()

        coEvery { matchPlayerRepository.updateMatchPlayer(any()) } returns Unit

        // When
        viewModel.decrementScore(testMatchPlayer)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify {
            matchPlayerRepository.updateMatchPlayer(
                match { it.score == 9 } // 10 - 1
            )
        }
    }
}
