package com.springboot.staking.infrastructure.service.step;

import com.springboot.staking.domain.node.service.NodeServiceFactory;
import com.springboot.staking.domain.staking.model.StakingTransaction;
import com.springboot.staking.domain.staking.service.SignRequestBuilderDomainService;
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
public class SignStepProcessor implements StepProcessor {

  private final NodeServiceFactory nodeServiceFactory;
  private final SignRequestBuilderDomainService signRequestBuilderService;
  private final StakingTransactionRepositoryImpl repositoryImpl;

  @Override
  public Step getStep() {
    return Step.SIGN;
  }

  @Override
  @Transactional
  public void process(StakingTransaction transaction) {
    var symbol = Symbol.valueOf(transaction.getProduct().getSymbol());

    log.info("Signing tx for requestId: {}", transaction.getRequestId());

    var signRequest = signRequestBuilderService.buildSignRequest(transaction, symbol);
    var nodeService = nodeServiceFactory.getServiceBySymbol(symbol);
    var signedTx = nodeService.sign(signRequest);

    // signedTx 업데이트
    repositoryImpl.updateSignedTx(transaction.getId(), signedTx);

    log.info("Signed tx for requestId: {}", transaction.getRequestId());
  }
}