package com.edwin.cftcurrencytask.data.repository

import com.edwin.cftcurrencytask.data.domain.model.Currency
import com.edwin.cftcurrencytask.data.util.SortOrder

interface CurrencyRepository {

    suspend fun getCurrencies(query: String, sortOrder: SortOrder): List<Currency>

    suspend fun getCurrenciesSortedByCharCode(searchQuery: String): List<Currency>

    suspend fun getCurrenciesSortedByName(searchQuery: String): List<Currency>
}