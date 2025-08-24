package com.springboot.staking.application.service;

import com.springboot.staking.application.dto.command.CreateStakingTransactionCommand;
import com.springboot.staking.application.dto.request.StakingRequest;
import com.springboot.staking.application.dto.response.TransactionResponse;
import com.springboot.staking.shared.constant.Symbol;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StakingDelegateApplicationService {

  private final DelegateTxWorkflow workflow;
  //private final DelegateWorkflowStore store; // 포트(인터페이스)

  public TransactionResponse delegate(UUID requestId, Symbol symbol, StakingRequest request) {
    return workflow.run(requestId, symbol, request);
  }

  public TransactionResponse createAndProcessDelegate(CreateStakingTransactionCommand command) {
    // Command를 기존 파라미터로 변환하여 처리
    return workflow.run(
        command.requestId().value(),
        Symbol.valueOf(command.symbol().name()),
        StakingRequest.of(
            command.delegator(),
            command.validator(),
            command.amount()
        )
    );
  }
}