package com.edwin.cftcurrencytask.presentation.ui.currencyList

import androidx.lifecycle.*
import com.edwin.cftcurrencytask.data.domain.model.Currency
import com.edwin.cftcurrencytask.data.repository.CurrencyRepository
import com.edwin.cftcurrencytask.data.util.PreferencesManager
import com.edwin.cftcurrencytask.data.util.SortOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
@ExperimentalCoroutinesApi
class CurrencyListViewModel @Inject constructor(
        private val currencyRepository: CurrencyRepository,
        private val preferencesManager: PreferencesManager,
        state: SavedStateHandle
) : ViewModel() {

    val searchQuery = state.getLiveData("searchQuery", "")
    private val preferencesFlow = preferencesManager.readFromDataStore
    private val currenciesEventChannel = Channel<CurrencyListEvents>()
    val currenciesEvent = currenciesEventChannel.receiveAsFlow()
    var currencies = liveData<List<Currency>> { }

    private fun getCurrencies() = combine(
            searchQuery.asFlow(),
            preferencesFlow
    ) { query, sortOrder ->
        Pair(query, sortOrder)
    }.flatMapLatest { (query, sortOrder) ->
        currencyRepository.getCurrencies(query, sortOrder)
    }

    fun refreshData() {
        currencies = getCurrencies().asLiveData()
    }

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }

    fun onCurrencySelected(currency: Currency) = viewModelScope.launch {
        currenciesEventChannel.send(CurrencyListEvents.NavigateToCurrencyConvertScreen(currency))
    }

    sealed class CurrencyListEvents {
        data class NavigateToCurrencyConvertScreen(val currency: Currency) : CurrencyListEvents()
    }
}