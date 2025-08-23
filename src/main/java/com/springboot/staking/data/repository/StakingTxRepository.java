package com.springboot.staking.data.repository;

import com.springboot.staking.common.constant.Step;
import com.springboot.staking.data.entity.StakingTx;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

@Repository
public interface StakingTxRepository extends JpaRepository<StakingTx, Long> {

  Optional<StakingTx> findByRequestId(UUID requestId);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("""
        update StakingTx s
           set s.step      = :nextStep,
               s.status    = :nextStatus,
               s.unsignedTx= coalesce(:unsignedTx, s.unsignedTx),
               s.signedTx  = coalesce(:signedTx, s.signedTx),
               s.txHash    = coalesce(:txHash, s.txHash)
         where s.requestId        = :requestId
           and s.step      = :prevStep
           and s.status    = :prevStatus
      """)
  int advance(@Param("requestId") UUID requestId, @Param("prevStep") Step prevStep,
      @Param("prevStatus") StakingTx.Status prevStatus, @Param("nextStep") Step nextStep,
      @Param("nextStatus") StakingTx.Status nextStatus,
      @Param("unsignedTx") @Nullable String unsignedTx,
      @Param("signedTx") @Nullable String signedTx, @Param("txHash") @Nullable String txHash);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("""
        update StakingTx s
           set s.status    = :nextStatus
         where s.requestId = :requestId
           and s.step      = :prevStep
           and s.status    = :prevStatus
      """)
  int fail(@Param("requestId") UUID requestId,
      @Param("prevStep") Step prevStep,
      @Param("prevStatus") StakingTx.Status prevStatus,
      @Param("nextStatus") StakingTx.Status nextStatus);

  @Query(value = """
      select id
        from staking_tx
       where status = 'READY'
       order by id asc
       limit 1
      """, nativeQuery = true)
  Optional<Long> pickAnyReady();

  // 2) READY → PROCESSING (클레임, CAS)
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("""
        update StakingTx s
           set s.status = com.springboot.staking.data.entity.StakingTx.Status.IN_PROGRESS
         where s.id = :id
           and s.status = com.springboot.staking.data.entity.StakingTx.Status.READY
      """)
  int claim(@Param("id") Long id);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("""
        update StakingTx s
           set s.step = :nextStep,
               s.status = :nextStatus
         where s.id = :id
           and s.step = :currentStep
           and s.status = :currentStatus
      """)
  int updateStepWithCAS(@Param("id") Long id,
      @Param("currentStep") Step currentStep,
      @Param("currentStatus") StakingTx.Status currentStatus,
      @Param("nextStep") Step nextStep,
      @Param("nextStatus") StakingTx.Status nextStatus);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("UPDATE StakingTx s SET s.unsignedTx = :unsignedTx WHERE s.id = :id")
  int updateUnsignedTx(@Param("id") Long id, @Param("unsignedTx") String unsignedTx);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("UPDATE StakingTx s SET s.signedTx = :signedTx WHERE s.id = :id")
  int updateSignedTx(@Param("id") Long id, @Param("signedTx") String signedTx);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("UPDATE StakingTx s SET s.txHash = :txHash WHERE s.id = :id")
  int updateTxHash(@Param("id") Long id, @Param("txHash") String txHash);
}
