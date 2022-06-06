package com.adithyaegc.tasksmvvm.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    /**
     * flow can only run inside a coroutine
     * that's why we don't use suspend fun for getTask() function
     */

    // || -> means append operator inside SQLite

    fun getTasks(query: String, sortOrder: SortOrder, hideCompleted: Boolean): Flow<List<Task>> =
        when (sortOrder) {
            SortOrder.BY_DATE -> getTasksSortedByDateCreated(query, hideCompleted)
            SortOrder.BY_NAME -> getTasksSortedByName(query, hideCompleted)
        }


    @Query("SELECT * FROM task_table WHERE (isCompleted != :hideCompleted OR isCompleted == 0) AND title LIKE  '%'|| :searchQuery || '%' ORDER BY isImportant DESC, title")
    fun getTasksSortedByName(searchQuery: String, hideCompleted: Boolean): Flow<List<Task>>


    @Query("SELECT * FROM task_table WHERE (isCompleted != :hideCompleted OR isCompleted == 0) AND title LIKE  '%'|| :searchQuery || '%' ORDER BY isImportant DESC, createdDate")
    fun getTasksSortedByDateCreated(searchQuery: String, hideCompleted: Boolean): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)


}

