package com.springboot.staking.domain.staking.vo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;

public record ExtraData(String value) {
  
  public ExtraData {
    // null 허용 (선택적 데이터)
  }
  
  public static ExtraData of(String value) {
    return new ExtraData(value);
  }
  
  public static ExtraData empty() {
    return new ExtraData(null);
  }
  
  public boolean isEmpty() {
    return value == null || value.trim().isEmpty();
  }
  
  public Optional<String> extractValue(String key, ObjectMapper objectMapper) {
    if (isEmpty()) {
      return Optional.empty();
    }
    
    try {
      JsonNode node = objectMapper.readTree(value);
      return node.has(key) ? Optional.of(node.get(key).asText()) : Optional.empty();
    } catch (JsonProcessingException e) {
      return Optional.empty();
    }
  }
  
  @Override
  public String toString() {
    return value != null ? value : "";
  }
}