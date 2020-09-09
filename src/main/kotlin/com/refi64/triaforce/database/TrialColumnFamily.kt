package com.refi64.triaforce.database

import com.refi64.triaforce.database.rockt.LongStorageAdapter
import com.refi64.triaforce.database.rockt.RocktColumnFamily
import com.refi64.triaforce.database.rockt.StringStorageAdapter
import com.refi64.triaforce.database.rockt.UInt64AddMergeDescriptor
import com.refi64.triaforce.util.ExpirationPoint
import org.rocksdb.BuiltinComparator
import org.rocksdb.ColumnFamilyOptions

class TrialColumnFamily {
  enum class Ordinal { ID, EXPIRATION, METRICS }

  object Id : RocktColumnFamily.Impl<Ordinal, String, ExpirationPoint>(DEFAULT,
      Ordinal.ID,
      StringStorageAdapter(),
      ExpirationPointStorageAdapter())

  object Expiration : RocktColumnFamily.Mergeable<Ordinal, ExpirationPoint, Long, Long>("expiration",
      Ordinal.EXPIRATION,
      ExpirationPointStorageAdapter(),
      LongStorageAdapter(),
      UInt64AddMergeDescriptor()) {
    override fun modifyColumnFamilyOptions(options: ColumnFamilyOptions) {
      super.modifyColumnFamilyOptions(options)

      // Compare in reverse, so the first elements are the newest dates.
      options.setComparator(BuiltinComparator.REVERSE_BYTEWISE_COMPARATOR)
    }
  }

  object Metrics : RocktColumnFamily.Mergeable<Ordinal, String, Long, Long>("metrics",
      Ordinal.METRICS,
      StringStorageAdapter(),
      LongStorageAdapter(),
      UInt64AddMergeDescriptor()) {
    const val KEY_TOTAL_TRIALS = "total-trials"
  }
}