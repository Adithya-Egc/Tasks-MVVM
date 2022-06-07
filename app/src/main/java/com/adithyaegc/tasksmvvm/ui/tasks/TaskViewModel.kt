package com.adithyaegc.tasksmvvm.ui.tasks

import androidx.lifecycle.*
import com.adithyaegc.tasksmvvm.data.PreferenceManager
import com.adithyaegc.tasksmvvm.data.SortOrder
import com.adithyaegc.tasksmvvm.data.Task
import com.adithyaegc.tasksmvvm.data.TaskDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val preferenceManager: PreferenceManager,
    state: SavedStateHandle

) : ViewModel() {

    /**
     * single even that can consume and over -> channels
     * using SavedStateHandle for storing the search data
     * before that we use MutableStateFlow for it
     * and if we use SavedStateHandle with livedata we don't need to set its set value it will done automatically
     */
    val searchQuery = state.getLiveData("searchQuery", "")
    val preferencesFlow = preferenceManager.preferenceFlow

    private val taskEventChannel = Channel<TaskEvent>()
    val taskEvent = taskEventChannel.receiveAsFlow()

    private val taskFlow = combine(searchQuery.asFlow(), preferencesFlow)
    { query, filterPreference ->
        Pair(query, filterPreference)

    }.flatMapLatest { (query, filterPreference) ->
        taskDao.getTasks(query, filterPreference.sortOrder, filterPreference.hideCompleted)
    }

    val task = taskFlow.asLiveData()

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferenceManager.updateSortOrder(sortOrder)
    }

    fun onHideCompletedClick(hideCompleted: Boolean) = viewModelScope.launch {
        preferenceManager.updateHideCompleted(hideCompleted)
    }


    fun onItemClick(task: Task) = viewModelScope.launch {
        taskEventChannel.send(TaskEvent.NavigateToEditTask(task))

    }

    fun onCheckBoxClicked(task: Task, isCompleted: Boolean) = viewModelScope.launch {
        taskDao.update(task.copy(isCompleted = isCompleted))
    }

    fun onTaskSwipe(task: Task) = viewModelScope.launch {
        taskDao.delete(task)
        taskEventChannel.send(TaskEvent.ShowUndoDeleteTaskMessage(task))
    }

    fun onUndoDeletedTask(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
    }

    fun onAddNewTaskClick() = viewModelScope.launch {
        taskEventChannel.send(TaskEvent.NavigateToAddTask)

    }


    sealed class TaskEvent {
        object NavigateToAddTask : TaskEvent()
        data class NavigateToEditTask(val task: Task) : TaskEvent()
        data class ShowUndoDeleteTaskMessage(val task: Task) : TaskEvent()
    }
}



