package com.refi64.triaforce

import org.eclipse.microprofile.config.spi.Converter
import java.time.Period

class PeriodCustomValueConverter : Converter<Period> {
  override fun convert(value: String): Period = Period.parse(value)
}