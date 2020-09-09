package com.refi64.triaforce.database.rockt

interface MergeDescriptor<M> {
  val mergeOperatorName: String
  fun convertMerge(merge: M): ByteArray
}