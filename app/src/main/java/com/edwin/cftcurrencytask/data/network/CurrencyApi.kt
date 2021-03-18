package com.edwin.cftcurrencytask.data.network

import com.edwin.cftcurrencytask.data.network.response.CurrencyResponse
import retrofit2.http.GET

interface CurrencyApi {

    @GET("daily_json.js")
    suspend fun getCurrencies(): CurrencyResponse
}