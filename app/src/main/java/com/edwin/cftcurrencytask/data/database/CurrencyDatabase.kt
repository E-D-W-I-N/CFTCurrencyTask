package com.edwin.cftcurrencytask.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.edwin.cftcurrencytask.data.database.dao.CurrencyDao
import com.edwin.cftcurrencytask.data.domain.model.Currency

@Database(entities = [Currency::class], version = 1)
abstract class CurrencyDatabase : RoomDatabase() {
    abstract fun currencyDao(): CurrencyDao
}