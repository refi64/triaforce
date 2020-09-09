package com.refi64.triaforce.database.rockt

import org.rocksdb.ReadOptions
import org.rocksdb.Transaction

class DatabaseTransaction<Ordinal : Enum<*>>(private val transaction: Transaction,
                                             private val columnFamilyHandleSet: ColumnFamilyHandleSet<Ordinal>) :
    AutoCloseable {
  @Suppress("unused")
  enum class Exclusive { YES, NO }

  fun <K, V> get(columnFamily: RocktColumnFamily.Impl<Ordinal, K, V>, key: K,
                 readOptionsBuilder: (ReadOptions.() -> Unit)? = null): V? {
    ReadOptions().use { readOptions ->
      if (readOptionsBuilder != null) {
        readOptionsBuilder(readOptions)
      }

      return transaction
          .get(columnFamilyHandleSet.get(columnFamily), readOptions, columnFamily.keyAdapter.save(key))
          ?.let(columnFamily.valueAdapter::load)
    }
  }

  fun <K, V> getForUpdate(columnFamily: RocktColumnFamily.Impl<Ordinal, K, V>, key: K, exclusive: Exclusive,
                          readOptionsBuilder: (ReadOptions.() -> Unit)? = null): V? {
    ReadOptions().use { readOptions ->
      if (readOptionsBuilder != null) {
        readOptionsBuilder(readOptions)
      }

      return transaction
          .getForUpdate(readOptions,
              columnFamilyHandleSet.get(columnFamily),
              columnFamily.keyAdapter.save(key),
              exclusive == Exclusive.YES)
          ?.let(columnFamily.valueAdapter::load)
    }
  }

  fun <K, V> put(columnFamily: RocktColumnFamily.Impl<Ordinal, K, V>, key: K, value: V) = transaction.put(
      columnFamilyHandleSet.get(columnFamily),
      columnFamily.keyAdapter.save(key),
      columnFamily.valueAdapter.save(value))

  fun <K, V, M> merge(columnFamily: RocktColumnFamily.Mergeable<Ordinal, K, V, M>, key: K, merge: M) =
      transaction.merge(columnFamilyHandleSet.get(columnFamily),
          columnFamily.keyAdapter.save(key),
          columnFamily.mergeDescriptor.convertMerge(merge))

  fun commit() {
    transaction.commit()
  }

  override fun close() {
    transaction.close()
  }
}