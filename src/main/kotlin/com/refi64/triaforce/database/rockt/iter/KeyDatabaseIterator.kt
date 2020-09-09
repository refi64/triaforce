package com.refi64.triaforce.database.rockt.iter

import com.refi64.triaforce.database.rockt.RocktColumnFamily
import org.rocksdb.RocksIterator

class KeyDatabaseIterator<Ordinal : Enum<*>, K, V>(iterator: RocksIterator,
                                                   columnFamily: RocktColumnFamily.Impl<Ordinal, K, V>) :
    AbstractDatabaseIterator<Ordinal, K, V, K>(iterator, columnFamily) {
  override fun accessEntry(): K = columnFamily.keyAdapter.load(iterator.key())
}