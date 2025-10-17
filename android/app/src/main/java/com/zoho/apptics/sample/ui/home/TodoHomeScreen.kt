package com.zoho.apptics.sample.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zoho.apptics.analytics.AppticsAnalytics
import com.zoho.apptics.common.Apptics
import com.zoho.apptics.common.AppticsSettings
import com.zoho.apptics.common.AppticsTrackingState
import com.zoho.apptics.common.AppticsUser
import com.zoho.apptics.sample.analytics.TodoEvent
import com.zoho.apptics.sample.data.FakeTodoRepository
import com.zoho.apptics.sample.model.Task
import com.zoho.apptics.sample.model.TaskCategory
import java.util.Locale

@Composable
fun TodoHomeRoute(onEvent: (TodoEvent) -> Unit) {
    TodoHomeScreen(onEvent)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoHomeScreen(onEvent: (TodoEvent) -> Unit) {
    val tasks = remember { mutableStateListOf<Task>().apply { addAll(FakeTodoRepository.tasks) } }

    var title by rememberSaveable { mutableStateOf("") }
    var note by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf(TaskCategory.PERSONAL) }
    var showSheet by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                showSheet = true
                onEvent.invoke(TodoEvent.Add())
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Add task")
            }
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = "Today’s tasks",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
            )
            Text(
                text = "Tap the checkbox to complete tasks. ",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            tasks.forEach { task ->
                TaskCard(
                    task = task,
                    onToggle = {
                        val index = tasks.indexOfFirst { it.id == task.id }
                        if (index != -1) {
                            val updated = task.copy(isDone = !task.isDone)
                            tasks[index] = updated

                            onEvent(TodoEvent.Completed(updated.id, updated.isDone))
                        }
                    },
                    onDelete = {
                        if (tasks.remove(task)) {

                            onEvent(TodoEvent.Deleted(task.id))
                        }
                    },
                )
            }
        }
    }

    // Modal bottom sheet to add new tasks
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
        ) {
            TaskInputSheet(
                title = title,
                note = note,
                category = category,
                onTitleChange = { title = it },
                onNoteChange = { note = it },
                onCategoryChange = { category = it },
                onAddTask = {
                    if (title.isBlank()) return@TaskInputSheet
                    val newTask =
                        Task(
                            id = "task_${System.currentTimeMillis()}",
                            title = title.trim(),
                            note = note.trim(),
                            category = category,
                            isDone = false,
                            reminderMinutes = null,
                        )
                    tasks.add(0, newTask)

                    onEvent.invoke(TodoEvent.Added())

                    title = ""
                    note = ""
                    category = TaskCategory.PERSONAL
                    showSheet = false
                },
                onCancel = { showSheet = false },
            )
        }
    }
}

@Composable
private fun TaskCard(
    task: Task,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Checkbox(
                checked = task.isDone,
                onCheckedChange = { onToggle() },
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (task.note.isNotBlank()) {
                    Text(
                        text = task.note,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    text = "Category: ${task.category.displayName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete task")
            }
        }
    }
}

@Composable
private fun TaskInputSheet(
    title: String,
    note: String,
    category: TaskCategory,
    onTitleChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onCategoryChange: (TaskCategory) -> Unit,
    onAddTask: () -> Unit,
    onCancel: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("New task", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        TextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        TextField(
            value = note,
            onValueChange = onNoteChange,
            label = { Text("Notes") },
            modifier = Modifier.fillMaxWidth(),
        )
        CategorySelector(selected = category, onCategoryChange = onCategoryChange)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            FilledTonalButton(
                modifier = Modifier.weight(1f),
                onClick = onAddTask,
            ) {
                Text("Add task")
            }
            TextButton(
                modifier = Modifier.weight(1f),
                onClick = onCancel,
            ) {
                Text("Cancel")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun CategorySelector(
    selected: TaskCategory,
    onCategoryChange: (TaskCategory) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("Category", style = MaterialTheme.typography.bodyMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            TaskCategory.entries.forEach { category ->
                val isSelected = category == selected
                val colors =
                    if (isSelected) {
                        ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                        )
                    } else {
                        ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                FilledTonalButton(
                    colors = colors,
                    onClick = { onCategoryChange(category) },
                ) {
                    Text(category.displayName)
                }
            }
        }
    }
}
