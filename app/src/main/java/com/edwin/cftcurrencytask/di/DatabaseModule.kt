package com.edwin.cftcurrencytask.di

import android.content.Context
import androidx.room.Room
import com.edwin.cftcurrencytask.data.database.CurrencyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(context, CurrencyDatabase::class.java, "currency_database")
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun provideCurrencyDao(database: CurrencyDatabase) = database.currencyDao()
}