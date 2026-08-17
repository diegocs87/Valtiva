package com.example.valtiva.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.valtiva.data.local.entity.ExchangeRateEntity
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

/**
 * Local access to stored rates. This table is the source the app reads from; the network only
 * refreshes it.
 *
 * Writes use [OnConflictStrategy.REPLACE]: a refresh re-publishes the same
 * `(base, quote, date)` rows, and replacing them keeps the refresh idempotent and lets a corrected
 * rate for a day overwrite the previous value. That also removes any need for a separate update or
 * delete path for the daily refresh.
 */
@Dao
interface ExchangeRateDao {

  /**
   * Rates for [base] on the most recent day stored for it.
   *
   * The date is resolved per base rather than globally, because different bases can be refreshed at
   * different moments.
   */
  @Query(
    """
    SELECT * FROM exchange_rates
    WHERE base_currency = :base
      AND date = (SELECT MAX(date) FROM exchange_rates WHERE base_currency = :base)
    """,
  )
  suspend fun getLatestRates(base: String): List<ExchangeRateEntity>

  /** Same as [getLatestRates] but observed, so the UI re-renders when a refresh lands. */
  @Query(
    """
    SELECT * FROM exchange_rates
    WHERE base_currency = :base
      AND date = (SELECT MAX(date) FROM exchange_rates WHERE base_currency = :base)
    """,
  )
  fun observeLatestRates(base: String): Flow<List<ExchangeRateEntity>>

  /** Rates for [base] on an exact [date]. */
  @Query("SELECT * FROM exchange_rates WHERE base_currency = :base AND date = :date")
  suspend fun getRatesForDate(base: String, date: LocalDate): List<ExchangeRateEntity>

  /** A single pair on an exact [date], or `null` when that day was never stored. */
  @Query(
    """
    SELECT * FROM exchange_rates
    WHERE base_currency = :base AND quote_currency = :quote AND date = :date
    """,
  )
  suspend fun getRate(base: String, quote: String, date: LocalDate): ExchangeRateEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(rate: ExchangeRateEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(rates: List<ExchangeRateEntity>)
}
