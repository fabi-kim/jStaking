package com.springboot.staking.presentation.controller;

import com.springboot.staking.application.dto.request.BroadcastRequest;
import com.springboot.staking.application.dto.request.SignRequest;
import com.springboot.staking.application.dto.response.BroadcastResponse;
import com.springboot.staking.application.dto.response.TransactionResponse;
import com.springboot.staking.domain.node.service.NodeServiceFactory;
import com.springboot.staking.shared.constant.Symbol;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("node")
public class NodeController {

  private final NodeServiceFactory nodeServiceFactory;

  @GetMapping("/{symbol}/balance/{address}")
  public ResponseEntity<String> getBalance(
      @PathVariable("symbol") Symbol symbol,
      @PathVariable("address") String address) {

    return ResponseEntity.ok(
        nodeServiceFactory.getServiceBySymbol(symbol).getBalance(address));
  }

  @GetMapping("/{symbol}/tx/{txHash}")
  public ResponseEntity<TransactionResponse> getTx(
      @PathVariable("symbol") Symbol symbol,
      @PathVariable("txHash") String txHash) {

    return ResponseEntity.ok(
        nodeServiceFactory.getServiceBySymbol(symbol).getTx(txHash));
  }

  @Operation(summary = "서명")
  @PostMapping("/{symbol}/sign")
  public ResponseEntity<String> sign(
      @PathVariable("symbol") Symbol symbol,
      @RequestBody() SignRequest signRequest) {

    return ResponseEntity.ok(
        nodeServiceFactory.getServiceBySymbol(symbol).sign(signRequest));
  }

  @PostMapping("/{symbol}/broadcast")
  public ResponseEntity<BroadcastResponse> broadcast(
      @PathVariable("symbol") Symbol symbol,
      @RequestBody() BroadcastRequest broadcastRequest) {

    return ResponseEntity.ok(
        nodeServiceFactory.getServiceBySymbol(symbol).broadcast(broadcastRequest));
  }
}
