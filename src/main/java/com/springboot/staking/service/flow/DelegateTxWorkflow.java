package com.springboot.staking.service.flow;

import com.springboot.staking.common.constant.Symbol;
import com.springboot.staking.data.dto.request.BroadcastRequest;
import com.springboot.staking.data.dto.request.SignRequest;
import com.springboot.staking.data.dto.request.StakingRequest;
import com.springboot.staking.data.dto.response.BroadcastResponse;
import com.springboot.staking.data.dto.response.DelegateTxResponse;
import com.springboot.staking.data.dto.response.TransactionResponse;
import com.springboot.staking.data.entity.StakingTx.Step;
import com.springboot.staking.service.NodeServiceFactory;
import com.springboot.staking.service.StakingServiceFactory;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class DelegateTxWorkflow extends
    TxWorkflowTemplate<StakingRequest, DelegateTxResponse, String, BroadcastResponse, TransactionResponse> {


  private final StakingServiceFactory stakingServices;
  private final NodeServiceFactory nodeServices;
  private final StakingTxStore txStore;

  @Override
  public DelegateTxResponse create(UUID requestId, Symbol symbol, StakingRequest req) {

    var staking = stakingServices.getServiceBySymbol(symbol);
/*

    txStore.createReady(requestId, symbol, StakingTx.TxType.DELEGATE,
        req.delegateAddress(), req.validatorAddress(), req.amount());
*/

    log.info("-> Create Delegate Tx Workflow");

    try {
      var response = staking.createDelegateTx(req);
      log.info("* Create Delegate Tx Response");

      txStore.afterCreated(requestId, response.unsignedTx());
      return response;
    } catch (Exception e) {
      log.error("-> Create Delegate Tx Workflow Exception", e);
      txStore.failed(requestId,
          Step.CREATE);
      throw e;
    }
  }

  @Override
  public String sign(UUID requestId, Symbol symbol, DelegateTxResponse delegateTxResponse) {

    var nodeService = nodeServices.getServiceBySymbol(symbol);
    txStore.beforeSign(requestId);

    log.info("--> Sign Delegate Tx Workflow");

    try {
      var response = nodeService.sign(SignRequest.from(delegateTxResponse));
      log.info("* Sign Delegate Tx Response");

      txStore.afterSigned(requestId, response);

      return response;
    } catch (Exception e) {
      log.error("-> Sign Delegate Tx Workflow Exception", e);
      txStore.failed(requestId, Step.SIGN);
      throw e;
    }

  }

  @Override
  public BroadcastResponse broadcast(UUID requestId, Symbol symbol, String signedRawTx) {
    var nodeService = nodeServices.getServiceBySymbol(symbol);

    txStore.beforeBroadcast(requestId);

    log.info("---> Broadcast Delegate Tx Workflow");

    try {
      var response = nodeService.broadcast(BroadcastRequest.of(signedRawTx));
      log.info("* Broadcast Delegate Tx Response");

      txStore.afterBroadcasted(requestId, response.txhash());

      return response;
    } catch (Exception e) {
      log.error("-> Broadcast Delegate Tx Workflow Exception", e);
      txStore.failed(requestId, Step.BROADCAST);
      throw e;
    }

  }

  @Override
  public TransactionResponse confirmed(UUID requestId, Symbol symbol,
      BroadcastResponse broadcastResponse) {
    var nodeService = nodeServices.getServiceBySymbol(symbol);

    txStore.beforeConfirm(requestId);

    log.info("----> Confirm Delegate Tx Workflow");

    try {
      var response = nodeService.getTx(broadcastResponse.txhash());
      log.info("* Confirm Delegate Tx Response");

      txStore.afterConfirmed(requestId);
      return response;
    } catch (Exception e) {
      log.error("-> Confirm Delegate Tx Workflow Exception", e);
      txStore.failed(requestId, Step.CONFIRM);
      throw e;
    }

  }
}