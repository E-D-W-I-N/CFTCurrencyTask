package com.edwin.cftcurrencytask.data.domain.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "currency")
data class Currency(
    @PrimaryKey val id: String,
    var charCode: String,
    val name: String,
    val nominal: Int,
    val numCode: String,
    val value: Double,
    val previous: Double
) : Parcelable