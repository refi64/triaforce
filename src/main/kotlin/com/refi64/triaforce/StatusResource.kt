package com.refi64.triaforce

import com.refi64.triaforce.database.TrialDatabaseRepository
import com.refi64.triaforce.util.UtcClock
import org.eclipse.microprofile.metrics.MetricUnits
import org.eclipse.microprofile.metrics.annotation.Timed
import javax.enterprise.inject.Default
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/status")
@Produces(MediaType.APPLICATION_JSON)
class StatusResource {
  @Inject
  @field:Default
  lateinit var repository: TrialDatabaseRepository

  @Inject
  @field:Default
  lateinit var config: TrialConfig

  @Inject
  @field:Default
  lateinit var utcClock: UtcClock

  @GET
  @Path("/{project: [a-zA-Z0-9]+}/{id}")
  @Timed(name = "statusTimer",
      description = "A measure of how long to check/update the status",
      unit = MetricUnits.MILLISECONDS)
  fun get(@PathParam("project") project: String, @PathParam("id") id: String): StatusResponse {
    if (!config.projects.contains(project)) {
      throw WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Project not found").build())
    }

    val expiration = repository.getOrInsertExpiration(TrialDatabaseRepository.Key(project, id), config.duration)
    val active = !expiration.isExpiredAt(utcClock.now())
    return StatusResponse(active, expiration.expiration)
  }
}
