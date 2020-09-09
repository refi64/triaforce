package com.refi64.triaforce.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun LocalDateTime.formatUtc() = format(DateTimeFormatter.ISO_DATE_TIME)
