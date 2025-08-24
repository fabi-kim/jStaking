package com.springboot.staking.infrastructure.parser;

import com.springboot.staking.application.dto.response.TransactionResponse;

public interface TxParser<T> {

  TransactionResponse parse(T txResponse);
}
