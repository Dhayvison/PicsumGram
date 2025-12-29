package com.example.picsumgram.data.repository

import com.example.picsumgram.data.db.PostDao
import com.example.picsumgram.data.db.UserDao
import com.example.picsumgram.data.model.Post
import com.example.picsumgram.data.model.PostWithUser
import com.example.picsumgram.data.remote.PostApi
import com.example.picsumgram.data.remote.UserApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class PostRepository @Inject constructor(
    private val postApi: PostApi,
    private val postDao: PostDao,
    private val userApi: UserApi,
    private val userDao: UserDao
) {
    suspend fun getPosts(): List<Post> {
        return try {
            val remotePosts = postApi.getPosts()

            postDao.insertAll(remotePosts)

            remotePosts
        } catch (e: Exception) {
            val localPosts = postDao.getPosts()

            localPosts.ifEmpty {
                throw e
            }
        }
    }

    suspend fun getPostsWithUsers(): List<PostWithUser> {
        return try {
            val remotePosts = postApi.getPosts()

            postDao.insertAll(remotePosts)

            val requiredUserIds = remotePosts.map { it.userId }.distinct()

            val localUsers = userDao.getUsersByIds(requiredUserIds)
            val localUserIds = localUsers.map { it.id }

            val missingUserIds = requiredUserIds.filter { it !in localUserIds }

            if (missingUserIds.isNotEmpty()) {
                coroutineScope {
                    val deferredUsers = missingUserIds.map { id ->
                        async { userApi.getUserById(id) }
                    }
                    
                    val newUsers = deferredUsers.awaitAll()

                    userDao.insertAll(newUsers)
                }
            }

            postDao.getPostsWithUsers()
        } catch (e: Exception) {
            postDao.getPostsWithUsers()
        }
    }
}