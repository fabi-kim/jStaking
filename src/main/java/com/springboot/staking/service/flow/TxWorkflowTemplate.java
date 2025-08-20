package com.springboot.staking.service.flow;

import com.springboot.staking.common.constant.Symbol;

public abstract class TxWorkflowTemplate<RQ, Unsigned, Signed, Broadcasted, Final> {

  public final Final run(Symbol symbol, RQ request) {
    Unsigned unsignedTx = create(symbol, request);
    Signed signedTx = sign(symbol, unsignedTx);
    Broadcasted br = broadcast(symbol, signedTx);
    return confirmed(symbol, br);
  }

  protected abstract Unsigned create(Symbol symbol, RQ request);

  protected abstract Signed sign(Symbol symbol, Unsigned unsignedTx);

  protected abstract Broadcasted broadcast(Symbol symbol, Signed signedTx);

  protected abstract Final confirmed(Symbol symbol, Broadcasted br);

  // 훅: 공통 로깅/리트라이/메트릭을 여기서 감싸도 됨
}