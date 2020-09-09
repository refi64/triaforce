package com.refi64.triaforce.database.rockt

import org.rocksdb.ColumnFamilyHandle

class ColumnFamilyHandleSet<Ordinal : Enum<*>>(private val handles: List<ColumnFamilyHandle>) {
  fun get(columnFamily: RocktColumnFamily<Ordinal>) = handles[columnFamily.order.ordinal]
}