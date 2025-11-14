package com.scoretally.di

import com.scoretally.util.AndroidResourceProvider
import com.scoretally.util.FirebaseLogger
import com.scoretally.util.Logger
import com.scoretally.util.ResourceProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UtilModule {

    @Binds
    @Singleton
    abstract fun bindResourceProvider(
        androidResourceProvider: AndroidResourceProvider
    ): ResourceProvider

    @Binds
    @Singleton
    abstract fun bindLogger(
        firebaseLogger: FirebaseLogger
    ): Logger
}
