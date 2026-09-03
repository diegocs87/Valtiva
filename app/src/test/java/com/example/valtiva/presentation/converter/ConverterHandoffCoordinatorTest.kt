package com.example.valtiva.presentation.converter

import app.cash.turbine.test
import com.example.valtiva.domain.COP
import com.example.valtiva.domain.USD
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ConverterHandoffCoordinatorTest {

  private val coordinator = ConverterHandoffCoordinator()
  private val publisher: ConverterHandoffPublisher = coordinator
  private val receiver: ConverterHandoffReceiver = coordinator

  @Test
  fun `a receiver observes exactly what a publisher requests`() = runTest {
    val pending = PendingConversion("100", USD, COP)

    receiver.pendingConversion.test {
      publisher.requestConversion(pending)

      assertEquals(pending, awaitItem())
      cancelAndIgnoreRemainingEvents()
    }
  }

  @Test
  fun `a subscriber that connects after a request does not replay it`() = runTest {
    // Deliberate: a hand-off is a one-time event, not state. Replaying a stale one to a screen
    // that only starts observing later (say, a converter recreated after process death) would
    // silently overwrite whatever the user has since typed.
    publisher.requestConversion(PendingConversion("50", COP, USD))

    val nextPending = PendingConversion("75", USD, COP)
    receiver.pendingConversion.test {
      publisher.requestConversion(nextPending)

      assertEquals(nextPending, awaitItem())
      cancelAndIgnoreRemainingEvents()
    }
  }
}
