package com.edwin.cftcurrencytask.data.repository

import com.edwin.cftcurrencytask.data.database.dao.CurrencyDao
import com.edwin.cftcurrencytask.data.domain.model.Currency
import com.edwin.cftcurrencytask.data.network.CurrencyApi
import com.edwin.cftcurrencytask.data.network.model.CurrencyDtoMapper
import com.edwin.cftcurrencytask.data.util.SortOrder

class CurrencyRepositoryImpl(
    private val currencyApi: CurrencyApi,
    private val currencyDao: CurrencyDao,
    private val mapper: CurrencyDtoMapper
) : CurrencyRepository {

    override suspend fun getCurrencies(query: String, sortOrder: SortOrder): List<Currency> {
        val currencies = when (sortOrder) {
            SortOrder.BY_CHAR_CODE -> getCurrenciesSortedByCharCode(query)
            SortOrder.BY_NAME -> getCurrenciesSortedByName(query)
        }
        currencyDao.deleteAll()
        currencyDao.insertAll(currencies)
        return currencies
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