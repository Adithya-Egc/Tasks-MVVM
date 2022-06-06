package com.adithyaegc.tasksmvvm.ui.addedittask

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.adithyaegc.tasksmvvm.data.Task
import com.adithyaegc.tasksmvvm.data.TaskDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddEditTaskViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val state: SavedStateHandle
) : ViewModel() {

    /**
     * by using SavedStateHandle we can get the arguments data directly
     * by using .get method inside it we need to add the same key value
     * where we used inside argument name
     */
    val task = state.get<Task>("task")

    var taskName = state.get<String>("taskName") ?: task?.title ?: ""
        set(value) {
            field = value
            state.set("taskName", value)
        }


    var isImportant = state.get<Boolean>("taskImportant") ?: task?.isImportant ?: false
        set(value) {
            field = value
            state.set("taskImportant", value)
        }
}

