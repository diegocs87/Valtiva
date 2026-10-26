package com.example.valtiva.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currencies")
data class CurrencyEntity(
  @PrimaryKey
  @ColumnInfo(name = "code") val code: String,
  @ColumnInfo(name = "name") val name: String,
  @ColumnInfo(name = "symbol") val symbol: String?,
)
