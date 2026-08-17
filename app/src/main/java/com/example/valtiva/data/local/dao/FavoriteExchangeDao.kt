package com.example.valtiva.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.valtiva.data.local.entity.FavoriteExchangeEntity
import kotlinx.coroutines.flow.Flow

/**
 * Local access to the user's starred pairs.
 *
 * Inserts use [OnConflictStrategy.IGNORE] rather than REPLACE: starring an already stored pair has
 * to be a no-op, and REPLACE would delete and reinsert the row, needlessly waking every observer of
 * [observeFavorites].
 */
@Dao
interface FavoriteExchangeDao {

  /** Emits the stored favorites and re-emits on every change. */
  @Query("SELECT * FROM favorite_exchanges ORDER BY base_currency, quote_currency")
  fun observeFavorites(): Flow<List<FavoriteExchangeEntity>>

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun insert(favorite: FavoriteExchangeEntity)

  /** Removing a pair that is not stored is a no-op. */
  @Query(
    "DELETE FROM favorite_exchanges WHERE base_currency = :base AND quote_currency = :quote",
  )
  suspend fun delete(base: String, quote: String)

  /** Whether [base]/[quote] is starred, for toggling the star without collecting the whole list. */
  @Query(
    """
    SELECT EXISTS(
      SELECT 1 FROM favorite_exchanges
      WHERE base_currency = :base AND quote_currency = :quote
    )
    """,
  )
  suspend fun exists(base: String, quote: String): Boolean
}
