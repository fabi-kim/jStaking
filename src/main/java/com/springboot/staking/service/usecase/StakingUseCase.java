package com.springboot.staking.service.usecase;

import com.springboot.staking.common.constant.Symbol;
import com.springboot.staking.data.dto.request.StakingRequest;
import com.springboot.staking.data.dto.response.TransactionResponse;
import com.springboot.staking.service.flow.DelegateTxWorkflow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StakingUseCase {

  private final DelegateTxWorkflow workflow;
  //private final DelegateWorkflowStore store; // 포트(인터페이스)

  public TransactionResponse delegate(Symbol symbol, StakingRequest request) {
    return workflow.run(symbol, request);
  }
}