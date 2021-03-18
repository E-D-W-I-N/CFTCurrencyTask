package com.edwin.cftcurrencytask.data.network.response

import com.edwin.cftcurrencytask.data.network.model.CurrencyDto
import com.google.gson.annotations.SerializedName

data class CurrencyResponse(
    @SerializedName("Date")
    val date: String,
    @SerializedName("PreviousDate")
    val previousDate: String,
    @SerializedName("PreviousURL")
    val previousURL: String,
    @SerializedName("Timestamp")
    val timestamp: String,
    @SerializedName("Valute")
    val valute: Map<String, CurrencyDto>
)