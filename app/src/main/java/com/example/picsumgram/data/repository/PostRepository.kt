package com.example.picsumgram.data.repository

import com.example.picsumgram.data.model.Post
import com.example.picsumgram.data.remote.PostApi
import javax.inject.Inject

class PostRepository @Inject constructor(
    private val postApi: PostApi
) {
    suspend fun getPosts(): List<Post> {
        return postApi.getPosts()
    }
}