package com.edwin.cftcurrencytask.presentation.ui.currencyConvert

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.edwin.cftcurrencytask.R
import com.edwin.cftcurrencytask.databinding.CurrencyConvertFragmentBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class CurrencyConvertFragment : Fragment(R.layout.currency_convert_fragment) {

    private val viewModel: CurrencyConvertViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = CurrencyConvertFragmentBinding.bind(view)

        binding.apply {
            convertAmountEditText.setText(viewModel.currencyAmountConvert)
            currencyToCurrencyText.text = "RUB -> ${viewModel.currency?.charCode}"
            exchangeRateText.text = "${viewModel.currency?.value} : ${viewModel.currency?.nominal}"
            convertedCurrencyText.text = viewModel.convertedCurrency

            convertAmountEditText.addTextChangedListener {
                viewModel.currencyAmountConvert = it.toString()
            }

            convertButton.setOnClickListener {
                viewModel.onConvertClick()
                convertedCurrencyText.text = viewModel.convertedCurrency
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.currenciesConvertEvent.collect { event ->
                when (event) {
                    is CurrencyConvertViewModel.CurrencyConvertEvents.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_LONG).show()
                        binding.convertedCurrencyText.text = ""
                    }
                }
            }
        }
    }
}