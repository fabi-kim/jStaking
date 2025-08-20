package com.springboot.staking.controller;

import com.springboot.staking.common.constant.Symbol;
import com.springboot.staking.data.dto.request.StakingRequest;
import com.springboot.staking.data.dto.response.DelegateTxResponse;
import com.springboot.staking.service.StakingServiceFactory;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("staking")
public class StakingController {

  private final StakingServiceFactory stakingServiceFactory;

  @Operation(summary = "트랜잭션 생성")
  @PostMapping("/{symbol}/delegate")
  public ResponseEntity<DelegateTxResponse> createDelegateTx(
      @PathVariable("symbol") Symbol symbol,
      @RequestBody() StakingRequest stakingRequest) {

    return ResponseEntity.ok(
        stakingServiceFactory.getServiceBySymbol(symbol).createDelegateTx(stakingRequest));
  }

}
