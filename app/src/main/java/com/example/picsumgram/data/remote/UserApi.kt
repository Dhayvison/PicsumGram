package com.example.picsumgram.data.remote

import com.example.picsumgram.data.model.User
import retrofit2.http.GET
import retrofit2.http.Path

interface UserApi {
    @GET("users/{id}")
    suspend fun getUserById(@Path("id") userId: Int): User
}
