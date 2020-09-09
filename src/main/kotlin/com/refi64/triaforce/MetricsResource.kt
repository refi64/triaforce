package com.refi64.triaforce

import com.refi64.triaforce.database.TrialDatabaseRepository
import org.eclipse.microprofile.metrics.MetricUnits
import org.eclipse.microprofile.metrics.annotation.Gauge
import javax.enterprise.inject.Default
import javax.inject.Inject
import javax.ws.rs.Path

@Path("/")
class MetricsResource {
  @Inject
  @field:Default
  lateinit var repository: TrialDatabaseRepository

  @Gauge(name = "activeTrials", unit = MetricUnits.NONE, description = "Number of active trials")
  fun activeTrials(): Long = repository.countActive()

  @Gauge(name = "totalTrials", unit = MetricUnits.NONE, description = "Number of total trials")
  fun totalTrials(): Long = repository.countTotal()
}
