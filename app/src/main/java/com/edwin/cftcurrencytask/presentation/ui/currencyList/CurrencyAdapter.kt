package com.edwin.cftcurrencytask.presentation.ui.currencyList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.edwin.cftcurrencytask.R
import com.edwin.cftcurrencytask.data.domain.model.Currency
import com.edwin.cftcurrencytask.databinding.CurrencyListItemBinding

class CurrencyAdapter(private val listener: OnItemClickListener) : ListAdapter<Currency, CurrencyAdapter.CurrencyViewHolder>(
    DiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        val binding = CurrencyListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
        )
        return CurrencyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CurrencyViewHolder(private val binding: CurrencyListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    listener.onItemClick(getItem(position))
                }
            }
        }

        fun bind(currency: Currency) {
            binding.apply {
                currencyCharCode.text = currency.charCode
                currencyValueBefore.text = "${currency.previous} ₽."
                currencyValueNow.text = "${currency.value} ₽."
                currencyName.text = currency.name
                currencyNominal.text = "За ${currency.nominal}"
                if (currency.previous < currency.value)
                    arrowImg.setBackgroundResource(R.drawable.ic_arrow_up)
                else
                    arrowImg.setBackgroundResource(R.drawable.ic_arrow_down)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(currency: Currency)
    }

    class DiffCallback : DiffUtil.ItemCallback<Currency>() {
        override fun areItemsTheSame(oldItem: Currency, newItem: Currency) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Currency, newItem: Currency) = oldItem == newItem
    }
}