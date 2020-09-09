package com.refi64.triaforce.database.rockt.iter

sealed class IteratorSeek<K> {
  class First<K> : IteratorSeek<K>()
  class Last<K> : IteratorSeek<K>()

  data class Next<K>(val key: K) : IteratorSeek<K>()
  data class Previous<K>(val key: K) : IteratorSeek<K>()
}