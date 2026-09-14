package com.example.valtiva.data.local.source

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import app.cash.turbine.test
import java.io.File
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class DataStoreRatesRefreshSettingsDataSourceTest {

  @get:Rule
  val temporaryFolder = TemporaryFolder()

  private lateinit var dataStore: DataStore<Preferences>
  private lateinit var dataSource: DataStoreRatesRefreshSettingsDataSource

  @Before
  fun setUp() {
    dataStore = PreferenceDataStoreFactory.create(
      produceFile = { File(temporaryFolder.newFolder(), "rates_refresh_settings.preferences_pb") },
    )
    dataSource = DataStoreRatesRefreshSettingsDataSource(dataStore)
  }

  @Test
  fun `defaults to enabled when nothing was ever stored`() = runTest {
    dataSource.getDailyAutoRefreshEnabled().test {
      assertEquals(true, awaitItem())
      cancelAndIgnoreRemainingEvents()
    }
  }

  @Test
  fun `re-emits the value it was just told to store`() = runTest {
    dataSource.setDailyAutoRefreshEnabled(false)

    dataSource.getDailyAutoRefreshEnabled().test {
      assertEquals(false, awaitItem())
      cancelAndIgnoreRemainingEvents()
    }
  }

  @Test
  fun `storing true after false is reflected on the next read`() = runTest {
    dataSource.setDailyAutoRefreshEnabled(false)
    dataSource.setDailyAutoRefreshEnabled(true)

    dataSource.getDailyAutoRefreshEnabled().test {
      assertEquals(true, awaitItem())
      cancelAndIgnoreRemainingEvents()
    }
  }
}
