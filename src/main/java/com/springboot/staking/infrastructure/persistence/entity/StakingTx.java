package com.springboot.staking.infrastructure.persistence.entity;

import com.springboot.staking.shared.constant.Step;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "staking_tx")
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class StakingTx extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @EqualsAndHashCode.Include
  @Column(nullable = false, updatable = false, length = 64, unique = true)
  private UUID requestId;
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private TxType txType;
  private String delegator;
  private String validator;
  private String amount;

  //@Lob @Basic(fetch = LAZY)로 본문 지연 로딩.
  @Lob
  @Basic(fetch = FetchType.LAZY)
  private String unsignedTx;
  @Lob
  @Basic(fetch = FetchType.LAZY)
  private String signedTx;

  @Lob
  @Basic(fetch = FetchType.LAZY)
  private String extraData;

  private String txHash;


  /**
   * step+status를 “버전처럼” 쓰는 CAS(Compare-And-Set) 방식으로 충돌을 감지·차단 WHERE id = :id AND step = :prevStep
   * AND status = :prevStatus 영향 행 수(updatedRows)가 1이면 성공, 0이면 누군가 먼저 바꾼 것(충돌) → 재시도/중단.
   */
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 16)
  private Step step;      // CREATE, SIGN, BROADCAST, CONFIRM
  private Status status;       // 0: READY, 10: 진행중, 11: 완료, 12: 실패

  /**
   * @Version을 쓰는 진짜 낙관적 락이라면 save()도 안전
   */

  @ManyToOne(fetch = FetchType.EAGER, optional = false)  //EAGER는 N+1과 과도한 로딩 유발
  @JoinColumn(name = "product_id")
  private Product product;

  public enum Status {READY, CONFIRMED, IN_PROGRESS, FAILED}

  public enum TxType {DELEGATE, UNDELEGATE, REDELEGATE}
}


/*
create table staking.staking_tx
(


    id          bigint auto_increment
        primary key,
    request_id  varchar(64)                                     not null,
    product_id  bigint                                          not null,
    amount      BIGINT                                          null,
    delegator   varchar(255)                                    null,
    validator   varchar(255)                                    null,
    unsigned_tx longtext                                        null,
    signed_tx   longtext                                        null,
    tx_hash     varchar(255)                                    null,
    step        enum ('BROADCAST', 'CONFIRM', 'CREATE', 'SIGN') not null,
    tx_type     enum ('DELEGATE', 'REDELEGATE', 'UNDELEGATE')   not null,
    status      tinyint default -1                              not null,
    created_at  datetime(6)                                     null,
    updated_at  datetime(6)                                     null,
    constraint UKdne47a8l4hr9mamq2c32898br
        unique (request_id),
    constraint FKrmiwhwhmdon7t8p3yk21o1u96
        foreign key (product_id) references staking.product (id)
);

 */