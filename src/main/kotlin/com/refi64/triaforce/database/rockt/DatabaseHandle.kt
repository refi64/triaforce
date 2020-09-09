package com.refi64.triaforce.database.rockt

import com.refi64.triaforce.util.AutoCloser
import org.rocksdb.ColumnFamilyDescriptor
import org.rocksdb.ColumnFamilyHandle
import org.rocksdb.DBOptions
import org.rocksdb.RocksDB

open class DatabaseHandle<Ordinal : Enum<*>>(override val database: RocksDB,
                                             columnFamilyHandleSet: ColumnFamilyHandleSet<Ordinal>, closer: AutoCloser) :
    AbstractDatabaseHandle<Ordinal>(columnFamilyHandleSet, closer) {
  class Factory<Ordinal : Enum<*>>(private val optionsBuilder: DBOptions.() -> Unit) :
      AbstractDatabaseHandleFactory<Ordinal>() {
    override fun createHandle(database: RocksDB, columnFamilyHandleSet: ColumnFamilyHandleSet<Ordinal>,
                              closer: AutoCloser) = DatabaseHandle(database, columnFamilyHandleSet, closer)

    override fun openInternalDb(path: String, columnFamilyDescriptors: List<ColumnFamilyDescriptor>,
                                columnFamilyHandles: MutableList<ColumnFamilyHandle>, optionsCloser: AutoCloser): RocksDB =
        RocksDB.open(optionsCloser.register(DBOptions()).apply(optionsBuilder),
            path,
            columnFamilyDescriptors,
            columnFamilyHandles)
  }

  companion object {
    fun <Ordinal : Enum<*>> create(path: String, columnFamilies: List<RocktColumnFamily<Ordinal>>,
                                   optionsBuilder: DBOptions.() -> Unit) =
        Factory<Ordinal>(optionsBuilder).create(path, columnFamilies) as DatabaseHandle<Ordinal>
  }
}
