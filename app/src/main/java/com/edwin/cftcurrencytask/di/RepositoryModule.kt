package com.edwin.cftcurrencytask.di

import com.edwin.cftcurrencytask.data.database.dao.CurrencyDao
import com.edwin.cftcurrencytask.data.network.CurrencyApi
import com.edwin.cftcurrencytask.data.network.model.CurrencyDtoMapper
import com.edwin.cftcurrencytask.data.repository.CurrencyRepository
import com.edwin.cftcurrencytask.data.repository.CurrencyRepositoryImpl
import com.edwin.cftcurrencytask.data.util.ConnectionCheck
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideCurrencyRepository(
            connectionCheck: ConnectionCheck,
            currencyApi: CurrencyApi,
            currencyDao: CurrencyDao,
            currencyDtoMapper: CurrencyDtoMapper
    ): CurrencyRepository {
        return CurrencyRepositoryImpl(connectionCheck, currencyApi, currencyDao, currencyDtoMapper)
    }
}