package com.springboot.staking.data.parser;

import com.springboot.staking.data.dto.response.TransactionResponse;

public interface TxParser<T> {

  TransactionResponse parse(T txResponse);
}
