package com.refi64.triaforce.database.rockt

class UInt64AddMergeDescriptor : MergeDescriptor<Long> {
  private val adapter = LongStorageAdapter()

  override val mergeOperatorName = "uint64add"
  override fun convertMerge(merge: Long): ByteArray = adapter.save(merge)
}