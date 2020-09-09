package com.refi64.triaforce

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.quarkus.jackson.ObjectMapperCustomizer
import javax.inject.Singleton

@Singleton
class RegisterKotlinModuleCustomizer : ObjectMapperCustomizer {
  override fun customize(objectMapper: ObjectMapper) {
    objectMapper.registerKotlinModule()
  }
}
