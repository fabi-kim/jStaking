package com.springboot.staking.infrastructure.service.step;

import com.springboot.staking.application.dto.request.BroadcastRequest;
import com.springboot.staking.domain.node.service.NodeServiceFactory;
import com.springboot.staking.domain.staking.model.StakingTransaction;
import com.springboot.staking.domain.staking.service.StepProcessor;
import com.springboot.staking.infrastructure.persistence.StakingTransactionRepositoryImpl;
import com.springboot.staking.shared.constant.Step;
import com.springboot.staking.shared.constant.Symbol;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class BroadcastStepProcessor implements StepProcessor {

  private final NodeServiceFactory nodeServiceFactory;
  private final StakingTransactionRepositoryImpl repositoryImpl;

  @Override
  public Step getStep() {
    return Step.BROADCAST;
  }

  @Override
  @Transactional
  public void process(StakingTransaction transaction) {
    var symbol = Symbol.valueOf(transaction.getProduct().getSymbol());

    log.info("Broadcasting tx for requestId: {}", transaction.getRequestId());

    var nodeService = nodeServiceFactory.getServiceBySymbol(symbol);
    var broadcastRequest = BroadcastRequest.of(transaction.getSignedTx().value());
    var broadcastResponse = nodeService.broadcast(broadcastRequest);
    var txHash = broadcastResponse.txhash();

    // txHash 업데이트
    repositoryImpl.updateTxHash(transaction.getId(), txHash);

    log.info("Broadcasted tx for requestId: {}, txHash: {}",
        transaction.getRequestId(), txHash);
  }
}