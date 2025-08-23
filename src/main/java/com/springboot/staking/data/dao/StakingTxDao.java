package com.springboot.staking.data.dao;

import com.springboot.staking.common.constant.ErrorCode;
import com.springboot.staking.common.constant.ProductType;
import com.springboot.staking.common.constant.Step;
import com.springboot.staking.common.constant.Symbol;
import com.springboot.staking.common.exception.ApplicationException;
import com.springboot.staking.data.entity.StakingTx;
import com.springboot.staking.data.entity.StakingTx.Status;
import com.springboot.staking.data.repository.ProductRepository;
import com.springboot.staking.data.repository.StakingTxRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class StakingTxDao {

  private final StakingTxRepository repository;
  private final ProductRepository productRepository;


  @Transactional(readOnly = true)
  public StakingTx findById(Long id) {
    return repository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("TX not found: " + id));
  }

  @Transactional(readOnly = true)
  public Optional<Long> pickAnyReady() {
    return repository.pickAnyReady();
  }


  /**
   * 최초 생성: CREATE/READY
   */
  @Transactional
  public StakingTx createReady(
      UUID requestId,
      Symbol symbol,
      StakingTx.TxType txType,
      String delegator,
      String validator,
      String amount) {

    var product = productRepository.findFirstBySymbolAndProductType(symbol.toString(),
        ProductType.STAKING).orElseThrow(
        () -> new ApplicationException(ErrorCode.PRODUCT_NOT_FOUND, "product를 확인하세요."));

    var e = new StakingTx();
    e.setRequestId(requestId);
    e.setTxType(txType);
    e.setDelegator(delegator);
    e.setValidator(validator);
    e.setAmount(amount);
    e.setStep(Step.CREATE);
    e.setProduct(productRepository.getReferenceById(
        product.getId())); // SELECT 안 함    e.setStep(StakingTx.Step.CREATE);
    e.setStatus(Status.READY);
    return repository.save(e);
  }

  @Transactional
  public boolean claimForProcessing(Long txId, Step currentStep) {
    int updatedRows = repository.updateStepWithCAS(
        txId, currentStep, Status.READY, currentStep, Status.IN_PROGRESS);

    if (updatedRows == 1) {
      log.info("Claimed tx {} for processing at step {}", txId, currentStep);
      return true;
    } else {
      log.debug("Failed to claim tx {} - already processing or modified", txId);
      return false;
    }
  }

  @Transactional
  public void completeStep(Long txId, Step currentStep, Step nextStep, Status nextStatus) {
    if (!currentStep.canTransitionTo(nextStep)) {
      throw new IllegalStateException(
          String.format("Invalid transition from %s to %s for tx %s",
              currentStep, nextStep, txId));
    }

    int updatedRows = repository.updateStepWithCAS(
        txId, currentStep, Status.IN_PROGRESS, nextStep, nextStatus);

    if (updatedRows == 0) {
      throw new ApplicationException(ErrorCode.LOCK_ERROR,
          String.format("Failed to complete step for tx %s", txId));
    }

    log.info("Completed step {} → {} for tx {}", currentStep, nextStep, txId);
  }

  @Transactional
  public void markAsFailed(Long txId, Step currentStep) {
    int updatedRows = repository.updateStepWithCAS(
        txId, currentStep, Status.IN_PROGRESS, currentStep, Status.FAILED);

    if (updatedRows == 1) {
      log.error("Marked tx {} as failed at step {}", txId, currentStep);
    } else {
      log.error("Failed to mark tx {} as failed due to concurrent modification", txId);
    }
  }

  @Transactional
  public void updateUnsignedTx(Long txId, String unsignedTx) {
    repository.updateUnsignedTx(txId, unsignedTx);
    log.debug("Updated unsignedTx for tx {}", txId);
  }

  @Transactional
  public void updateSignedTx(Long txId, String signedTx) {
    repository.updateSignedTx(txId, signedTx);
    log.debug("Updated signedTx for tx {}", txId);
  }

  @Transactional
  public void updateTxHash(Long txId, String txHash) {
    repository.updateTxHash(txId, txHash);
    log.debug("Updated txHash for tx {}", txId);
  }
}