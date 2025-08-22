package com.springboot.staking.data.repository;

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
  int advance(@Param("requestId") UUID requestId, @Param("prevStep") StakingTx.Step prevStep,
      @Param("prevStatus") StakingTx.Status prevStatus, @Param("nextStep") StakingTx.Step nextStep,
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
      @Param("prevStep") StakingTx.Step prevStep,
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
}
