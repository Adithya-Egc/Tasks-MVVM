package com.adithyaegc.tasksmvvm.ui.addedittask

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.adithyaegc.tasksmvvm.R
import com.adithyaegc.tasksmvvm.databinding.FragmentAddEditTaskBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditTaskFragment : Fragment(R.layout.fragment_add_edit_task) {

    private val viewModel: AddEditTaskViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentAddEditTaskBinding.bind(view)

        binding.apply {
            etEditAdd.setText(viewModel.taskName)
            cbImportant.isChecked = viewModel.isImportant
            cbImportant.jumpDrawablesToCurrentState()
            tvDate.isVisible = viewModel.task != null
            tvDate.text = "Created: ${viewModel.task?.createDateFormatted}"
        }

    }
}