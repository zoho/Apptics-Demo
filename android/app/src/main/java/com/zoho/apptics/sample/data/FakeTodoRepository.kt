package com.zoho.apptics.sample.data

import com.zoho.apptics.sample.model.Task
import com.zoho.apptics.sample.model.TaskCategory

/**
 * Hardcoded tasks and activity history so the showcase can emphasise Apptics wiring.
 */
object FakeTodoRepository {

    val tasks: List<Task> =
        listOf(
            Task(
                id = "task_inbox_zero",
                title = "Clear email inbox",
                note = "Reply to Mia + archive the rest.",
                category = TaskCategory.WORK,
                isDone = false,
                reminderMinutes = 30,
            ),
            Task(
                id = "task_sync_notes",
                title = "Plan tomorrow's stand-up",
                note = "Capture blockers in the shared doc.",
                category = TaskCategory.WORK,
                isDone = true,
                reminderMinutes = null,
            ),
            Task(
                id = "task_groceries",
                title = "Buy groceries",
                note = "Veggies, oat milk, granola.",
                category = TaskCategory.PERSONAL,
                isDone = false,
                reminderMinutes = 120,
            ),
            Task(
                id = "task_walk",
                title = "Evening walk",
                note = "15 minute loop around the park.",
                category = TaskCategory.WELLNESS,
                isDone = true,
                reminderMinutes = 60,
            ),
        )

}
