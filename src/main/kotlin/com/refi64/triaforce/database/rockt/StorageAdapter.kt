package com.refi64.triaforce.database.rockt

interface StorageAdapter<V> {
  fun save(value: V): ByteArray
  fun load(bytes: ByteArray): V
}