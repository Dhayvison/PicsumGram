package com.example.picsumgram.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.picsumgram.data.model.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<User>)

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Int): User?

    @Query("SELECT * FROM users WHERE id IN (:userIds)")
    suspend fun getUsersByIds(userIds: List<Int>): List<User>

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>
}