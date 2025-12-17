package com.example.picsumgram.di

import android.content.Context
import androidx.room.Room
import com.example.picsumgram.data.db.AppDatabase
import com.example.picsumgram.data.db.PostDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Faz com que as instâncias vivam enquanto o app estiver vivo
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "picsumgram_db"
        )
            .fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    fun providePostDao(appDatabase: AppDatabase): PostDao {
        return appDatabase.postDao()
    }
}