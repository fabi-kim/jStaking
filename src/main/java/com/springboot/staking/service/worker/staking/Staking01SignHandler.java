package com.springboot.staking.service.worker.staking;

import com.springboot.staking.common.constant.Step;
import com.springboot.staking.common.constant.Symbol;
import com.springboot.staking.data.dao.StakingTxDao;
import com.springboot.staking.data.dto.request.SignRequest;
import com.springboot.staking.data.dto.response.DelegateTxResponse;
import com.springboot.staking.data.entity.StakingTx;
import com.springboot.staking.service.NodeServiceFactory;
import com.springboot.staking.service.worker.StepHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class Staking01SignHandler implements StepHandler {

  private final NodeServiceFactory nodeServiceFactory;
  private final StakingTxDao stakingTxDao;

  @Override
  public Step step() {
    return Step.SIGN;
  }

  @Override
  public StepResult process(StakingTx tx) {
    var symbol = Symbol.valueOf(tx.getProduct().getSymbol());

    log.info("Signing tx for requestId: {}", tx.getRequestId());

    DelegateTxResponse delegateTxResponse = DelegateTxResponse.of(tx.getUnsignedTx(), "120019",
        "15");

    var nodeService = nodeServiceFactory.getServiceBySymbol(symbol);
    var signedTx = nodeService.sign(SignRequest.from(delegateTxResponse));

    // signedTx 저장
    stakingTxDao.updateSignedTx(tx.getId(), signedTx);

    log.info("Signed tx for requestId: {}", tx.getRequestId());

    return success();
  }
}