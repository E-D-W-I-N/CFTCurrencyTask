package com.edwin.cftcurrencytask.data.network.model

import com.edwin.cftcurrencytask.data.domain.DomainMapper
import com.edwin.cftcurrencytask.data.domain.model.Currency

class CurrencyDtoMapper : DomainMapper<CurrencyDto, Currency> {

    override fun mapToDomainModel(model: CurrencyDto): Currency {
        return Currency(
            id = model.id,
            charCode = model.charCode,
            name = model.name,
            nominal = model.nominal,
            numCode = model.numCode,
            value = model.value,
            previous = model.previous
        )
    }

    override fun mapFromDomainModel(domainModel: Currency): CurrencyDto {
        return CurrencyDto(
            id = domainModel.id,
            charCode = domainModel.charCode,
            name = domainModel.name,
            nominal = domainModel.nominal,
            numCode = domainModel.numCode,
            value = domainModel.value,
            previous = domainModel.previous
        )
    }

    fun toDomainList(initial: List<CurrencyDto>): List<Currency> {
        return initial.map { mapToDomainModel(it) }
    }

    fun fromDomainList(initial: List<Currency>): List<CurrencyDto> {
        return initial.map { mapFromDomainModel(it) }
    }
}