package com.scoretally.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.scoretally.R
import com.scoretally.domain.model.Player
import com.scoretally.domain.repository.PlayerRepository
import com.scoretally.util.ResourceProvider
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ValidatePlayerNameUseCaseTest {

    private lateinit var playerRepository: PlayerRepository
    private lateinit var resourceProvider: ResourceProvider
    private lateinit var useCase: ValidatePlayerNameUseCase

    private val errorMessage = "A player with this name already exists"

    @Before
    fun setup() {
        playerRepository = mockk()
        resourceProvider = mockk()
        useCase = ValidatePlayerNameUseCase(playerRepository, resourceProvider)

        every { resourceProvider.getString(R.string.error_player_name_exists) } returns errorMessage
    }

    @Test
    fun `validate returns Valid when name is unique`() = runTest {
        // Given
        val existingPlayers = listOf(
            Player(id = 1, name = "John", preferredColor = "#FF0000"),
            Player(id = 2, name = "Jane", preferredColor = "#00FF00")
        )
        coEvery { playerRepository.getAllPlayers() } returns flowOf(existingPlayers)

        // When
        val result = useCase("Bob")

        // Then
        assertThat(result).isEqualTo(ValidationResult.Valid)
    }

    @Test
    fun `validate returns Invalid when exact name exists`() = runTest {
        // Given
        val existingPlayers = listOf(
            Player(id = 1, name = "John", preferredColor = "#FF0000"),
            Player(id = 2, name = "Jane", preferredColor = "#00FF00")
        )
        coEvery { playerRepository.getAllPlayers() } returns flowOf(existingPlayers)

        // When
        val result = useCase("John")

        // Then
        assertThat(result).isInstanceOf(ValidationResult.Invalid::class.java)
        assertThat((result as ValidationResult.Invalid).errorMessage).isEqualTo(errorMessage)
    }

    @Test
    fun `validate returns Invalid when name exists with different case`() = runTest {
        // Given
        val existingPlayers = listOf(
            Player(id = 1, name = "John", preferredColor = "#FF0000")
        )
        coEvery { playerRepository.getAllPlayers() } returns flowOf(existingPlayers)

        // When
        val result = useCase("JOHN")

        // Then
        assertThat(result).isInstanceOf(ValidationResult.Invalid::class.java)
        assertThat((result as ValidationResult.Invalid).errorMessage).isEqualTo(errorMessage)
    }

    @Test
    fun `validate returns Valid when name has extra spaces`() = runTest {
        // Given
        val existingPlayers = listOf(
            Player(id = 1, name = "John", preferredColor = "#FF0000")
        )
        coEvery { playerRepository.getAllPlayers() } returns flowOf(existingPlayers)

        // When
        val result = useCase("  John  ")

        // Then - should be Invalid because trimmed name matches
        assertThat(result).isInstanceOf(ValidationResult.Invalid::class.java)
    }

    @Test
    fun `validate returns Valid when blank name`() = runTest {
        // Given
        val existingPlayers = listOf(
            Player(id = 1, name = "John", preferredColor = "#FF0000")
        )
        coEvery { playerRepository.getAllPlayers() } returns flowOf(existingPlayers)

        // When
        val result = useCase("   ")

        // Then - blank validation is handled by UI
        assertThat(result).isEqualTo(ValidationResult.Valid)
    }

    @Test
    fun `validate excludes player ID when editing`() = runTest {
        // Given
        val existingPlayers = listOf(
            Player(id = 1, name = "John", preferredColor = "#FF0000"),
            Player(id = 2, name = "Jane", preferredColor = "#00FF00")
        )
        coEvery { playerRepository.getAllPlayers() } returns flowOf(existingPlayers)

        // When - editing player 1, name stays "John"
        val result = useCase("John", excludePlayerId = 1)

        // Then - should be Valid because we're editing the same player
        assertThat(result).isEqualTo(ValidationResult.Valid)
    }

    @Test
    fun `validate returns Invalid when name exists for different player during edit`() = runTest {
        // Given
        val existingPlayers = listOf(
            Player(id = 1, name = "John", preferredColor = "#FF0000"),
            Player(id = 2, name = "Jane", preferredColor = "#00FF00")
        )
        coEvery { playerRepository.getAllPlayers() } returns flowOf(existingPlayers)

        // When - editing player 1, trying to rename to "Jane"
        val result = useCase("Jane", excludePlayerId = 1)

        // Then - should be Invalid because Jane already exists
        assertThat(result).isInstanceOf(ValidationResult.Invalid::class.java)
    }

    @Test
    fun `validate returns Valid for empty player list`() = runTest {
        // Given
        coEvery { playerRepository.getAllPlayers() } returns flowOf(emptyList())

        // When
        val result = useCase("NewPlayer")

        // Then
        assertThat(result).isEqualTo(ValidationResult.Valid)
    }
}
