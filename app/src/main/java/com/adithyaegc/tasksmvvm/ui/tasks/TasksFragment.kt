package com.adithyaegc.tasksmvvm.ui.tasks

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adithyaegc.tasksmvvm.R
import com.adithyaegc.tasksmvvm.data.SortOrder
import com.adithyaegc.tasksmvvm.data.Task
import com.adithyaegc.tasksmvvm.databinding.FragmentTasksBinding
import com.adithyaegc.tasksmvvm.util.exhaustive
import com.adithyaegc.tasksmvvm.util.onQueryTextChanged
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TasksFragment : Fragment(R.layout.fragment_tasks), TaskAdapter.OnItemClickListener {

    private val taskViewModel: TaskViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentTasksBinding.bind(view)
        val taskAdapter = TaskAdapter(this)

        binding.apply {
            rView.apply {
                adapter = taskAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val task = taskAdapter.currentList[viewHolder.adapterPosition]
                    taskViewModel.onTaskSwipe(task)
                }
            }).attachToRecyclerView(binding.rView)

            fab.setOnClickListener {
                taskViewModel.onAddNewTaskClick()
            }
        }

        taskViewModel.task.observe(viewLifecycleOwner) {
            taskAdapter.submitList(it)
        }
        //launchWhenStarted will call onStart and destroy at onStop
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            taskViewModel.taskEvent.collect { event ->
                when (event) {
                    is TaskViewModel.TaskEvent.ShowUndoDeleteTaskMessage ->
                        Snackbar.make(requireView(), "Task deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO") {
                                taskViewModel.onUndoDeletedTask(event.task)
                            }.show()
                    is TaskViewModel.TaskEvent.NavigateToAddTask -> {
                        val action =
                            TasksFragmentDirections.actionTasksFragment2ToAddEditTaskFragment2("New Task")
                        findNavController().navigate(action)
                    }
                    is TaskViewModel.TaskEvent.NavigateToEditTask -> {
                        val action =
                            TasksFragmentDirections.actionTasksFragment2ToAddEditTaskFragment2(
                                "Edit Task",
                                event.task
                            )
                        findNavController().navigate(action)
                    }
                }.exhaustive
            }
        }

        setHasOptionsMenu(true)

    }

    override fun onItemClick(task: Task) {
        taskViewModel.onItemClick(task)
    }

    override fun onCheckBoxClicked(task: Task, isCompleted: Boolean) {
        taskViewModel.onCheckBoxClicked(task, isCompleted)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_tasks, menu)

        val search = menu.findItem(R.id.menu_search)
        val searchView = search.actionView as SearchView

        searchView.onQueryTextChanged {
            taskViewModel.searchQuery.value = it
        }

        // while launching the application we need to check tha hide completed value is checked or not
        viewLifecycleOwner.lifecycleScope.launch {
            menu.findItem(R.id.menu_hide_completed).isChecked =
                taskViewModel.preferencesFlow.first().hideCompleted
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_sort_name -> {
                taskViewModel.onSortOrderSelected(SortOrder.BY_NAME)
                true
            }
            R.id.menu_sort_date -> {
                taskViewModel.onSortOrderSelected(SortOrder.BY_DATE)
                true
            }
            R.id.menu_hide_completed -> {
                item.isChecked = !item.isChecked
                taskViewModel.onHideCompletedClick(item.isChecked)
                true
            }
            R.id.menu_delete_all -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}

