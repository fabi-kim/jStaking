package com.springboot.staking.domain.staking.model;

import com.springboot.staking.domain.shared.vo.Address;
import com.springboot.staking.domain.shared.vo.Amount;
import com.springboot.staking.domain.shared.vo.RequestId;
import com.springboot.staking.domain.staking.vo.ExtraData;
import com.springboot.staking.domain.staking.vo.StakingTransactionId;
import com.springboot.staking.domain.staking.vo.TransactionData;
import com.springboot.staking.shared.constant.Step;
import java.time.LocalDateTime;
import java.util.Objects;

public class StakingTransaction {

  private final StakingTransactionId id;
  private final RequestId requestId;
  private final TxType txType;
  private final Address delegator;
  private final Address validator;
  private final Amount amount;
  private final ProductInfo product;
  private final LocalDateTime createdAt;
  private TransactionData unsignedTx;
  private TransactionData signedTx;
  private String txHash;
  private ExtraData extraData;
  private Step step;
  private TransactionStatus status;
  private LocalDateTime updatedAt;

  // Constructor for creation (without ID)
  public StakingTransaction(
      RequestId requestId,
      TxType txType,
      Address delegator,
      Address validator,
      Amount amount,
      ProductInfo product) {

    this.id = null; // Will be assigned by infrastructure
    this.requestId = Objects.requireNonNull(requestId);
    this.txType = Objects.requireNonNull(txType);
    this.delegator = Objects.requireNonNull(delegator);
    this.validator = Objects.requireNonNull(validator);
    this.amount = Objects.requireNonNull(amount);
    this.product = Objects.requireNonNull(product);

    this.unsignedTx = TransactionData.empty();
    this.signedTx = TransactionData.empty();
    this.extraData = ExtraData.empty();

    this.step = Step.CREATE;
    this.status = TransactionStatus.READY;

    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  // Constructor for reconstruction (with ID)
  public StakingTransaction(
      StakingTransactionId id,
      RequestId requestId,
      TxType txType,
      Address delegator,
      Address validator,
      Amount amount,
      ProductInfo product,
      TransactionData unsignedTx,
      TransactionData signedTx,
      String txHash,
      ExtraData extraData,
      Step step,
      TransactionStatus status,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {

    this.id = Objects.requireNonNull(id);
    this.requestId = Objects.requireNonNull(requestId);
    this.txType = Objects.requireNonNull(txType);
    this.delegator = Objects.requireNonNull(delegator);
    this.validator = Objects.requireNonNull(validator);
    this.amount = Objects.requireNonNull(amount);
    this.product = Objects.requireNonNull(product);
    this.unsignedTx = unsignedTx;
    this.signedTx = signedTx;
    this.txHash = txHash;
    this.extraData = extraData;
    this.step = Objects.requireNonNull(step);
    this.status = Objects.requireNonNull(status);
    this.createdAt = Objects.requireNonNull(createdAt);
    this.updatedAt = Objects.requireNonNull(updatedAt);
  }

  // Domain behavior methods
  public boolean canTransitionTo(Step nextStep) {
    return step.canTransitionTo(nextStep);
  }

  public void processToNextStep(Step nextStep) {
    if (!canTransitionTo(nextStep)) {
      throw new IllegalStateException(
          String.format("Invalid transition from %s to %s", step, nextStep));
    }
    this.step = nextStep;
    this.status = TransactionStatus.READY;
    this.updatedAt = LocalDateTime.now();
  }

  public void markAsInProgress() {
    this.status = TransactionStatus.IN_PROGRESS;
    this.updatedAt = LocalDateTime.now();
  }

  public void markAsCompleted() {
    this.status = TransactionStatus.CONFIRMED;
    this.updatedAt = LocalDateTime.now();
  }

  public void markAsFailed() {
    this.status = TransactionStatus.FAILED;
    this.updatedAt = LocalDateTime.now();
  }

  public void updateUnsignedTx(String unsignedTx) {
    this.unsignedTx = TransactionData.of(unsignedTx);
    this.updatedAt = LocalDateTime.now();
  }

  public void updateSignedTx(String signedTx) {
    this.signedTx = TransactionData.of(signedTx);
    this.updatedAt = LocalDateTime.now();
  }

  public void updateTxHash(String txHash) {
    this.txHash = txHash;
    this.updatedAt = LocalDateTime.now();
  }

  public void updateExtraData(String extraData) {
    this.extraData = ExtraData.of(extraData);
    this.updatedAt = LocalDateTime.now();
  }

  public void updateUnsignedTxAndExtraData(String unsignedTx, String extraData) {
    this.unsignedTx = TransactionData.of(unsignedTx);
    this.extraData = ExtraData.of(extraData);
    this.updatedAt = LocalDateTime.now();
  }

  public boolean isReady() {
    return status == TransactionStatus.READY;
  }

  public boolean isInProgress() {
    return status == TransactionStatus.IN_PROGRESS;
  }

  public boolean isCompleted() {
    return status == TransactionStatus.CONFIRMED;
  }

  public boolean isFailed() {
    return status == TransactionStatus.FAILED;
  }

  // Getters
  public StakingTransactionId getId() {
    return id;
  }

  public RequestId getRequestId() {
    return requestId;
  }

  public TxType getTxType() {
    return txType;
  }

  public Address getDelegator() {
    return delegator;
  }

  public Address getValidator() {
    return validator;
  }

  public Amount getAmount() {
    return amount;
  }

  public ProductInfo getProduct() {
    return product;
  }

  public TransactionData getUnsignedTx() {
    return unsignedTx;
  }

  public TransactionData getSignedTx() {
    return signedTx;
  }

  public String getTxHash() {
    return txHash;
  }

  public ExtraData getExtraData() {
    return extraData;
  }

  public Step getStep() {
    return step;
  }

  public TransactionStatus getStatus() {
    return status;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public enum TxType {
    DELEGATE, UNDELEGATE, REDELEGATE
  }

  public enum TransactionStatus {
    READY, CONFIRMED, IN_PROGRESS, FAILED
  }

  public static class ProductInfo {

    private final Long id;
    private final String symbol;
    private final String productType;

    public ProductInfo(Long id, String symbol, String productType) {
      this.id = Objects.requireNonNull(id);
      this.symbol = Objects.requireNonNull(symbol);
      this.productType = Objects.requireNonNull(productType);
    }

    // Getters
    public Long getId() {
      return id;
    }

    public String getSymbol() {
      return symbol;
    }

    public String getProductType() {
      return productType;
    }
  }
}