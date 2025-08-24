package com.springboot.staking.infrastructure.adaptor.proxy;

import com.springboot.staking.infrastructure.adaptor.proxy.dto.request.EthRpcRequest;
import com.springboot.staking.infrastructure.adaptor.proxy.dto.response.EthRpcResponse;
import com.springboot.staking.infrastructure.adaptor.proxy.dto.response.EthTxResponse;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class EthNodeWebClient {

  ParameterizedTypeReference<EthRpcResponse<EthTxResponse>> TYPE =
      new ParameterizedTypeReference<>() {
      };
  @Value("${external.proxy.eth.url}")
  private String url;

  public EthRpcResponse<EthTxResponse> getTx(String txHash) {
    WebClient webClient = WebClient.builder()
        .baseUrl(url)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
    System.out.println("[getTx] request: " + EthRpcRequest.ofGetTransaction(txHash));
    System.out.println("[getTx] request.params: " + Arrays.toString(
        EthRpcRequest.ofGetTransaction(txHash).params()));

    var response = webClient.post()
        .uri(uriBuilder -> uriBuilder.path("")
            .build())
        .bodyValue(EthRpcRequest.ofGetTransaction(txHash))
        .retrieve()
        .toEntity(new ParameterizedTypeReference<Map<String, Object>>() {
        })
        .block();

    System.out.println("[getTx] response: " + Objects.requireNonNull(response).getStatusCode());
    Objects.requireNonNull(response.getBody()).forEach(
        (k, v) -> System.out.println("[getTx] response => k: " + k + "v:" + v)
    );
    return null;
  }

}
