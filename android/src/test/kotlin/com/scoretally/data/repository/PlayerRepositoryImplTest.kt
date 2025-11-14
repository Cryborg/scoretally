package com.scoretally.data.repository

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.scoretally.data.local.dao.PlayerDao
import com.scoretally.data.local.entity.PlayerEntity
import com.scoretally.domain.model.Player
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class PlayerRepositoryImplTest {

    private lateinit var playerDao: PlayerDao
    private lateinit var repository: PlayerRepositoryImpl

    private val testPlayerEntity = PlayerEntity(
        id = 1L,
        name = "John",
        avatarUri = null,
        preferredColor = "#FF0000"
    )

    private val testPlayer = Player(
        id = 1L,
        name = "John",
        avatarUri = null,
        preferredColor = "#FF0000"
    )

    @Before
    fun setup() {
        playerDao = mockk(relaxed = true)
        repository = PlayerRepositoryImpl(playerDao)
    }

    @Test
    fun `getAllPlayers returns mapped domain models`() = runTest {
        // Given
        val entities = listOf(testPlayerEntity)
        coEvery { playerDao.getAllPlayers() } returns flowOf(entities)

        // When
        repository.getAllPlayers().test {
            val players = awaitItem()

            // Then
            assertThat(players).hasSize(1)
            assertThat(players[0].id).isEqualTo(testPlayer.id)
            assertThat(players[0].name).isEqualTo(testPlayer.name)
            assertThat(players[0].preferredColor).isEqualTo(testPlayer.preferredColor)
            awaitComplete()
        }
    }

    @Test
    fun `getAllPlayers returns empty list when dao returns empty`() = runTest {
        // Given
        coEvery { playerDao.getAllPlayers() } returns flowOf(emptyList())

        // When
        repository.getAllPlayers().test {
            val players = awaitItem()

            // Then
            assertThat(players).isEmpty()
            awaitComplete()
        }
    }

    @Test
    fun `getPlayerById returns mapped domain model`() = runTest {
        // Given
        coEvery { playerDao.getPlayerById(1L) } returns testPlayerEntity

        // When
        val player = repository.getPlayerById(1L)

        // Then
        assertThat(player).isNotNull()
        assertThat(player?.id).isEqualTo(testPlayer.id)
        assertThat(player?.name).isEqualTo(testPlayer.name)
    }

    @Test
    fun `getPlayerById returns null when dao returns null`() = runTest {
        // Given
        coEvery { playerDao.getPlayerById(999L) } returns null

        // When
        val player = repository.getPlayerById(999L)

        // Then
        assertThat(player).isNull()
    }

    @Test
    fun `searchPlayers returns mapped domain models`() = runTest {
        // Given
        coEvery { playerDao.searchPlayers("John") } returns flowOf(listOf(testPlayerEntity))

        // When
        repository.searchPlayers("John").test {
            val players = awaitItem()

            // Then
            assertThat(players).hasSize(1)
            assertThat(players[0].name).isEqualTo("John")
            awaitComplete()
        }
    }

    @Test
    fun `insertPlayer converts to entity and delegates to dao`() = runTest {
        // Given
        coEvery { playerDao.insertPlayer(any()) } returns 1L

        // When
        val id = repository.insertPlayer(testPlayer)

        // Then
        assertThat(id).isEqualTo(1L)
        coVerify {
            playerDao.insertPlayer(
                match {
                    it.name == testPlayer.name &&
                    it.preferredColor == testPlayer.preferredColor
                }
            )
        }
    }

    @Test
    fun `updatePlayer converts to entity and delegates to dao`() = runTest {
        // Given
        coEvery { playerDao.updatePlayer(any()) } returns Unit

        // When
        repository.updatePlayer(testPlayer)

        // Then
        coVerify {
            playerDao.updatePlayer(
                match {
                    it.id == testPlayer.id &&
                    it.name == testPlayer.name
                }
            )
        }
    }

    @Test
    fun `deletePlayer converts to entity and delegates to dao`() = runTest {
        // Given
        coEvery { playerDao.deletePlayer(any()) } returns Unit

        // When
        repository.deletePlayer(testPlayer)

        // Then
        coVerify {
            playerDao.deletePlayer(
                match { it.id == testPlayer.id }
            )
        }
    }

    @Test
    fun `deletePlayerById delegates to dao`() = runTest {
        // Given
        coEvery { playerDao.deletePlayerById(1L) } returns Unit

        // When
        repository.deletePlayerById(1L)

        // Then
        coVerify { playerDao.deletePlayerById(1L) }
    }
}
