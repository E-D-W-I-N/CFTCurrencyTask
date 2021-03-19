package com.edwin.cftcurrencytask.data.repository

import com.edwin.cftcurrencytask.data.database.dao.CurrencyDao
import com.edwin.cftcurrencytask.data.domain.model.Currency
import com.edwin.cftcurrencytask.data.network.CurrencyApi
import com.edwin.cftcurrencytask.data.network.model.CurrencyDtoMapper
import com.edwin.cftcurrencytask.data.util.ConnectionCheck
import com.edwin.cftcurrencytask.data.util.SortOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn

class CurrencyRepositoryImpl(
        private val connectionCheck: ConnectionCheck,
        private val currencyApi: CurrencyApi,
        private val currencyDao: CurrencyDao,
        private val mapper: CurrencyDtoMapper
) : CurrencyRepository {

    override suspend fun getCurrencies(query: String, sortOrder: SortOrder): Flow<List<Currency>> {
        val isConnected = connectionCheck.hasInternetConnection()
        return if (isConnected) {
            val currencies = when (sortOrder) {
                SortOrder.BY_CHAR_CODE -> getCurrenciesSortedByCharCode(query)
                SortOrder.BY_NAME -> getCurrenciesSortedByName(query)
            }
            currencyDao.deleteAll()
            currencyDao.insertAll(currencies)
            flowOf(currencies).flowOn(Dispatchers.IO)
        } else {
            currencyDao.getCurrencies(query, sortOrder)
        }
    }

    override suspend fun getCurrenciesSortedByCharCode(searchQuery: String): List<Currency> {
        return mapper.toDomainList(currencyApi.getCurrencies().valute.values.toList())
                .filter {
                    it.name.contains(searchQuery, ignoreCase = true) || it.charCode.contains(
                            searchQuery,
                            ignoreCase = true
                    )
                }
                .sortedBy { it.charCode }
    }

    override suspend fun getCurrenciesSortedByName(searchQuery: String): List<Currency> {
        return mapper.toDomainList(currencyApi.getCurrencies().valute.values.toList())
                .filter {
                    it.name.contains(searchQuery, ignoreCase = true) || it.charCode.contains(
                            searchQuery,
                            ignoreCase = true
                    )
                }
                .sortedBy { it.name }
    }

}