// AppDatabase.kt
package com.example.picsumgram.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.picsumgram.data.model.Post
import com.example.picsumgram.data.model.User

@Database(entities = [Post::class, User::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun userDao(): UserDao
}