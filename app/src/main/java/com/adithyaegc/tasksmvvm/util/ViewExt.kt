package com.adithyaegc.tasksmvvm.util

import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.ItemTouchHelper

inline fun SearchView.onQueryTextChanged(crossinline listener: (String) -> Unit) {


    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            listener(newText.orEmpty())
            return true
        }
    })

}

