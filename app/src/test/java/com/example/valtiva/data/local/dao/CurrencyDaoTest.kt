package com.example.valtiva.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.valtiva.data.local.ValtivaDatabase
import com.example.valtiva.data.local.entity.CurrencyEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CurrencyDaoTest {

  private lateinit var database: ValtivaDatabase
  private lateinit var dao: CurrencyDao

  private val usd = CurrencyEntity("USD", "United States Dollar", "\$")
  private val cop = CurrencyEntity("COP", "Colombian Peso", "\$")

  @Before
  fun setUp() {
    database = Room.inMemoryDatabaseBuilder(
      ApplicationProvider.getApplicationContext(),
      ValtivaDatabase::class.java,
    ).build()
    dao = database.currencyDao()
  }

  @After
  fun tearDown() {
    database.close()
  }

  @Test
  fun `inserts a single currency and reads it by code`() = runTest {
    dao.insert(usd)

    assertEquals(usd, dao.getByCode("USD"))
  }

  @Test
  fun `returns null for an unknown code`() = runTest {
    assertNull(dao.getByCode("JPY"))
  }

  @Test
  fun `inserts many currencies and returns them ordered by code`() = runTest {
    dao.insertAll(listOf(usd, cop))

    assertEquals(listOf(cop, usd), dao.getAll())
  }

  @Test
  fun `re-inserting a code updates the stored name and symbol`() = runTest {
    dao.insert(usd)
    dao.insert(CurrencyEntity("USD", "US Dollar", "US\$"))

    assertEquals(1, dao.getAll().size)
    assertEquals("US Dollar", dao.getByCode("USD")?.name)
  }

  @Test
  fun `stores a currency without a symbol`() = runTest {
    dao.insert(CurrencyEntity("XDR", "Special Drawing Rights", null))

    assertNull(dao.getByCode("XDR")?.symbol)
  }
}
