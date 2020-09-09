package com.refi64.triaforce

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.quarkus.jackson.ObjectMapperCustomizer
import javax.inject.Singleton

@Singleton
class RegisterJavaTimeModuleCustomizer : ObjectMapperCustomizer {
  override fun customize(objectMapper: ObjectMapper) {
    objectMapper.registerModule(JavaTimeModule())
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
  }
}