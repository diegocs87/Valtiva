package com.example.valtiva.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.valtiva.data.local.entity.ConversionHistoryEntity
import kotlinx.coroutines.flow.Flow

/** The most conversions kept locally. Enforced here, not by any caller. */
private const val MAX_HISTORY_SIZE = 10

@Dao
interface ConversionHistoryDao {

  /** Emits the stored conversions, most recent first, and re-emits on every change. */
  @Query("SELECT * FROM conversion_history ORDER BY timestamp DESC, id DESC LIMIT $MAX_HISTORY_SIZE")
  fun observeHistory(): Flow<List<ConversionHistoryEntity>>

  @Insert
  suspend fun insert(entity: ConversionHistoryEntity)

  @Query(
    """
    DELETE FROM conversion_history
    WHERE id NOT IN (
      SELECT id FROM conversion_history ORDER BY timestamp DESC, id DESC LIMIT $MAX_HISTORY_SIZE
    )
    """,
  )
  suspend fun trimToMostRecent()

  /** Stores [entity] and drops anything past the [MAX_HISTORY_SIZE] most recent conversions. */
  @Transaction
  suspend fun insertAndTrim(entity: ConversionHistoryEntity) {
    insert(entity)
    trimToMostRecent()
  }
}
