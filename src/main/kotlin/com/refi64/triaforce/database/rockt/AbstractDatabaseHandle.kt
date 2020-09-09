package com.refi64.triaforce.database.rockt

import com.refi64.triaforce.database.rockt.iter.EntryDatabaseIterator
import com.refi64.triaforce.database.rockt.iter.IteratorSeek
import com.refi64.triaforce.database.rockt.iter.KeyDatabaseIterator
import com.refi64.triaforce.database.rockt.iter.ValueDatabaseIterator
import com.refi64.triaforce.util.AutoCloser
import org.rocksdb.RocksDB

abstract class AbstractDatabaseHandle<Ordinal : Enum<*>>(
    protected val columnFamilyHandleSet: ColumnFamilyHandleSet<Ordinal>, protected val closer: AutoCloser) :
    AutoCloseable {
  protected abstract val database: RocksDB

  override fun close() {
    closer.close()
  }

  private fun <K, V> getIterator(columnFamily: RocktColumnFamily.Impl<Ordinal, K, V>) =
      database.newIterator(columnFamilyHandleSet.get(columnFamily))

  fun <K, V> get(columnFamily: RocktColumnFamily.Impl<Ordinal, K, V>, key: K): V? = database
      .get(columnFamilyHandleSet.get(columnFamily), columnFamily.keyAdapter.save(key))
      ?.let(columnFamily.valueAdapter::load)

  fun <K, V> put(columnFamily: RocktColumnFamily.Impl<Ordinal, K, V>, key: K, value: V) = database.put(
      columnFamilyHandleSet.get(columnFamily),
      columnFamily.keyAdapter.save(key),
      columnFamily.valueAdapter.save(value))

  fun <K, V, M> merge(columnFamily: RocktColumnFamily.Mergeable<Ordinal, K, V, M>, key: K, merge: M) = database.merge(
      columnFamilyHandleSet.get(columnFamily),
      columnFamily.keyAdapter.save(key),
      columnFamily.mergeDescriptor.convertMerge(merge))

  fun <K, V> openKeyIterator(columnFamily: RocktColumnFamily.Impl<Ordinal, K, V>,
                             at: IteratorSeek<K> = IteratorSeek.First()) =
      KeyDatabaseIterator(getIterator(columnFamily), columnFamily).apply { seek(to = at) }

  fun <K, V> openValueIterator(columnFamily: RocktColumnFamily.Impl<Ordinal, K, V>,
                               at: IteratorSeek<K> = IteratorSeek.First()) =
      ValueDatabaseIterator(getIterator(columnFamily), columnFamily).apply { seek(to = at) }

  fun <K, V> openEntryIterator(columnFamily: RocktColumnFamily.Impl<Ordinal, K, V>,
                               at: IteratorSeek<K> = IteratorSeek.First()) =
      EntryDatabaseIterator(getIterator(columnFamily), columnFamily).apply { seek(to = at) }
}
