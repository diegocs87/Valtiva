package com.example.valtiva.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.valtiva.data.local.entity.CurrencyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDao {

  /** Emits the catalogue, alphabetically by code, and re-emits when it is refreshed. */
  @Query("SELECT * FROM currencies ORDER BY code")
  fun observeCurrencies(): Flow<List<CurrencyEntity>>

  @Query("SELECT * FROM currencies ORDER BY code")
  suspend fun getAll(): List<CurrencyEntity>

  @Query("SELECT * FROM currencies WHERE code = :code")
  suspend fun getByCode(code: String): CurrencyEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(currency: CurrencyEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(currencies: List<CurrencyEntity>)
}
