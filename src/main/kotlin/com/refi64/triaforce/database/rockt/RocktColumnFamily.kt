package com.refi64.triaforce.database.rockt

import org.rocksdb.ColumnFamilyOptions

abstract class RocktColumnFamily<Ordinal : Enum<*>>(val name: String, val order: Ordinal) {
  companion object {
    const val DEFAULT = "default"
  }

  abstract class Impl<Ordinal : Enum<*>, K, V>(name: String, order: Ordinal, val keyAdapter: StorageAdapter<K>,
                                               val valueAdapter: StorageAdapter<V>) :
      RocktColumnFamily<Ordinal>(name, order)

  abstract class Mergeable<Ordinal : Enum<*>, K, V, M>(name: String, order: Ordinal, keyAdapter: StorageAdapter<K>,
                                                       valueAdapter: StorageAdapter<V>,
                                                       val mergeDescriptor: MergeDescriptor<M>) :
      Impl<Ordinal, K, V>(name, order, keyAdapter, valueAdapter) {
    override fun modifyColumnFamilyOptions(options: ColumnFamilyOptions) {
      options.setMergeOperatorName(mergeDescriptor.mergeOperatorName)
    }
  }

  open fun modifyColumnFamilyOptions(options: ColumnFamilyOptions) {}
}