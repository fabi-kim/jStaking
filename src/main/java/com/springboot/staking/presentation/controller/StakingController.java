package com.springboot.staking.presentation.controller;

import com.springboot.staking.application.dto.command.CreateStakingTransactionCommand;
import com.springboot.staking.application.dto.request.StakingRequest;
import com.springboot.staking.application.dto.response.DelegateTxResponse;
import com.springboot.staking.application.dto.response.StakingTxResponse;
import com.springboot.staking.application.dto.response.TransactionResponse;
import com.springboot.staking.application.service.StakingDelegateApplicationService;
import com.springboot.staking.application.service.StakingWorkflowApplicationService;
import com.springboot.staking.domain.shared.vo.RequestId;
import com.springboot.staking.domain.staking.model.StakingTransaction;
import com.springboot.staking.domain.staking.service.StakingServiceFactory;
import com.springboot.staking.shared.annotation.RedissonLock;
import com.springboot.staking.shared.annotation.RequestIdHeader;
import com.springboot.staking.shared.constant.Symbol;
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
  private final StakingDelegateApplicationService delegateService;
  private final StakingWorkflowApplicationService workflowService;


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

    var resp = delegateService.delegate(requestId, symbol, stakingRequest);
    return ResponseEntity.ok(resp);
  }

  @RedissonLock(keys = {"#symbol"}, error = "이미 요청되었습니다.")
  @Operation(summary = "위임 요청 큐에 저장")
  @PostMapping("/{symbol}/worker/delegate")
  public ResponseEntity<StakingTxResponse> createWorkerJob(
      @RequestIdHeader UUID requestId,
      @PathVariable("symbol") Symbol symbol,
      @Valid @RequestBody StakingRequest stakingRequest) {

    // DDD Command 방식으로 변경
    var command = CreateStakingTransactionCommand.of(
        RequestId.of(requestId),
        symbol,
        StakingTransaction.TxType.DELEGATE,
        stakingRequest.delegateAddress(),
        stakingRequest.validatorAddress(),
        stakingRequest.amount()
    );

    var resp = workflowService.createWorkerJob(command);
    return ResponseEntity.ok(resp);
  }

}
