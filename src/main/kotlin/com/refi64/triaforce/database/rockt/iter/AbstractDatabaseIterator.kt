package com.refi64.triaforce.database.rockt.iter

import com.refi64.triaforce.database.rockt.RocktColumnFamily
import org.rocksdb.RocksIterator

abstract class AbstractDatabaseIterator<Ordinal : Enum<*>, K, V, Entry>(protected val iterator: RocksIterator,
                                                                        protected val columnFamily: RocktColumnFamily.Impl<Ordinal, K, V>) :
    Iterator<Entry> {
  protected abstract fun accessEntry(): Entry

  val isValid: Boolean get() = iterator.isValid

  override fun hasNext(): Boolean = iterator.isValid

  override fun next(): Entry {
    if (!iterator.isValid) {
      throw NoSuchElementException()
    }

    val entry = accessEntry()
    iterator.next()

    return entry
  }

  fun previous(): Entry {
    if (!iterator.isValid) {
      throw NoSuchElementException()
    }

    val entry = accessEntry()
    iterator.next()

    return entry
  }

  fun seek(to: IteratorSeek<K>) {
    when (to) {
      is IteratorSeek.First<K> -> iterator.seekToFirst()
      is IteratorSeek.Last<K> -> iterator.seekToLast()
      is IteratorSeek.Next<K> -> iterator.seek(columnFamily.keyAdapter.save(to.key))
      is IteratorSeek.Previous<K> -> iterator.seekForPrev(columnFamily.keyAdapter.save(to.key))
    }
  }
}
