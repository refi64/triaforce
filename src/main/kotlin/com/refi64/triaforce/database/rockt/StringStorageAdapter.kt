package com.refi64.triaforce.database.rockt

import java.nio.charset.Charset

class StringStorageAdapter(private val charset: Charset = Charsets.UTF_8) : StorageAdapter<String> {
  override fun load(bytes: ByteArray) = bytes.toString(charset)
  override fun save(value: String) = value.toByteArray(charset)
}