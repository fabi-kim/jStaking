package com.springboot.staking.service.worker.staking;

import com.springboot.staking.common.constant.Step;
import com.springboot.staking.common.constant.Symbol;
import com.springboot.staking.data.dao.StakingTxDao;
import com.springboot.staking.data.dto.request.BroadcastRequest;
import com.springboot.staking.data.entity.StakingTx;
import com.springboot.staking.service.NodeServiceFactory;
import com.springboot.staking.service.worker.StepHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class Staking02BroadcastHandler implements StepHandler {

  private final NodeServiceFactory nodeServiceFactory;
  private final StakingTxDao stakingTxDao;

  @Override
  public Step step() {
    return Step.BROADCAST;
  }

  @Override
  public StepResult process(StakingTx tx) {
    var symbol = Symbol.valueOf(tx.getProduct().getSymbol());
    var signedTx = tx.getSignedTx();

    log.info("Broadcasting tx for requestId: {}", tx.getRequestId());

    var nodeService = nodeServiceFactory.getServiceBySymbol(symbol);
    var response = nodeService.broadcast(BroadcastRequest.of(signedTx));

    // txHash 저장
    stakingTxDao.updateTxHash(tx.getId(), response.txhash());

    log.info("Broadcasted tx with hash: {} for requestId: {}", response.txhash(),
        tx.getRequestId());

    return success();
  }
}