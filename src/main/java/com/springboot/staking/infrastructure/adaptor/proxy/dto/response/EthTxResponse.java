package com.springboot.staking.infrastructure.adaptor.proxy.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EthTxResponse(
    List<AccessListEntry> accessList,
    String blockHash,
    String blockNumber,
    String chainId,
    String from,
    String gas,                  // ⬅ 추가
    String gasPrice,             // ⬅ 추가
    String hash,
    String input,
    String maxFeePerGas,
    String maxPriorityFeePerGas,
    String nonce,
    String r,
    String s,
    String to,                   // 계약 생성 트랜잭션이면 null일 수 있음
    String transactionIndex,     // pending일 땐 null일 수 있음
    String type,                 // "0x0","0x1","0x2"(EIP-1559) 등
    String v,                    // 일부 노드는 yParity만 주거나 둘 다 줌
    String value,
    String yParity
) {

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record AccessListEntry(
      String address,
      List<String> storageKeys
  ) {

  }
}
