package com.refi64.triaforce.database.rockt

import com.refi64.triaforce.util.AutoCloser
import org.rocksdb.ColumnFamilyDescriptor
import org.rocksdb.ColumnFamilyHandle
import org.rocksdb.ColumnFamilyOptions
import org.rocksdb.RocksDB

abstract class AbstractDatabaseHandleFactory<Ordinal : Enum<*>> {
  protected abstract fun openInternalDb(path: String, columnFamilyDescriptors: List<ColumnFamilyDescriptor>,
                                        columnFamilyHandles: MutableList<ColumnFamilyHandle>, optionsCloser: AutoCloser): RocksDB

  protected abstract fun createHandle(database: RocksDB, columnFamilyHandleSet: ColumnFamilyHandleSet<Ordinal>,
                                      closer: AutoCloser): AbstractDatabaseHandle<Ordinal>

  fun create(path: String, columnFamilies: List<RocktColumnFamily<Ordinal>>): AbstractDatabaseHandle<Ordinal> {
    val closer = AutoCloser()
    val columnFamilyHandles = mutableListOf<ColumnFamilyHandle>()
    var success = false

    try {
      val columnFamilyDescriptors = mutableListOf<ColumnFamilyDescriptor>()

      for (columnFamily in columnFamilies.sortedBy { it.order.ordinal }) {
        val options = closer.register(ColumnFamilyOptions())
        columnFamily.modifyColumnFamilyOptions(options)

        columnFamilyDescriptors.add(ColumnFamilyDescriptor(columnFamily.name.toByteArray(), options))
      }

      val database = closer.register(openInternalDb(path, columnFamilyDescriptors, columnFamilyHandles, closer))

      closer.registerAll(columnFamilyHandles)

      success = true
      return createHandle(database, ColumnFamilyHandleSet(columnFamilyHandles), closer)
    } finally {
      if (!success) {
        closer.close()
      }
    }
  }
}