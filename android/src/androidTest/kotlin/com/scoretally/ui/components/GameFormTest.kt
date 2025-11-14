package com.scoretally.ui.components

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.scoretally.ui.theme.ScoreTallyTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GameFormTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun gameForm_displaysAllFields() {
        // Given
        composeTestRule.setContent {
            ScoreTallyTheme {
                GameForm(
                    name = "",
                    onNameChange = {},
                    minPlayers = "",
                    onMinPlayersChange = {},
                    maxPlayers = "",
                    onMaxPlayersChange = {},
                    averageDuration = "",
                    onAverageDurationChange = {},
                    category = "",
                    onCategoryChange = {},
                    description = "",
                    onDescriptionChange = {},
                    scoreIncrement = "",
                    onScoreIncrementChange = {},
                    hasDice = false,
                    onHasDiceChange = {},
                    diceCount = "",
                    onDiceCountChange = {},
                    diceFaces = "",
                    onDiceFacesChange = {},
                    allowNegativeScores = false,
                    onAllowNegativeScoresChange = {},
                    onSave = {},
                    isSaving = false,
                    canSave = true,
                    saveButtonText = "Save"
                )
            }
        }

        // Then - verify all fields exist
        composeTestRule.onNodeWithText("Game name *").assertExists()
        composeTestRule.onNodeWithText("Min players").assertExists()
        composeTestRule.onNodeWithText("Max players").assertExists()
        composeTestRule.onNodeWithText("Average duration (min)").assertExists()
        composeTestRule.onNodeWithText("Category").assertExists()
        composeTestRule.onNodeWithText("Description").assertExists()
        composeTestRule.onNodeWithText("Score increment").assertExists()
        composeTestRule.onNodeWithText("Save").assertExists()
    }

    @Test
    fun gameForm_nameInput_updatesValue() {
        // Given
        var currentName = ""
        composeTestRule.setContent {
            ScoreTallyTheme {
                GameForm(
                    name = currentName,
                    onNameChange = { currentName = it },
                    minPlayers = "",
                    onMinPlayersChange = {},
                    maxPlayers = "",
                    onMaxPlayersChange = {},
                    averageDuration = "",
                    onAverageDurationChange = {},
                    category = "",
                    onCategoryChange = {},
                    description = "",
                    onDescriptionChange = {},
                    scoreIncrement = "",
                    onScoreIncrementChange = {},
                    hasDice = false,
                    onHasDiceChange = {},
                    diceCount = "",
                    onDiceCountChange = {},
                    diceFaces = "",
                    onDiceFacesChange = {},
                    allowNegativeScores = false,
                    onAllowNegativeScoresChange = {},
                    onSave = {},
                    isSaving = false,
                    canSave = true,
                    saveButtonText = "Save"
                )
            }
        }

        // When
        composeTestRule.onNodeWithText("Game name *").performTextInput("Chess")

        // Then
        assert(currentName == "Chess")
    }

    @Test
    fun gameForm_saveButton_isDisabledWhenSaving() {
        // Given
        composeTestRule.setContent {
            ScoreTallyTheme {
                GameForm(
                    name = "Chess",
                    onNameChange = {},
                    minPlayers = "2",
                    onMinPlayersChange = {},
                    maxPlayers = "2",
                    onMaxPlayersChange = {},
                    averageDuration = "30",
                    onAverageDurationChange = {},
                    category = "Strategy",
                    onCategoryChange = {},
                    description = "Board game",
                    onDescriptionChange = {},
                    scoreIncrement = "1",
                    onScoreIncrementChange = {},
                    hasDice = false,
                    onHasDiceChange = {},
                    diceCount = "",
                    onDiceCountChange = {},
                    diceFaces = "",
                    onDiceFacesChange = {},
                    allowNegativeScores = false,
                    onAllowNegativeScoresChange = {},
                    onSave = {},
                    isSaving = true,
                    canSave = true,
                    saveButtonText = "Save"
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Save").assertIsNotEnabled()
    }

    @Test
    fun gameForm_saveButton_isDisabledWhenCanSaveIsFalse() {
        // Given
        composeTestRule.setContent {
            ScoreTallyTheme {
                GameForm(
                    name = "",
                    onNameChange = {},
                    minPlayers = "",
                    onMinPlayersChange = {},
                    maxPlayers = "",
                    onMaxPlayersChange = {},
                    averageDuration = "",
                    onAverageDurationChange = {},
                    category = "",
                    onCategoryChange = {},
                    description = "",
                    onDescriptionChange = {},
                    scoreIncrement = "",
                    onScoreIncrementChange = {},
                    hasDice = false,
                    onHasDiceChange = {},
                    diceCount = "",
                    onDiceCountChange = {},
                    diceFaces = "",
                    onDiceFacesChange = {},
                    allowNegativeScores = false,
                    onAllowNegativeScoresChange = {},
                    onSave = {},
                    isSaving = false,
                    canSave = false,
                    saveButtonText = "Save"
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Save").assertIsNotEnabled()
    }

    @Test
    fun gameForm_saveButton_triggersCallback() {
        // Given
        var saveClicked = false
        composeTestRule.setContent {
            ScoreTallyTheme {
                GameForm(
                    name = "Chess",
                    onNameChange = {},
                    minPlayers = "2",
                    onMinPlayersChange = {},
                    maxPlayers = "2",
                    onMaxPlayersChange = {},
                    averageDuration = "30",
                    onAverageDurationChange = {},
                    category = "Strategy",
                    onCategoryChange = {},
                    description = "Board game",
                    onDescriptionChange = {},
                    scoreIncrement = "1",
                    onScoreIncrementChange = {},
                    hasDice = false,
                    onHasDiceChange = {},
                    diceCount = "",
                    onDiceCountChange = {},
                    diceFaces = "",
                    onDiceFacesChange = {},
                    allowNegativeScores = false,
                    onAllowNegativeScoresChange = {},
                    onSave = { saveClicked = true },
                    isSaving = false,
                    canSave = true,
                    saveButtonText = "Save"
                )
            }
        }

        // When
        composeTestRule.onNodeWithText("Save").performClick()

        // Then
        assert(saveClicked)
    }

    @Test
    fun gameForm_allowNegativeScoresSwitch_togglesValue() {
        // Given
        var allowNegative = false
        composeTestRule.setContent {
            ScoreTallyTheme {
                GameForm(
                    name = "",
                    onNameChange = {},
                    minPlayers = "",
                    onMinPlayersChange = {},
                    maxPlayers = "",
                    onMaxPlayersChange = {},
                    averageDuration = "",
                    onAverageDurationChange = {},
                    category = "",
                    onCategoryChange = {},
                    description = "",
                    onDescriptionChange = {},
                    scoreIncrement = "",
                    onScoreIncrementChange = {},
                    hasDice = false,
                    onHasDiceChange = {},
                    diceCount = "",
                    onDiceCountChange = {},
                    diceFaces = "",
                    onDiceFacesChange = {},
                    allowNegativeScores = allowNegative,
                    onAllowNegativeScoresChange = { allowNegative = it },
                    onSave = {},
                    isSaving = false,
                    canSave = true,
                    saveButtonText = "Save"
                )
            }
        }

        // When - find the switch by its parent text
        composeTestRule.onNodeWithText("Allow negative scores").performClick()

        // Then
        assert(allowNegative)
    }
}
