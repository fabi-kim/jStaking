package com.springboot.staking.controller;

import com.springboot.staking.common.annotation.RedissonLock;
import com.springboot.staking.common.annotation.RequestIdHeader;
import com.springboot.staking.common.constant.Symbol;
import com.springboot.staking.data.dto.request.StakingRequest;
import com.springboot.staking.data.dto.response.DelegateTxResponse;
import com.springboot.staking.data.dto.response.StakingTxResponse;
import com.springboot.staking.data.dto.response.TransactionResponse;
import com.springboot.staking.service.StakingServiceFactory;
import com.springboot.staking.service.usecase.StakingUseCase;
import com.springboot.staking.service.worker.StakingWorker;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.UUID;
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
  private final StakingWorker stakingWorker;
  private final StakingUseCase useCase;


  @Operation(summary = "트랜잭션 생성")
  @PostMapping("/{symbol}/tx/delegate")
  public ResponseEntity<DelegateTxResponse> createDelegateTx(
      @PathVariable("symbol") Symbol symbol,
      @RequestBody() StakingRequest stakingRequest) {

    return ResponseEntity.ok(
        stakingServiceFactory.getServiceBySymbol(symbol).createDelegateTx(stakingRequest));
  }

  @Operation(summary = "위임 플로우")
  @PostMapping("/{symbol}/flow/delegate")
  public ResponseEntity<TransactionResponse> delegate(
      @RequestIdHeader UUID requestId,
      @PathVariable("symbol") Symbol symbol,
      @Valid @RequestBody StakingRequest stakingRequest) {

    var resp = useCase.delegate(requestId, symbol, stakingRequest);
    return ResponseEntity.ok(resp);
  }

  @RedissonLock(keys = {"#symbol"}, error = "이미 요청되었습니다.")
  @Operation(summary = "위임 요청 큐에 저장")
  @PostMapping("/{symbol}/worker/delegate")
  public ResponseEntity<StakingTxResponse> createWorkerJob(
      @RequestIdHeader UUID requestId,
      @PathVariable("symbol") Symbol symbol,
      @Valid @RequestBody StakingRequest stakingRequest) {

    var resp = stakingWorker.createWorkerJob(requestId, symbol, stakingRequest);
    return ResponseEntity.ok(resp);
  }

}
