package com.springboot.staking.application.service;

import com.springboot.staking.application.dto.command.CreateStakingTransactionCommand;
import com.springboot.staking.application.dto.command.ProcessStakingTransactionCommand;
import com.springboot.staking.common.constant.ProductType;
import com.springboot.staking.common.constant.Step;
import com.springboot.staking.common.constant.Symbol;
import com.springboot.staking.common.exception.ApplicationException;
import com.springboot.staking.common.constant.ErrorCode;
import com.springboot.staking.data.repository.ProductRepository;
import com.springboot.staking.domain.shared.vo.Address;
import com.springboot.staking.domain.shared.vo.Amount;
import com.springboot.staking.domain.staking.model.StakingTransaction;
import com.springboot.staking.domain.staking.repository.StakingTransactionRepository;
import com.springboot.staking.domain.staking.service.StakingProcessorDomainService;
import com.springboot.staking.domain.staking.service.StepProcessorService;
import com.springboot.staking.domain.staking.vo.StakingTransactionId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StakingApplicationService {

  private final StakingTransactionRepository stakingTransactionRepository;
  private final StakingProcessorDomainService processorDomainService;
  private final StepProcessorService stepProcessorService;
  private final ProductRepository productRepository; // 임시로 기존 리포지토리 사용

  @Transactional
  public StakingTransaction createStakingTransaction(CreateStakingTransactionCommand command) {
    
    // Product 조회 (임시적으로 기존 방식 사용)
    var product = productRepository.findFirstBySymbolAndProductType(
            command.symbol().toString(), ProductType.STAKING)
        .orElseThrow(() -> new ApplicationException(ErrorCode.PRODUCT_NOT_FOUND, "product를 확인하세요."));

    // 도메인 모델 생성
    var transaction = new StakingTransaction(
        command.requestId(),
        command.txType(),
        Address.of(command.delegator()),
        Address.of(command.validator()),
        Amount.of(command.amount()),
        new StakingTransaction.ProductInfo(
            product.getId(),
            product.getSymbol(),
            product.getProductType().name()
        )
    );

    // 저장
    StakingTransaction saved = stakingTransactionRepository.save(transaction);
    
    log.info("Created staking transaction for requestId: {}", command.requestId());
    
    return saved;
  }

  @Transactional
  public void processStakingTransaction(ProcessStakingTransactionCommand command) {
    
    // 트랜잭션 클레임 시도
    boolean claimed = processorDomainService.claimTransaction(
        command.transactionId(), command.step());
    
    if (!claimed) {
      log.debug("Failed to claim transaction {} for step {}", 
                command.transactionId(), command.step());
      return;
    }

    try {
      // 실제 처리 로직 수행
      executeStepProcessing(command.transactionId(), command.step());
      
      // 다음 단계로 진행
      StakingTransaction transaction = stakingTransactionRepository
          .findById(command.transactionId())
          .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
      
      Step nextStep = determineNextStep(command.step());
      processorDomainService.completeStep(transaction, nextStep);
      
    } catch (Exception e) {
      log.error("Failed to process transaction {} at step {}", 
                command.transactionId(), command.step(), e);
      
      StakingTransaction transaction = stakingTransactionRepository
          .findById(command.transactionId())
          .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
      
      processorDomainService.markAsFailed(transaction);
      throw e;
    }
  }

  @Transactional(readOnly = true)
  public StakingTransactionId findAnyReadyTransaction() {
    return stakingTransactionRepository.findAnyReadyTransactionId()
        .orElse(null);
  }

  private void executeStepProcessing(StakingTransactionId transactionId, Step step) {
    log.info("Executing step {} for transaction {}", step, transactionId);
    
    StakingTransaction transaction = stakingTransactionRepository.findById(transactionId)
        .orElseThrow(() -> new IllegalArgumentException("Transaction not found: " + transactionId));
    
    switch (step) {
      case CREATE -> stepProcessorService.processCreateStep(transaction);
      case SIGN -> stepProcessorService.processSignStep(transaction);
      case BROADCAST -> stepProcessorService.processBroadcastStep(transaction);
      case CONFIRM -> stepProcessorService.processConfirmStep(transaction);
    }
  }

  private Step determineNextStep(Step currentStep) {
    return stepProcessorService.getNextStep(currentStep);
  }
}