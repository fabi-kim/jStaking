package com.springboot.staking.service.flow;

import com.springboot.staking.common.constant.ErrorCode;
import com.springboot.staking.common.constant.ProductType;
import com.springboot.staking.common.constant.Symbol;
import com.springboot.staking.common.exception.ApplicationException;
import com.springboot.staking.data.entity.StakingTx;
import com.springboot.staking.data.entity.StakingTx.Status;
import com.springboot.staking.data.entity.StakingTx.Step;
import com.springboot.staking.data.repository.ProductRepository;
import com.springboot.staking.data.repository.StakingTxRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StakingTxStore {

  private final StakingTxRepository repo;
  private final ProductRepository productRepository;

  private static void conflict(UUID requestId) {
    throw new ObjectOptimisticLockingFailureException(StakingTx.class, requestId);
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
    e.setStatus(Status.IN_PROGRESS);
    return repo.save(e);
  }

  /**
   * createDelegateTx 완료 → SIGN/IN_PROGRESS + unsignedTx 저장
   */
  @Transactional
  public void afterCreated(UUID requestId, String unsignedRawTx) {
    int n = repo.advance(requestId,
        Step.CREATE, Status.READY,
        Step.SIGN, Status.READY,
        unsignedRawTx, null, null);
    if (n == 0) {
      conflict(requestId);
    }
  }

  @Transactional
  public void beforeSign(UUID requestId) {
    int n = repo.advance(requestId,
        Step.SIGN, Status.READY,
        Step.SIGN, Status.IN_PROGRESS,
        null, null, null);
    if (n == 0) {
      conflict(requestId);
    }
  }

  /**
   * sign 완료 → BROADCAST/IN_PROGRESS + signedTx 저장
   */
  @Transactional
  public void afterSigned(UUID requestId, String signedRawTx) {
    int n = repo.advance(requestId,
        Step.SIGN, Status.IN_PROGRESS,
        Step.BROADCAST, Status.READY,
        null, signedRawTx, null);
    if (n == 0) {
      conflict(requestId);
    }
  }

  @Transactional
  public void beforeBroadcast(UUID requestId) {
    int n = repo.advance(requestId,
        Step.BROADCAST, Status.READY,
        Step.BROADCAST, Status.IN_PROGRESS,
        null, null, null);
    if (n == 0) {
      conflict(requestId);
    }
  }

  /**
   * broadcast 완료 → CONFIRM/IN_PROGRESS + txHash 저장
   */
  @Transactional
  public void afterBroadcasted(UUID requestId, String txHash) {
    int n = repo.advance(requestId,
        Step.BROADCAST, Status.IN_PROGRESS,
        Step.CONFIRM, Status.READY,
        null, null, txHash);
    if (n == 0) {
      conflict(requestId);
    }
  }

  @Transactional
  public void beforeConfirm(UUID requestId) {
    int n = repo.advance(requestId,
        Step.CONFIRM, Status.READY,
        Step.CONFIRM, Status.IN_PROGRESS,
        null, null, null);
    if (n == 0) {
      conflict(requestId);
    }
  }

  /**
   * 최종 확인 → CONFIRM/CONFIRMED (txHash 유지)
   */
  @Transactional
  public void afterConfirmed(UUID requestId) {
    int n = repo.advance(requestId,
        Step.CONFIRM, Status.IN_PROGRESS,
        Step.CONFIRM, Status.CONFIRMED,
        null, null, null);
    if (n == 0) {
      conflict(requestId);
    }
  }

  @Transactional
  public void failed(UUID requestId, Step step) {
    int n = repo.fail(requestId,
        step,
        Status.IN_PROGRESS,
        Status.FAILED);
    if (n == 0) {
      // 다른 워커가 이미 전이했거나 상태가 바뀜 → 덮어쓰기 방지
      log.warn("fail skip - stale state");
    }
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public Optional<Long> claim() {
    return repo.pickAnyReady();
  }
}