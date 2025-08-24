package com.springboot.staking.infrastructure.persistence;

import com.springboot.staking.common.constant.Step;
import com.springboot.staking.data.entity.Product;
import com.springboot.staking.data.entity.StakingTx;
import com.springboot.staking.domain.shared.vo.Address;
import com.springboot.staking.domain.shared.vo.Amount;
import com.springboot.staking.domain.shared.vo.RequestId;
import com.springboot.staking.domain.staking.model.StakingTransaction;
import com.springboot.staking.domain.staking.repository.StakingTransactionRepository;
import com.springboot.staking.domain.staking.vo.ExtraData;
import com.springboot.staking.domain.staking.vo.StakingTransactionId;
import com.springboot.staking.domain.staking.vo.TransactionData;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class StakingTransactionRepositoryImpl implements StakingTransactionRepository {

  private final StakingTransactionJpaRepository jpaRepository;

  @Override
  @Transactional
  public StakingTransaction save(StakingTransaction transaction) {
    StakingTx entity = toEntity(transaction);
    StakingTx saved = jpaRepository.save(entity);
    return toDomain(saved);
  }
  
  // 기존 DAO 메서드들을 Repository로 이관
  @Transactional
  public void updateUnsignedTxAndExtraData(StakingTransactionId id, String unsignedTx, String extraData) {
    jpaRepository.updateUnsignedTxAndExtraData(id.value(), unsignedTx, extraData);
  }
  
  @Transactional
  public void updateSignedTx(StakingTransactionId id, String signedTx) {
    jpaRepository.updateSignedTx(id.value(), signedTx);
  }
  
  @Transactional
  public void updateTxHash(StakingTransactionId id, String txHash) {
    jpaRepository.updateTxHash(id.value(), txHash);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<StakingTransaction> findById(StakingTransactionId id) {
    return jpaRepository.findById(id.value())
        .map(this::toDomain);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<StakingTransaction> findByRequestId(RequestId requestId) {
    return jpaRepository.findByRequestId(requestId.value())
        .map(this::toDomain);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<StakingTransactionId> findAnyReadyTransactionId() {
    return jpaRepository.pickAnyReady()
        .map(StakingTransactionId::of);
  }

  @Override
  @Transactional
  public boolean claimForProcessing(StakingTransactionId id, Step currentStep) {
    int updatedRows = jpaRepository.updateStepWithCAS(
        id.value(), 
        currentStep, 
        StakingTx.Status.READY, 
        currentStep, 
        StakingTx.Status.IN_PROGRESS
    );
    return updatedRows == 1;
  }

  @Override
  @Transactional
  public void updateStep(StakingTransactionId id, Step currentStep, Step nextStep, 
                        StakingTransaction.TransactionStatus nextStatus) {
    StakingTx.Status entityStatus = toEntityStatus(nextStatus);
    
    int updatedRows = jpaRepository.updateStepWithCAS(
        id.value(),
        currentStep,
        StakingTx.Status.IN_PROGRESS,
        nextStep,
        entityStatus
    );
    
    if (updatedRows == 0) {
      throw new IllegalStateException(
          String.format("Failed to update step for transaction %s", id));
    }
  }

  private StakingTransaction toDomain(StakingTx entity) {
    return new StakingTransaction(
        StakingTransactionId.of(entity.getId()),
        RequestId.of(entity.getRequestId()),
        StakingTransaction.TxType.valueOf(entity.getTxType().name()),
        Address.of(entity.getDelegator()),
        Address.of(entity.getValidator()),
        Amount.of(entity.getAmount()),
        new StakingTransaction.ProductInfo(
            entity.getProduct().getId(),
            entity.getProduct().getSymbol(),
            entity.getProduct().getProductType().name()
        ),
        TransactionData.of(entity.getUnsignedTx() != null ? entity.getUnsignedTx() : ""),
        TransactionData.of(entity.getSignedTx() != null ? entity.getSignedTx() : ""),
        entity.getTxHash(),
        ExtraData.of(entity.getExtraData()),
        entity.getStep(),
        toDomainStatus(entity.getStatus()),
        entity.getCreatedAt(),
        entity.getUpdatedAt()
    );
  }

  private StakingTx toEntity(StakingTransaction domain) {
    StakingTx entity = new StakingTx();
    
    if (domain.getId() != null) {
      entity.setId(domain.getId().value());
    }
    
    entity.setRequestId(domain.getRequestId().value());
    entity.setTxType(StakingTx.TxType.valueOf(domain.getTxType().name()));
    entity.setDelegator(domain.getDelegator().value());
    entity.setValidator(domain.getValidator().value());
    entity.setAmount(domain.getAmount().value().toString());
    
    // Product 설정 (실제로는 ProductRepository에서 조회해야 함)
    Product product = new Product();
    product.setId(domain.getProduct().getId());
    entity.setProduct(product);
    
    entity.setUnsignedTx(domain.getUnsignedTx().value());
    entity.setSignedTx(domain.getSignedTx().value());
    entity.setTxHash(domain.getTxHash());
    entity.setExtraData(domain.getExtraData().value());
    entity.setStep(domain.getStep());
    entity.setStatus(toEntityStatus(domain.getStatus()));
    
    return entity;
  }

  private StakingTransaction.TransactionStatus toDomainStatus(StakingTx.Status status) {
    return switch (status) {
      case READY -> StakingTransaction.TransactionStatus.READY;
      case CONFIRMED -> StakingTransaction.TransactionStatus.CONFIRMED;
      case IN_PROGRESS -> StakingTransaction.TransactionStatus.IN_PROGRESS;
      case FAILED -> StakingTransaction.TransactionStatus.FAILED;
    };
  }

  private StakingTx.Status toEntityStatus(StakingTransaction.TransactionStatus status) {
    return switch (status) {
      case READY -> StakingTx.Status.READY;
      case CONFIRMED -> StakingTx.Status.CONFIRMED;
      case IN_PROGRESS -> StakingTx.Status.IN_PROGRESS;
      case FAILED -> StakingTx.Status.FAILED;
    };
  }
}