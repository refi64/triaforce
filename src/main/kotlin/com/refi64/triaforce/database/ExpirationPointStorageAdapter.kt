package com.refi64.triaforce.database

import com.refi64.triaforce.database.rockt.StorageAdapter
import com.refi64.triaforce.database.rockt.StringStorageAdapter
import com.refi64.triaforce.util.ExpirationPoint
import java.time.LocalDateTime

class ExpirationPointStorageAdapter : StorageAdapter<ExpirationPoint> {
  private val stringStorageAdapter = StringStorageAdapter()

  override fun load(bytes: ByteArray): ExpirationPoint =
      ExpirationPoint(LocalDateTime.parse(stringStorageAdapter.load(bytes)))

  override fun save(value: ExpirationPoint) = stringStorageAdapter.save(value.expiration.toString())
}