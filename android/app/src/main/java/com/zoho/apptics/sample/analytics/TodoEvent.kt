package com.zoho.apptics.sample.analytics

sealed interface TodoEvent {
    val eventName: String
    val groupName: String
    val properties: HashMap<String, String>?

    class Add: TodoEvent {
        override val eventName = "add_fab_clicked"
        override val groupName = "todos"
        override val properties: HashMap<String, String>?
            get() = null

    }

    class Added : TodoEvent {
        override val eventName = "task_added"
        override val groupName = "todos"
        override val properties: HashMap<String, String>?
            get() = null
    }

    data class Completed(val taskId: String, val completed: Boolean) : TodoEvent {
        override val eventName: String =
            if (completed) "task_completed" else "task_unchecked"
        override val groupName: String = "todos"
        override val properties =
            hashMapOf("task_id" to taskId)
    }

    data class Deleted(val taskId: String) : TodoEvent {
        override val eventName = "task_deleted"
        override val groupName = "todos"
        override val properties =
            hashMapOf("task_id" to taskId)
    }
}
