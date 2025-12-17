package com.example.picsumgram.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.picsumgram.data.model.Post

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(posts: List<Post>)

    @Query("SELECT * FROM posts LIMIT :limit OFFSET :offset")
    suspend fun getPostsByPage(limit: Int, offset: Int): List<Post>

    @Query("DELETE FROM posts")
    suspend fun deleteAll()
}