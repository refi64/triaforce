package com.refi64.triaforce

import com.refi64.triaforce.database.TrialDatabaseRepository
import com.refi64.triaforce.utils.formatUtc
import io.quarkus.test.common.http.TestHTTPEndpoint
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.junit.TestProfile
import io.restassured.RestAssured.given
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import javax.enterprise.inject.Default
import javax.inject.Inject

@QuarkusTest
@TestHTTPEndpoint(StatusResource::class)
@TestProfile(MockConfigProfile::class)
class StatusResourceTest {
  @Inject
  @field:Default
  lateinit var repository: TrialDatabaseRepository

  @Inject
  @field:Default
  lateinit var utcClock: MockUtcClock

  @BeforeEach
  fun beforeEach(@TempDir tempDir: File) {
    repository.resetDatabaseForUnitTests(tempDir.absolutePath)
    utcClock.reset()
  }

  @Test
  fun testStatusEndpoint() {
    val key1Expiration = utcClock.now().plus(MockConfigProfile.DURATION)

    given()
        .`when`()
        .get("/${MockConfigProfile.PROJECT}/0")
        .then()
        .statusCode(200)
        .body("active", `is`(true))
        .body("expiration", `is`(key1Expiration.formatUtc()))

    utcClock.expire()

    given()
        .`when`()
        .get("/${MockConfigProfile.PROJECT}/0")
        .then()
        .statusCode(200)
        .body("active", `is`(false))
        .body("expiration", `is`(key1Expiration.formatUtc()))

    val key2Expiration = utcClock.now().plus(MockConfigProfile.DURATION)

    given()
        .`when`()
        .get("/${MockConfigProfile.PROJECT}/1")
        .then()
        .statusCode(200)
        .body("active", `is`(true))
        .body("expiration", `is`(key2Expiration.formatUtc()))
  }

  @Test
  fun testStatusErrors() {
    given().`when`().get("/something/0").then().statusCode(404)
  }
}