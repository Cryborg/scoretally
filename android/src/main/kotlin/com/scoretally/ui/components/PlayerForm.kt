package com.scoretally.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.scoretally.R
import kotlinx.coroutines.delay

@Composable
fun PlayerForm(
    name: String,
    onNameChange: (String) -> Unit,
    preferredColor: String,
    onColorChange: (String) -> Unit,
    onSave: () -> Unit,
    isSaving: Boolean,
    canSave: Boolean,
    saveButtonText: String,
    modifier: Modifier = Modifier,
    autoFocus: Boolean = true
) {
    val focusRequester = FocusRequester()

    if (autoFocus) {
        LaunchedEffect(Unit) {
            delay(100)
            focusRequester.requestFocus()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text(stringResource(R.string.add_player_name_label)) },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            enabled = !isSaving,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences
            )
        )

        Text(
            stringResource(R.string.add_player_color_label),
            style = MaterialTheme.typography.labelLarge
        )

        ColorPicker(
            selectedColor = preferredColor,
            onColorSelected = onColorChange,
            enabled = !isSaving
        )

        Spacer(modifier = Modifier.height(8.dp))

        LoadingButton(
            onClick = onSave,
            text = saveButtonText,
            isLoading = isSaving,
            enabled = canSave,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
