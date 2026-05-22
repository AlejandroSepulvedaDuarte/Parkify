package com.example.parkify.di

import com.example.parkify.data.remote.firebase.FirebaseService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideFirebaseService(
        auth: FirebaseAuth,
        db: FirebaseFirestore
    ) = FirebaseService(auth, db)
}