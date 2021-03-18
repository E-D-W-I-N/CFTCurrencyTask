package com.edwin.cftcurrencytask.presentation.ui.currencyList

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.edwin.cftcurrencytask.R
import com.edwin.cftcurrencytask.data.domain.model.Currency
import com.edwin.cftcurrencytask.data.util.DataRefreshWorker
import com.edwin.cftcurrencytask.data.util.SortOrder
import com.edwin.cftcurrencytask.data.util.onQueryTextChanged
import com.edwin.cftcurrencytask.databinding.CurrencyListFragmentBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class CurrencyListFragment : Fragment(R.layout.currency_list_fragment),
    CurrencyAdapter.OnItemClickListener {

    private val viewModel: CurrencyListViewModel by viewModels()

    private lateinit var searchView: SearchView
    private lateinit var observer: Observer<List<Currency>>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = CurrencyListFragmentBinding.bind(view)

        val currencyAdapter = CurrencyAdapter(this)

        observer = Observer {
            binding.progressBar.visibility = View.VISIBLE
            currencyAdapter.submitList(it)
            binding.progressBar.visibility = View.GONE
            if (it.isNullOrEmpty() && !viewModel.isConnected && searchView.isIconified) {
                binding.progressBar.visibility = View.GONE
                Snackbar.make(
                    requireView(),
                    "Отсутствует подключение к интернету, а также данные в базе данных",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        binding.apply {
            recyclerViewCurrency.apply {
                adapter = currencyAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            swipeRefreshLayout.setOnRefreshListener {
                viewModel.refreshData()
                viewModel.currencies.observe(viewLifecycleOwner, observer)
                swipeRefreshLayout.isRefreshing = false
            }
        }

        setupWorker()

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.currenciesEvent.collect { event ->
                when (event) {
                    is CurrencyListViewModel.CurrencyListEvents.NavigateToCurrencyConvertScreen -> {
                        val action =
                            CurrencyListFragmentDirections.actionCurrencyListFragmentToCurrencyConvertFragment(
                                event.currency,
                                "RUB -> ${event.currency.charCode}"
                            )
                        findNavController().navigate(action)
                    }
                }
            }
        }

        setHasOptionsMenu(true)
    }

    private fun setupWorker() {
        val refreshCurrency = PeriodicWorkRequestBuilder<DataRefreshWorker>(1, TimeUnit.HOURS)
            .setInitialDelay(1, TimeUnit.HOURS)
            .build()

        val workManager = WorkManager.getInstance(requireContext())
        workManager.enqueueUniquePeriodicWork(
            "refreshCurrencies", ExistingPeriodicWorkPolicy.REPLACE, refreshCurrency
        )
        workManager.getWorkInfoByIdLiveData(refreshCurrency.id)
            .observe(viewLifecycleOwner, { workInfo ->
                if (workInfo != null && workInfo.state == WorkInfo.State.ENQUEUED) {
                    viewModel.refreshData()
                    viewModel.currencies.observe(viewLifecycleOwner, observer)
                }
            })
    }

    override fun onItemClick(currency: Currency) {
        viewModel.onCurrencySelected(currency)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_currency_list, menu)

        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView

        val pendingQuery = viewModel.searchQuery.value
        if (!pendingQuery.isNullOrEmpty()) {
            searchItem.expandActionView()
            searchView.setQuery(pendingQuery, false)
        }
        searchView.onQueryTextChanged {
            viewModel.searchQuery.value = it
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort_by_name -> {
                viewModel.onSortOrderSelected(SortOrder.BY_NAME)
                true
            }

            R.id.action_sort_by_char_code -> {
                viewModel.onSortOrderSelected(SortOrder.BY_CHAR_CODE)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchView.setOnQueryTextListener(null)
        viewModel.currencies.removeObserver(observer)
    }
}