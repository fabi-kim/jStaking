package com.springboot.staking.service.worker;

import com.springboot.staking.common.constant.Symbol;
import com.springboot.staking.data.dao.StakingTxDao;
import com.springboot.staking.data.dto.request.StakingRequest;
import com.springboot.staking.data.dto.response.StakingTxResponse;
import com.springboot.staking.data.entity.StakingTx;
import com.springboot.staking.data.mapper.StakingTxMapper;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class StakingWorker {

  private final StakingStateMachine stakingStateMachine;
  private final StakingTxDao stakingTxDao;
  private final StakingTxMapper stakingTxMapper;

  public StakingTxResponse createWorkerJob(UUID requestId, Symbol symbol,
      StakingRequest stakingRequest) {
    var tx = stakingTxDao.createReady(requestId, symbol, StakingTx.TxType.DELEGATE,
        stakingRequest.delegateAddress(), stakingRequest.validatorAddress(),
        stakingRequest.amount());

    log.info("[createWorkerJob] tx: {}", tx);

    return stakingTxMapper.toDto(tx);

  }

  @Scheduled(fixedDelayString = "${staking.createWorker.delay:3000}")
  public void tick() {
    log.info("tick!");
    stakingTxDao.pickAnyReady().ifPresent(this::processOne);
  }

  protected void processOne(Long id) {
    try {
      stakingStateMachine.processTransaction(id);
    } catch (Exception e) {
      log.error("Failed to process transaction {}: {}", id, e.getMessage(), e);
    }
  }
}
