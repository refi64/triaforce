package com.refi64.triaforce

import com.refi64.triaforce.database.TrialDatabaseRepository
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.junit.TestProfile
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import javax.enterprise.inject.Default
import javax.inject.Inject
import kotlin.test.assertEquals

@QuarkusTest
@TestProfile(MockConfigProfile::class)
class TrialDatabaseRepositoryTest {
  // XXX: beforeEach & injects are copied from StatusResourceTest

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
  fun testRepository() {
    val key1 = TrialDatabaseRepository.Key(MockConfigProfile.PROJECT, "0")
    val key2 = TrialDatabaseRepository.Key(MockConfigProfile.PROJECT, "1")

    val key1Expiration = utcClock.now().plus(MockConfigProfile.DURATION)

    assertEquals(repository.getOrInsertExpiration(key1, MockConfigProfile.DURATION).expiration, key1Expiration)
    assertEquals(repository.countTotal(), 1)
    assertEquals(repository.countActive(), 1)

    utcClock.expire()
    assertEquals(repository.getOrInsertExpiration(key1, MockConfigProfile.DURATION).expiration, key1Expiration)
    assertEquals(1, repository.countTotal())
    assertEquals(0, repository.countActive())

    val key2Expiration = utcClock.now().plus(MockConfigProfile.DURATION)

    assertEquals(repository.getOrInsertExpiration(key2, MockConfigProfile.DURATION).expiration, key2Expiration)
    assertEquals(repository.countTotal(), 2)
    assertEquals(repository.countActive(), 1)
  }
}
