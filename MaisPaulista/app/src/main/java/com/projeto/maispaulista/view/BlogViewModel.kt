package com.projeto.maispaulista.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projeto.maispaulista.model.Blog
import com.projeto.maispaulista.repository.BlogRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BlogViewModel(private val repository: BlogRepository) : ViewModel() {

    private val _blogList = MutableStateFlow<List<Blog>>(emptyList())
    val blogList: StateFlow<List<Blog>> get() = _blogList

    init {
        fetchBlogs()
    }

    private fun fetchBlogs() {
        viewModelScope.launch {
            repository.getBlogs().collect { blogs ->
                _blogList.value = blogs
            }
        }
    }
}