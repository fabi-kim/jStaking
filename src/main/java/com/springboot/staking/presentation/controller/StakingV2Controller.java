package com.springboot.staking.presentation.controller;

import com.springboot.staking.application.dto.command.CreateStakingTransactionCommand;
import com.springboot.staking.application.service.StakingWorkflowApplicationService;
import com.springboot.staking.common.annotation.RedissonLock;
import com.springboot.staking.common.annotation.RequestIdHeader;
import com.springboot.staking.common.constant.Symbol;
import com.springboot.staking.data.dto.request.StakingRequest;
import com.springboot.staking.data.dto.response.DelegateTxResponse;
import com.springboot.staking.data.dto.response.StakingTxResponse;
import com.springboot.staking.data.dto.response.TransactionResponse;
import com.springboot.staking.domain.shared.vo.RequestId;
import com.springboot.staking.domain.staking.model.StakingTransaction;
import com.springboot.staking.service.StakingServiceFactory;
import com.springboot.staking.service.usecase.StakingUseCase;
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
@RequestMapping("v2/staking")
public class StakingV2Controller {

  private final StakingServiceFactory stakingServiceFactory;
  private final StakingUseCase useCase;
  private final StakingWorkflowApplicationService stakingWorkflowService;

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
  @Operation(summary = "위임 요청 큐에 저장 (DDD 적용)")
  @PostMapping("/{symbol}/worker/delegate")
  public ResponseEntity<StakingTxResponse> createWorkerJobV2(
      @RequestIdHeader UUID requestId,
      @PathVariable("symbol") Symbol symbol,
      @Valid @RequestBody StakingRequest stakingRequest) {

    // DDD 구조로 처리
    // Command 패턴은 복잡한 요청을 객체로 캡슐화해서 코드의 가독성과 유지보수성을 크게 향상시킵니다!
    var command = CreateStakingTransactionCommand.of(
        RequestId.of(requestId),
        symbol,
        StakingTransaction.TxType.DELEGATE,
        stakingRequest.delegateAddress(),
        stakingRequest.validatorAddress(),
        stakingRequest.amount()
    );

    var response = stakingWorkflowService.createWorkerJob(command);

    return ResponseEntity.ok(response);
  }
}