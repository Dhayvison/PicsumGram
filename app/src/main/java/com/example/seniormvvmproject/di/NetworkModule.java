package com.example.seniormvvmproject.di;

import com.example.seniormvvmproject.data.remote.PostApi;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import javax.inject.Singleton;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {
private static final String BASE_URL = "https://jsonplaceholder.typicode.com/";

@Provides
@Singleton
public GsonConverterFactory provideGsonConverterFactory() {
return GsonConverterFactory.create();
}

@Provides
@Singleton
public Retrofit provideRetrofit(
GsonConverterFactory converterFactory
) {
return new Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(converterFactory)
    .build();
}

@Provides
@Singleton
public PostApi providePostApi(Retrofit retrofit) {
return retrofit.create(PostApi.class);
}
}