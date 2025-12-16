package com.example.picsumgram.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.picsumgram.data.repository.PostRepository
import com.example.picsumgram.presentation.uistate.PostListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Collections
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository
) : ViewModel() {

    private val _state = MutableLiveData<PostListState>()
    val state: LiveData<PostListState> = _state

    init {
        loadPosts()
    }

    fun loadPosts() {
        viewModelScope.launch {
            _state.value = PostListState.Loading

            try {
                val postData = repository.getPosts()
                Collections.shuffle(postData)

                _state.value = PostListState.Success(postData)
            } catch (e: Exception) {
                _state.value = PostListState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }
}