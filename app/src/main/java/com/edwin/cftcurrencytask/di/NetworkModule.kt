package com.edwin.cftcurrencytask.di

import android.content.Context
import com.edwin.cftcurrencytask.data.network.CurrencyApi
import com.edwin.cftcurrencytask.data.network.model.CurrencyDtoMapper
import com.edwin.cftcurrencytask.data.util.ConnectionCheck
import com.edwin.cftcurrencytask.data.util.ConnectionCheckImpl
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideCurrencyMapper(): CurrencyDtoMapper {
        return CurrencyDtoMapper()
    }

    @Provides
    @Singleton
    fun provideCurrencyApi(): CurrencyApi {
        return Retrofit.Builder()
                .baseUrl("https://www.cbr-xml-daily.ru/")
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .build().create(CurrencyApi::class.java)
    }

    @Provides
    @Singleton
    fun provideConnection(@ApplicationContext context: Context): ConnectionCheck {
        return ConnectionCheckImpl(context)
    }
}