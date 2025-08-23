package com.springboot.staking.service.worker.staking;

import com.springboot.staking.common.constant.Step;
import com.springboot.staking.common.constant.Symbol;
import com.springboot.staking.data.dao.StakingTxDao;
import com.springboot.staking.data.dto.request.StakingRequest;
import com.springboot.staking.data.entity.StakingTx;
import com.springboot.staking.service.StakingServiceFactory;
import com.springboot.staking.service.worker.StepHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class Staking00CreateHandler implements StepHandler {

  private final StakingServiceFactory stakingServiceFactory;
  private final StakingTxDao stakingTxDao;

  @Override
  public Step step() {
    return Step.CREATE;
  }

  @Override
  public StepResult process(StakingTx tx) {
    var symbol = Symbol.valueOf(tx.getProduct().getSymbol());
    var req = StakingRequest.of(tx.getDelegator(), tx.getValidator(), tx.getAmount());

    log.info("Creating delegate tx for requestId: {}", tx.getRequestId());

    var stakingService = stakingServiceFactory.getServiceBySymbol(symbol);
    var response = stakingService.createDelegateTx(req);

    // unsignedTx 저장
    stakingTxDao.updateUnsignedTx(tx.getId(), response.unsignedTx());

    log.info("Created delegate tx with unsignedTx length: {}", response.unsignedTx().length());

    return success();
  }
}