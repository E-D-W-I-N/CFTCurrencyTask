package com.edwin.cftcurrencytask.presentation.ui.currencyConvert

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edwin.cftcurrencytask.data.database.dao.CurrencyDao
import com.edwin.cftcurrencytask.data.domain.model.Currency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrencyConvertViewModel @Inject constructor(
        private val currencyDao: CurrencyDao,
        private val state: SavedStateHandle
) : ViewModel() {

    val currency = state.get<Currency>("Currency")

    var currencyAmountConvert = state.get<String>("currencyAmountConvert") ?: ""
        set(value) {
            field = value
            state.set("currencyAmountConvert", value)
        }

    var convertedCurrency: String = ""

    private val currencyConvertEventChannel = Channel<CurrencyConvertEvents>()
    val currenciesConvertEvent = currencyConvertEventChannel.receiveAsFlow()

    fun onConvertClick() {
        when {
            currencyAmountConvert.isBlank() -> {
                showInvalidInputMessage("Поле не может быть пустым")
                convertedCurrency = ""
                return
            }

            currencyAmountConvert.startsWith(".") -> {
                showInvalidInputMessage("Поле не может начинаться с точки")
                convertedCurrency = ""
                return
            }

            else -> convertCurrency()
        }
    }

    private fun convertCurrency() {
        val nominal = currency?.nominal
        val value = currency?.value
        val rate = nominal?.div(value!!)
        convertedCurrency = String.format("%.4f", (rate?.times(currencyAmountConvert.toDouble())))
    }

    private fun showInvalidInputMessage(text: String) = viewModelScope.launch {
        currencyConvertEventChannel.send(CurrencyConvertEvents.ShowInvalidInputMessage(text))
    }

    sealed class CurrencyConvertEvents {
        data class ShowInvalidInputMessage(val message: String) : CurrencyConvertEvents()
    }
}