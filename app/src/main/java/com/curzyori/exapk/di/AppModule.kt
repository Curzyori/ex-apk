package com.curzyori.exapk.di

import android.content.Context
import com.curzyori.exapk.data.repository.AppRepository
import com.curzyori.exapk.data.repository.PreferencesRepository
import com.curzyori.exapk.data.source.PackageManagerSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun providePackageManagerSource(
        @ApplicationContext context: Context
    ): PackageManagerSource {
        return PackageManagerSource(context)
    }
}
