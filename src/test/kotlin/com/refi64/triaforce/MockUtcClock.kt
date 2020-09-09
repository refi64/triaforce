package com.refi64.triaforce

import com.refi64.triaforce.util.UtcClock
import io.quarkus.test.Mock
import java.time.LocalDateTime
import javax.inject.Singleton

@Mock
@Singleton
class MockUtcClock : UtcClock() {
  companion object {
    val INITIAL_TIME: LocalDateTime = LocalDateTime.parse("2000-01-01T00:00:00")
  }

  private var time = INITIAL_TIME

  fun expire() {
    time = time.plus(MockConfigProfile.DURATION)
  }

  fun reset() {
    time = INITIAL_TIME
  }

  override fun now(): LocalDateTime = time
}