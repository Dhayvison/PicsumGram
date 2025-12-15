package com.example.seniormvvmproject.presentation.uistate

import com.example.seniormvvmproject.data.model.Post

sealed interface PostListState {
    // 1. Estado de Carregamento (não precisa de dados extras)
    data object Loading : PostListState

    // 2. Estado de Sucesso (precisa carregar a lista de Posts)
    data class Success(val posts: List<Post>) : PostListState

    // 3. Estado de Erro (precisa carregar a mensagem)
    data class Error(val message: String) : PostListState
}