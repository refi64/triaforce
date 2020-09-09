package com.refi64.triaforce.database.rockt.iter

import com.refi64.triaforce.database.rockt.RocktColumnFamily
import org.rocksdb.RocksIterator

class ValueDatabaseIterator<Ordinal : Enum<*>, K, V>(iterator: RocksIterator,
                                                     columnFamily: RocktColumnFamily.Impl<Ordinal, K, V>) :
    AbstractDatabaseIterator<Ordinal, K, V, V>(iterator, columnFamily) {
  override fun accessEntry(): V = columnFamily.valueAdapter.load(iterator.value())
}