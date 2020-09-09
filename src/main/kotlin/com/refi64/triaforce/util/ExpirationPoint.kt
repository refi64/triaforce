package com.refi64.triaforce.util

import java.time.LocalDateTime

data class ExpirationPoint(val expiration: LocalDateTime) {
  // NOTE: isBefore was explicitly chosen so that this is considered expired if expiration == time
  fun isExpiredAt(time: LocalDateTime) = !time.isBefore(expiration)
}