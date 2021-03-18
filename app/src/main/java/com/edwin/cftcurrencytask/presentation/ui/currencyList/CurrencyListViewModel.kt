package com.edwin.cftcurrencytask.presentation.ui.currencyList

import androidx.lifecycle.*
import com.edwin.cftcurrencytask.data.database.dao.CurrencyDao
import com.edwin.cftcurrencytask.data.domain.model.Currency
import com.edwin.cftcurrencytask.data.repository.CurrencyRepository
import com.edwin.cftcurrencytask.data.util.ConnectionCheck
import com.edwin.cftcurrencytask.data.util.PreferencesManager
import com.edwin.cftcurrencytask.data.util.SortOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
@ExperimentalCoroutinesApi
class CurrencyListViewModel @Inject constructor(
    private val connectionCheck: ConnectionCheck,
    private val currencyRepository: CurrencyRepository,
    private val currencyDao: CurrencyDao,
    private val preferencesManager: PreferencesManager,
    state: SavedStateHandle
) : ViewModel() {

    val searchQuery = state.getLiveData("searchQuery", "")
    private val preferencesFlow = preferencesManager.readFromDataStore
    private val currenciesEventChannel = Channel<CurrencyListEvents>()
    val currenciesEvent = currenciesEventChannel.receiveAsFlow()
    var currencies = liveData<List<Currency>> { }
    var isConnected = connectionCheck.hasInternetConnection()

    private fun getCurrenciesFromServerAsFlow() = combine(
        searchQuery.asFlow(),
        preferencesFlow
    ) { query, sortOrder ->
        Pair(query, sortOrder)
    }.flatMapLatest { (query, sortOrder) ->
        flowOf(currencyRepository.getCurrencies(query, sortOrder))
    }

    private fun getCurrenciesFromDbAsFlow() = combine(
        searchQuery.asFlow(),
        preferencesFlow
    ) { query, sortOrder ->
        Pair(query, sortOrder)
    }.flatMapLatest { (query, sortOrder) ->
        currencyDao.getCurrencies(query, sortOrder)
    }

    fun refreshData() {
        isConnected = connectionCheck.hasInternetConnection()
        currencies = if (isConnected) {
            getCurrenciesFromServerAsFlow().asLiveData()
        } else {
            getCurrenciesFromDbAsFlow().asLiveData()
        }
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