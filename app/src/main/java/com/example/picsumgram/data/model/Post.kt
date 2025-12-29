package com.example.picsumgram.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "posts",
    indices = [Index(value = ["userId"])]
)
data class Post(
    val userId: Int,

    @PrimaryKey
    val id: Int,

    val title: String,
    val body: String
)