package com.example.valtiva.presentation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Replaces the main dispatcher for the duration of a test.
 *
 * `viewModelScope` posts to `Dispatchers.Main`, which does not exist in a plain JVM test, so
 * without this rule every ViewModel test would fail on construction. [UnconfinedTestDispatcher] is
 * the default because these tests are about state transitions, not about scheduling: coroutines
 * then run eagerly and an assertion can follow an event directly. Tests that need to observe an
 * operation *while* it runs suspend the use case itself instead of manipulating the dispatcher.
 */
class MainDispatcherRule(
  private val dispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher() {

  override fun starting(description: Description) {
    Dispatchers.setMain(dispatcher)
  }

  override fun finished(description: Description) {
    Dispatchers.resetMain()
  }
}
