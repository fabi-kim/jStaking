package com.springboot.staking.infrastructure.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.staking.common.constant.Step;
import com.springboot.staking.common.constant.Symbol;
import com.springboot.staking.data.dto.request.BroadcastRequest;
import com.springboot.staking.data.dto.request.StakingRequest;
import com.springboot.staking.domain.staking.model.StakingTransaction;
import com.springboot.staking.domain.staking.repository.StakingTransactionRepository;
import com.springboot.staking.infrastructure.persistence.StakingTransactionRepositoryImpl;
import com.springboot.staking.domain.staking.service.SignRequestBuilderDomainService;
import com.springboot.staking.domain.staking.service.StepProcessorService;
import com.springboot.staking.service.NodeServiceFactory;
import com.springboot.staking.service.StakingServiceFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StepProcessorServiceImpl implements StepProcessorService {

  private final StakingServiceFactory stakingServiceFactory;
  private final NodeServiceFactory nodeServiceFactory;
  private final SignRequestBuilderDomainService signRequestBuilderService;
  private final StakingTransactionRepository repository;
  private final StakingTransactionRepositoryImpl repositoryImpl;
  private final ObjectMapper objectMapper;

  @Override
  @Transactional
  public void processCreateStep(StakingTransaction transaction) {
    var symbol = Symbol.valueOf(transaction.getProduct().getSymbol());
    var stakingRequest = StakingRequest.of(
        transaction.getDelegator().value(),
        transaction.getValidator().value(),
        transaction.getAmount().toString()
    );

    log.info("Creating delegate tx for requestId: {}", transaction.getRequestId());

    var stakingService = stakingServiceFactory.getServiceBySymbol(symbol);
    var response = stakingService.createDelegateTx(stakingRequest);

    // unsignedTx와 extraData 업데이트
    String extraDataJson = null;
    if (response.options() != null) {
      try {
        extraDataJson = objectMapper.writeValueAsString(response.options());
      } catch (Exception e) {
        log.warn("Failed to serialize options to JSON for requestId: {}", 
                 transaction.getRequestId(), e);
      }
    }

    // Repository를 통해 직접 업데이트
    repositoryImpl.updateUnsignedTxAndExtraData(transaction.getId(), response.unsignedTx(), extraDataJson);

    log.info("Created delegate tx with unsignedTx length: {}", response.unsignedTx().length());
  }

  @Override
  @Transactional
  public void processSignStep(StakingTransaction transaction) {
    var symbol = Symbol.valueOf(transaction.getProduct().getSymbol());

    log.info("Signing tx for requestId: {}", transaction.getRequestId());

    var signRequest = signRequestBuilderService.buildSignRequest(transaction, symbol);
    var nodeService = nodeServiceFactory.getServiceBySymbol(symbol);
    var signedTx = nodeService.sign(signRequest);

    // signedTx 업데이트
    repositoryImpl.updateSignedTx(transaction.getId(), signedTx);

    log.info("Signed tx for requestId: {}", transaction.getRequestId());
  }

  @Override
  @Transactional
  public void processBroadcastStep(StakingTransaction transaction) {
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

  @Override
  @Transactional
  public void processConfirmStep(StakingTransaction transaction) {
    var symbol = Symbol.valueOf(transaction.getProduct().getSymbol());

    log.info("Confirming tx for requestId: {}", transaction.getRequestId());

    var nodeService = nodeServiceFactory.getServiceBySymbol(symbol);
    
    // 트랜잭션 상태 확인
    try {
      var txResponse = nodeService.getTx(transaction.getTxHash());
      if (txResponse != null) {
        log.info("Transaction confirmed for requestId: {}", transaction.getRequestId());
      } else {
        throw new RuntimeException("Transaction not found");
      }
    } catch (Exception e) {
      log.warn("Failed to confirm transaction for requestId: {}", transaction.getRequestId(), e);
      throw new RuntimeException("Transaction not confirmed yet", e);
    }
  }

  @Override
  public Step getNextStep(Step currentStep) {
    return switch (currentStep) {
      case CREATE -> Step.SIGN;
      case SIGN -> Step.BROADCAST;
      case BROADCAST -> Step.CONFIRM;
      case CONFIRM -> Step.CONFIRM; // 마지막 단계
    };
  }
}