package com.makelick.anytime.di

import com.makelick.anytime.model.AccountRepository
import com.makelick.anytime.model.FirestoreRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideFirestoreRepository(accountRepository: AccountRepository): FirestoreRepository {
        return FirestoreRepository(accountRepository.getUser()?.uid.toString())
    }
}