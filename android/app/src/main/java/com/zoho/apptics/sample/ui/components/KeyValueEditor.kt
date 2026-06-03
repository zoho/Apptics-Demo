package com.zoho.apptics.sample.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class KvEntry(val key: String = "", val value: String = "")

@Composable
fun KeyValueEditor(
    entries: List<KvEntry>,
    onChange: (List<KvEntry>) -> Unit,
    addLabel: String = "Add property",
    keyLabel: String = "Key",
    valueLabel: String = "Value"
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        entries.forEachIndexed { index, entry ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = entry.key,
                    onValueChange = { newKey ->
                        onChange(entries.toMutableList().also { it[index] = entry.copy(key = newKey) })
                    },
                    label = { Text(keyLabel, style = MaterialTheme.typography.labelMedium) },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = entry.value,
                    onValueChange = { newValue ->
                        onChange(entries.toMutableList().also { it[index] = entry.copy(value = newValue) })
                    },
                    label = { Text(valueLabel, style = MaterialTheme.typography.labelMedium) },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = {
                    onChange(entries.toMutableList().also { it.removeAt(index) })
                }) {
                    Icon(Icons.Filled.Close, contentDescription = "Remove", modifier = Modifier.size(20.dp))
                }
            }
        }
        TextButton(onClick = { onChange(entries + KvEntry()) }) {
            Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Text(text = "  $addLabel")
        }
    }
}
