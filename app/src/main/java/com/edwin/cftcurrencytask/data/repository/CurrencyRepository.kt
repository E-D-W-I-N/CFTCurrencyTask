package com.edwin.cftcurrencytask.data.repository

import com.edwin.cftcurrencytask.data.domain.model.Currency
import com.edwin.cftcurrencytask.data.util.SortOrder
import kotlinx.coroutines.flow.Flow

interface CurrencyRepository {

    suspend fun getCurrencies(query: String, sortOrder: SortOrder): Flow<List<Currency>>

    suspend fun getCurrenciesSortedByCharCode(searchQuery: String): List<Currency>

    suspend fun getCurrenciesSortedByName(searchQuery: String): List<Currency>
}