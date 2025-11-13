package com.scoretally.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun NumberField(
    value: Int,
    onValueChange: (Int) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    minValue: Int = 1,
    maxValue: Int = Int.MAX_VALUE,
    enabled: Boolean = true
) {
    var textValue by remember(value) { mutableStateOf(value.toString()) }

    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledTonalButton(
                onClick = {
                    if (value > minValue) {
                        val newValue = value - 1
                        onValueChange(newValue)
                        textValue = newValue.toString()
                    }
                },
                enabled = enabled && value > minValue,
                modifier = Modifier.size(40.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("-", style = MaterialTheme.typography.headlineSmall)
            }

            OutlinedTextField(
                value = textValue,
                onValueChange = { newValue ->
                    textValue = newValue
                    if (newValue.isEmpty()) {
                        // Permet de vider le champ
                    } else {
                        newValue.toIntOrNull()?.let { intValue ->
                            if (intValue in minValue..maxValue) {
                                onValueChange(intValue)
                            }
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                enabled = enabled,
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
            )

            FilledTonalButton(
                onClick = {
                    if (value < maxValue) {
                        val newValue = value + 1
                        onValueChange(newValue)
                        textValue = newValue.toString()
                    }
                },
                enabled = enabled && value < maxValue,
                modifier = Modifier.size(40.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("+", style = MaterialTheme.typography.headlineSmall)
            }
        }
    }
}
