package com.scoretally.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.scoretally.data.local.AppDatabase
import com.scoretally.data.local.entity.PlayerEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlayerDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var playerDao: PlayerDao

    private val testPlayer1 = PlayerEntity(
        id = 0,
        name = "John",
        avatarUri = null,
        preferredColor = "#FF0000"
    )

    private val testPlayer2 = PlayerEntity(
        id = 0,
        name = "Jane",
        avatarUri = null,
        preferredColor = "#00FF00"
    )

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).build()
        playerDao = database.playerDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertPlayer_returnsGeneratedId() = runTest {
        // When
        val id = playerDao.insertPlayer(testPlayer1)

        // Then
        assertThat(id).isGreaterThan(0)
    }

    @Test
    fun insertPlayer_andGetById_returnsPlayer() = runTest {
        // Given
        val id = playerDao.insertPlayer(testPlayer1)

        // When
        val player = playerDao.getPlayerById(id)

        // Then
        assertThat(player).isNotNull()
        assertThat(player?.name).isEqualTo("John")
        assertThat(player?.preferredColor).isEqualTo("#FF0000")
    }

    @Test
    fun getAllPlayers_returnsAllInsertedPlayers() = runTest {
        // Given
        playerDao.insertPlayer(testPlayer1)
        playerDao.insertPlayer(testPlayer2)

        // When
        playerDao.getAllPlayers().test {
            val players = awaitItem()

            // Then
            assertThat(players).hasSize(2)
            assertThat(players.map { it.name }).containsExactly("John", "Jane")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getAllPlayers_returnsEmptyListInitially() = runTest {
        // When
        playerDao.getAllPlayers().test {
            val players = awaitItem()

            // Then
            assertThat(players).isEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun updatePlayer_updatesPlayerData() = runTest {
        // Given
        val id = playerDao.insertPlayer(testPlayer1)
        val updatedPlayer = testPlayer1.copy(
            id = id,
            name = "John Updated",
            preferredColor = "#0000FF"
        )

        // When
        playerDao.updatePlayer(updatedPlayer)

        // Then
        val player = playerDao.getPlayerById(id)
        assertThat(player?.name).isEqualTo("John Updated")
        assertThat(player?.preferredColor).isEqualTo("#0000FF")
    }

    @Test
    fun deletePlayer_removesPlayer() = runTest {
        // Given
        val id = playerDao.insertPlayer(testPlayer1)
        val player = playerDao.getPlayerById(id)

        // When
        playerDao.deletePlayer(player!!)

        // Then
        val deletedPlayer = playerDao.getPlayerById(id)
        assertThat(deletedPlayer).isNull()
    }

    @Test
    fun deletePlayerById_removesPlayer() = runTest {
        // Given
        val id = playerDao.insertPlayer(testPlayer1)

        // When
        playerDao.deletePlayerById(id)

        // Then
        val deletedPlayer = playerDao.getPlayerById(id)
        assertThat(deletedPlayer).isNull()
    }

    @Test
    fun searchPlayers_findsMatchingPlayers() = runTest {
        // Given
        playerDao.insertPlayer(testPlayer1) // John
        playerDao.insertPlayer(testPlayer2) // Jane

        // When
        playerDao.searchPlayers("Jo").test {
            val players = awaitItem()

            // Then
            assertThat(players).hasSize(1)
            assertThat(players[0].name).isEqualTo("John")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun searchPlayers_isCaseInsensitive() = runTest {
        // Given
        playerDao.insertPlayer(testPlayer1) // John

        // When
        playerDao.searchPlayers("john").test {
            val players = awaitItem()

            // Then
            assertThat(players).hasSize(1)
            assertThat(players[0].name).isEqualTo("John")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun searchPlayers_returnsEmptyForNoMatch() = runTest {
        // Given
        playerDao.insertPlayer(testPlayer1)

        // When
        playerDao.searchPlayers("xyz").test {
            val players = awaitItem()

            // Then
            assertThat(players).isEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun insertMultiplePlayers_getAllPlayers_returnsInCorrectOrder() = runTest {
        // Given
        val player3 = testPlayer1.copy(name = "Alice")
        val player4 = testPlayer2.copy(name = "Bob")

        playerDao.insertPlayer(player3)
        playerDao.insertPlayer(player4)

        // When
        playerDao.getAllPlayers().test {
            val players = awaitItem()

            // Then - should be ordered by name
            assertThat(players).hasSize(2)
            assertThat(players.map { it.name }).containsExactly("Alice", "Bob").inOrder()
            cancelAndIgnoreRemainingEvents()
        }
    }
}
