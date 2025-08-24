package com.springboot.staking.infrastructure.service.builder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.staking.application.dto.request.SignRequest;
import com.springboot.staking.domain.staking.model.StakingTransaction;
import com.springboot.staking.domain.staking.service.SignRequestBuilder;
import com.springboot.staking.shared.constant.Symbol;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CosmosSignRequestBuilder implements SignRequestBuilder {

  private final ObjectMapper objectMapper;

  @Override
  public Symbol getSymbol() {
    return Symbol.ATOM;
  }

  @Override
  public SignRequest buildSignRequest(StakingTransaction transaction) {
    String accountNumber = "";  // 기본값
    String sequence = "";           // 기본값
    String chainId = "";           // 기본값
    // extraData가 있으면 JSON에서 값 추출
    if (transaction.getExtraData() != null && !transaction.getExtraData().value().trim()
        .isEmpty()) {
      try {
        JsonNode extraNode = objectMapper.readTree(transaction.getExtraData().value());
        if (extraNode.has("accountNumber")) {
          accountNumber = extraNode.get("accountNumber").asText();
        }
        if (extraNode.has("sequence")) {
          sequence = extraNode.get("sequence").asText();
        }
        if (extraNode.has("chainId")) {
          chainId = extraNode.get("chainId").asText();
        }
      } catch (JsonProcessingException e) {
        log.warn("Failed to parse extraData JSON for requestId: {}, using default values",
            transaction.getRequestId(), e);
      }
    }

    // 필수 값들이 비어있으면 에러
    if (accountNumber.isEmpty()) {
      throw new IllegalArgumentException(
          "accountNumber is required for Cosmos chains but not found in extraData for requestId: "
              + transaction.getRequestId());
    }
    if (sequence.isEmpty()) {
      throw new IllegalArgumentException(
          "sequence is required for Cosmos chains but not found in extraData for requestId: "
              + transaction.getRequestId());
    }
    if (chainId.isEmpty()) {
      throw new IllegalArgumentException(
          "chainId is required for Cosmos chains but not found in extraData for requestId: "
              + transaction.getRequestId());
    }

    return SignRequest.forCosmos(transaction.getUnsignedTx().value(),
        accountNumber,
        sequence, chainId);
  }
}