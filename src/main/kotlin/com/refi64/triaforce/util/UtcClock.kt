package com.refi64.triaforce.util

import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Singleton

@Singleton
open class UtcClock {
  open fun now(): LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)
}