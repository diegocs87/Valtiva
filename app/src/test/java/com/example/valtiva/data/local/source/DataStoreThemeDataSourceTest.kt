package com.example.valtiva.data.local.source

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import app.cash.turbine.test
import com.example.valtiva.domain.model.ThemeMode
import java.io.File
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class DataStoreThemeDataSourceTest {

  @get:Rule
  val temporaryFolder = TemporaryFolder()

  private lateinit var dataStore: DataStore<Preferences>
  private lateinit var dataSource: DataStoreThemeDataSource

  @Before
  fun setUp() {
    dataStore = PreferenceDataStoreFactory.create(
      produceFile = { File(temporaryFolder.newFolder(), "theme.preferences_pb") },
    )
    dataSource = DataStoreThemeDataSource(dataStore)
  }

  @Test
  fun `defaults to system when nothing was ever stored`() = runTest {
    dataSource.getThemeMode().test {
      assertEquals(ThemeMode.SYSTEM, awaitItem())
      cancelAndIgnoreRemainingEvents()
    }
  }

  @Test
  fun `re-emits the mode it was just told to store`() = runTest {
    dataSource.setThemeMode(ThemeMode.DARK)

    dataSource.getThemeMode().test {
      assertEquals(ThemeMode.DARK, awaitItem())
      cancelAndIgnoreRemainingEvents()
    }
  }

  @Test
  fun `falls back to system for a value that is not a known mode`() = runTest {
    dataStore.edit { preferences -> preferences[stringPreferencesKey("theme_mode")] = "NOT_A_MODE" }

    dataSource.getThemeMode().test {
      assertEquals(ThemeMode.SYSTEM, awaitItem())
      cancelAndIgnoreRemainingEvents()
    }
  }
}
