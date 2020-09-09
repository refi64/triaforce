package com.refi64.triaforce.database

import com.refi64.triaforce.database.rockt.DatabaseTransaction
import com.refi64.triaforce.database.rockt.TransactionalDatabaseHandle
import com.refi64.triaforce.database.rockt.iter.IteratorSeek
import com.refi64.triaforce.util.ExpirationPoint
import com.refi64.triaforce.util.UtcClock
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.rocksdb.RocksDB
import org.rocksdb.RocksDBException
import org.rocksdb.Status
import java.time.Period
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.enterprise.inject.Default
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrialDatabaseRepository {
  data class Key(val project: String, val id: String) {
    override fun toString() = "$project:$id"
  }

  @Inject
  @field:Default
  lateinit var utcClock: UtcClock

  @ConfigProperty(name = "database.path")
  lateinit var databasePath: String

  private lateinit var databaseHandle: TransactionalDatabaseHandle<TrialColumnFamily.Ordinal>

  fun getOrInsertExpiration(key: Key, duration: Period): ExpirationPoint {
    while (true) {
      try {
        databaseHandle.beginTransaction().use { transaction ->
          transaction
              .getForUpdate(TrialColumnFamily.Id, key.toString(), DatabaseTransaction.Exclusive.YES)
              ?.let { return it }

          val expiration = ExpirationPoint(utcClock.now().plus(duration))
          transaction.put(TrialColumnFamily.Id, key.toString(), expiration)
          transaction.merge(TrialColumnFamily.Expiration, expiration, 1)
          transaction.merge(TrialColumnFamily.Metrics, TrialColumnFamily.Metrics.KEY_TOTAL_TRIALS, 1)
          transaction.commit()

          return expiration
        }
      } catch (ex: RocksDBException) {
        if (ex.status.code == Status.Code.Busy) {
          continue
        } else {
          throw ex
        }
      }
    }
  }

  fun countActive(): Long {
    var count = 0L
    val now = utcClock.now()

    for (item in databaseHandle.openEntryIterator(TrialColumnFamily.Expiration, IteratorSeek.First())) {
      if (item.key.isExpiredAt(now)) {
        break
      }

      count += item.value
    }

    return count
  }

  fun countTotal(): Long =
      databaseHandle.get(TrialColumnFamily.Metrics, TrialColumnFamily.Metrics.KEY_TOTAL_TRIALS) ?: 0

  @PostConstruct
  fun postConstruct() {
    RocksDB.loadLibrary()

    databaseHandle = TransactionalDatabaseHandle.create(databasePath,
        listOf(TrialColumnFamily.Id, TrialColumnFamily.Expiration, TrialColumnFamily.Metrics)) {
      db.setCreateIfMissing(true)
      db.setCreateMissingColumnFamilies(true)
    }
  }

  @PreDestroy
  fun preDestroy() {
    databaseHandle.close()
  }

  fun resetDatabaseForUnitTests(path: String) {
    if (::databaseHandle.isInitialized) {
      preDestroy()
    }

    databasePath = path
    postConstruct()
  }
}