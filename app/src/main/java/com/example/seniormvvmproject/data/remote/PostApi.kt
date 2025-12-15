package com.example.seniormvvmproject.data.remote

import retrofit2.http.GET
import com.example.seniormvvmproject.data.model.Post

interface PostApi {
    @GET("posts")
    suspend fun getPosts(): List<Post>
}

//package com.example.seniormvvmproject.data.remote;
//
//import java.util.List;
//import com.example.seniormvvmproject.data.model.Post;
//import retrofit2.Call;
//import retrofit2.http.GET;
//
//public interface PostApi {
//
//    @GET("posts")
//    Call<List<Post>> getPosts();
//}