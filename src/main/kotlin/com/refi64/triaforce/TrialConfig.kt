package com.refi64.triaforce

import org.eclipse.microprofile.config.inject.ConfigProperty
import java.time.Period
import javax.inject.Singleton

@Singleton
class TrialConfig {
  @ConfigProperty(name = "trial.duration")
  lateinit var duration: Period

  @ConfigProperty(name = "trial.projects")
  lateinit var projects: List<String>
}