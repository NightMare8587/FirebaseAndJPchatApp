package com.example.firebasechatappjetpackcompose.module

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun providesSharedPrefContext(applicationContext: Application) : SharedPreferences{
        return applicationContext.applicationContext.getSharedPreferences("UserLoginCreds",Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun providesFirebaseAuthInstance() : FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun providesFirebaseFirestoreInstance() : FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun providesFirebaseStorageInstance() : FirebaseStorage {
        return FirebaseStorage.getInstance()
    }
}