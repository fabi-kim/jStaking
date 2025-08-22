package com.springboot.staking.service.flow;

import com.springboot.staking.common.constant.Symbol;
import java.util.UUID;

public abstract class TxWorkflowTemplate<Request, Unsigned, Signed, Broadcasted, Final> {

  public final Final run(UUID requestId, Symbol symbol, Request request) {
    Unsigned unsignedTx = create(requestId, symbol, request);
    Signed signedTx = sign(requestId, symbol, unsignedTx);
    Broadcasted br = broadcast(requestId, symbol, signedTx);
    return confirmed(requestId, symbol, br);
  }

  protected abstract Unsigned create(UUID requestId, Symbol symbol, Request request);

  protected abstract Signed sign(UUID requestId, Symbol symbol, Unsigned unsignedTx);

  protected abstract Broadcasted broadcast(UUID requestId, Symbol symbol, Signed signedTx);

  protected abstract Final confirmed(UUID requestId, Symbol symbol, Broadcasted br);

  // 훅: 공통 로깅/리트라이/메트릭을 여기서 감싸도 됨
}