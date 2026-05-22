package com.example.parkify.di

import com.example.parkify.data.repository.AdminRepositoryImpl
import com.example.parkify.data.repository.AuthRepositoryImpl
import com.example.parkify.data.repository.ParqueoRepositoryImpl
import com.example.parkify.domain.repository.IAdminRepository
import com.example.parkify.domain.repository.IAuthRepository
import com.example.parkify.domain.repository.IParqueoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindIAdminRepository(
        impl: AdminRepositoryImpl
    ): IAdminRepository

    @Binds
    @Singleton
    abstract fun bindIAuthRepository(
        impl: AuthRepositoryImpl
    ): IAuthRepository

    @Binds
    @Singleton
    abstract fun bindIParqueoRepository(
        impl: ParqueoRepositoryImpl
    ): IParqueoRepository
}