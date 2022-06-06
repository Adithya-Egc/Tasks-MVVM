package com.adithyaegc.tasksmvvm.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.adithyaegc.tasksmvvm.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider


@Database(entities = [Task::class], version = 1)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao


    class CallBack @Inject constructor(
        private val dataBase: Provider<TaskDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) :
        RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val dao = dataBase.get().taskDao()

            applicationScope.launch {
                dao.insert(
                    Task(
                        "We have a call at 12:00 pm",
                        isCompleted = true,
                    )
                )
                dao.insert(Task("Go to dinner this night"))
                dao.insert(Task("Need to do leg work out today", isCompleted = true))
                dao.insert(Task("Need to learn how to get more view on YT"))
                dao.insert(Task("Learn a new concept everyday", isImportant = true))
                dao.insert(Task("Go to dinner this night", isCompleted = true, isImportant = true))
                dao.insert(Task("Need to do leg work out today", isCompleted = true))
                dao.insert(Task("Need to learn how to get more view on YT"))
                dao.insert(Task("Learn a new concept everyday", isImportant = true))
            }
        }
    }


}
