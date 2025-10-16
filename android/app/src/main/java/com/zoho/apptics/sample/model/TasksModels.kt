package com.zoho.apptics.sample.model

/**
 * Lightweight task models so the sample can behave like a todos app while keeping
 * the focus on Apptics instrumentation.
 */
data class Task(
    val id: String,
    val title: String,
    val note: String,
    val category: TaskCategory,
    val isDone: Boolean,
    val reminderMinutes: Int?,
)

enum class TaskCategory(val displayName: String) {
    PERSONAL("Personal"),
    WORK("Work"),
    WELLNESS("Wellness"),
}
