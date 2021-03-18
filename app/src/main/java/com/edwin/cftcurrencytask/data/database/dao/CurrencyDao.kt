package com.edwin.cftcurrencytask.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.edwin.cftcurrencytask.data.domain.model.Currency
import com.edwin.cftcurrencytask.data.util.SortOrder
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDao {

    fun getCurrencies(query: String, sortOrder: SortOrder): Flow<List<Currency>> =
        when (sortOrder) {
            SortOrder.BY_CHAR_CODE -> getCurrenciesSortedByCharCode(query)
            SortOrder.BY_NAME -> getCurrenciesSortedByName(query)
        }

    @Query("SELECT * FROM currency WHERE name LIKE '%' || :searchQuery || '%' or charCode LIKE '%' || :searchQuery || '%' ORDER BY charCode")
    fun getCurrenciesSortedByCharCode(searchQuery: String): Flow<List<Currency>>

    @Query("SELECT * FROM currency WHERE name LIKE '%' || :searchQuery || '%' or charCode LIKE '%' || :searchQuery || '%' ORDER BY name")
    fun getCurrenciesSortedByName(searchQuery: String): Flow<List<Currency>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(currencies: List<Currency>)

    @Query("DELETE FROM currency")
    suspend fun deleteAll()
}