package com.springboot.staking.service.worker;

import com.springboot.staking.data.repository.StakingTxRepository;
import com.springboot.staking.service.flow.StakingTxStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class StakingWorker {

  private final StakingTxRepository stakingTxRepository;
  private final StepHandlerFactory stepHandlerFactory;
  private final StakingTxStore stakingTxStore;

  @Scheduled(fixedDelayString = "${staking.createWorker.delay:3000}")
  public void tick() {
    log.info("tick!");
    stakingTxStore.claim().ifPresent(this::processOne);
  }

  /**
   * 1) 클레임: 아주 짧은 TX로 id만 들고 나오기
   */

  protected void processOne(Long id) {
    var tx = stakingTxRepository.findById(id).orElseThrow();
    var handler = stepHandlerFactory.getHandler(tx.getStep());
    try {
      handler.process(tx);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }
}
