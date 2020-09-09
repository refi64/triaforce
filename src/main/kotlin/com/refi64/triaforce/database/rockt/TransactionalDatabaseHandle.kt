package com.refi64.triaforce.database.rockt

import com.refi64.triaforce.util.AutoCloser
import org.rocksdb.*

class TransactionalDatabaseHandle<Ordinal : Enum<*>>(override val database: TransactionDB,
                                                     columnFamilyHandleSet: ColumnFamilyHandleSet<Ordinal>,
                                                     closer: AutoCloser) :
    AbstractDatabaseHandle<Ordinal>(columnFamilyHandleSet, closer) {
  class Factory<Ordinal : Enum<*>>(private val optionsBuilder: Options.() -> Unit) :
      AbstractDatabaseHandleFactory<Ordinal>() {
    data class Options(val db: DBOptions, val transaction: TransactionDBOptions)

    override fun createHandle(database: RocksDB, columnFamilyHandleSet: ColumnFamilyHandleSet<Ordinal>,
                              closer: AutoCloser) =
        TransactionalDatabaseHandle(database as TransactionDB, columnFamilyHandleSet, closer)

    override fun openInternalDb(path: String, columnFamilyDescriptors: List<ColumnFamilyDescriptor>,
                                columnFamilyHandles: MutableList<ColumnFamilyHandle>, optionsCloser: AutoCloser): RocksDB =
        Options(db = optionsCloser.register(DBOptions()), transaction = optionsCloser.register(TransactionDBOptions()))
            .apply(optionsBuilder)
            .let { options ->
              TransactionDB.open(options.db, options.transaction, path, columnFamilyDescriptors, columnFamilyHandles)
            }
  }

  companion object {
    fun <Ordinal : Enum<*>> create(path: String, columnFamilies: List<RocktColumnFamily<Ordinal>>,
                                   optionsBuilder: Factory.Options.() -> Unit) =
        Factory<Ordinal>(optionsBuilder).create(path, columnFamilies) as TransactionalDatabaseHandle<Ordinal>
  }

  fun beginTransaction(writeOptionsBuilder: (WriteOptions.() -> Unit)? = null): DatabaseTransaction<Ordinal> {
    WriteOptions().use { writeOptions ->
      if (writeOptionsBuilder != null) {
        writeOptionsBuilder(writeOptions)
      }

      return DatabaseTransaction(database.beginTransaction(writeOptions), columnFamilyHandleSet)
    }
  }
}