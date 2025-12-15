package com.example.seniormvvmproject.data.repository

import com.example.seniormvvmproject.data.remote.PostApi
import com.example.seniormvvmproject.data.model.Post
import javax.inject.Inject

class PostRepository @Inject constructor(
    private val postApi: PostApi
) {
    suspend fun getPosts(): List<Post> {
        return postApi.getPosts()
    }
}