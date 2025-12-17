package com.example.picsumgram.data.repository

import com.example.picsumgram.data.db.PostDao
import com.example.picsumgram.data.model.Post
import com.example.picsumgram.data.remote.PostApi
import javax.inject.Inject

class PostRepository @Inject constructor(
    private val postApi: PostApi,
    private val postDao: PostDao
) {
    suspend fun getPosts(): List<Post> {
        return try {
            val remotePosts = postApi.getPosts()

            postDao.insertAll(remotePosts)

            remotePosts
        } catch (e: Exception) {
            val localPosts = postDao.getPostsByPage(100, 0)

            localPosts.ifEmpty {
                throw e
            }
        }
    }
}