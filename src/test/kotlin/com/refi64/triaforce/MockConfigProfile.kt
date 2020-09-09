package com.refi64.triaforce

import io.quarkus.test.junit.QuarkusTestProfile
import java.time.Period

class MockConfigProfile : QuarkusTestProfile {
  companion object {
    val DURATION: Period = Period.parse("P1D")
    const val PROJECT = "test"
  }

  override fun getConfigOverrides(): Map<String, String> {
    return mapOf("trial.duration" to DURATION.toString(), "trial.projects" to PROJECT)
  }
}
