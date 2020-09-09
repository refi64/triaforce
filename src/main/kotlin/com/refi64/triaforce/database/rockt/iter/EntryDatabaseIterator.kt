package com.refi64.triaforce.database.rockt.iter

import com.refi64.triaforce.database.rockt.RocktColumnFamily
import org.rocksdb.RocksIterator
import java.util.*

class EntryDatabaseIterator<Ordinal : Enum<*>, K, V>(iterator: RocksIterator,
                                                     columnFamily: RocktColumnFamily.Impl<Ordinal, K, V>) :
    AbstractDatabaseIterator<Ordinal, K, V, Map.Entry<K, V>>(iterator, columnFamily) {
  override fun accessEntry() = AbstractMap.SimpleEntry(columnFamily.keyAdapter.load(iterator.key()),
      columnFamily.valueAdapter.load(iterator.value()))
}