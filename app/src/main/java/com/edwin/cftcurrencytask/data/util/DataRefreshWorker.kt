package com.edwin.cftcurrencytask.data.util

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.edwin.cftcurrencytask.data.repository.CurrencyRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DataRefreshWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: CurrencyRepository
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        return try {
            val currencies = repository.getCurrenciesSortedByCharCode("")
            Log.d("Worker", currencies.size.toString())
            Result.success()
        } catch (throwable: Throwable) {
            Log.d("Worker", throwable.message.toString())
            Result.failure()
        }
    }
}