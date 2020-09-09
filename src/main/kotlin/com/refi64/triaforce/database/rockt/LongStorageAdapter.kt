package com.refi64.triaforce.database.rockt

import java.nio.ByteBuffer
import java.nio.ByteOrder

class LongStorageAdapter : StorageAdapter<Long> {
  override fun load(bytes: ByteArray): Long = ByteBuffer.allocate(Long.SIZE_BYTES).apply {
    order(ByteOrder.LITTLE_ENDIAN)
    put(bytes)
    flip()
  }.long

  override fun save(value: Long): ByteArray = ByteBuffer.allocate(Long.SIZE_BYTES).apply {
    order(ByteOrder.LITTLE_ENDIAN)
    putLong(value)
  }.array()
}