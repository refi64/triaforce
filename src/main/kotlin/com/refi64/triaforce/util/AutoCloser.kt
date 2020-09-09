package com.refi64.triaforce.util

class AutoCloser : AutoCloseable {
  private val toClose = mutableListOf<AutoCloseable>()

  fun <T : AutoCloseable> register(item: T): T {
    toClose.add(item)
    return item
  }

  fun <T : AutoCloseable> registerAll(items: List<T>) {
    toClose.addAll(items)
  }

  override fun close() {
    for (item in toClose.reversed()) {
      item.close()
    }

    toClose.clear()
  }
}