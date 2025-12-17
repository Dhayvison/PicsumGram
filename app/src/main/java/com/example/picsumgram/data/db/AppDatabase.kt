// AppDatabase.kt
package com.example.picsumgram.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.picsumgram.data.model.Post

@Database(entities = [Post::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
}