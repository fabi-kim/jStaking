package com.springboot.staking.domain.staking.service;

import com.springboot.staking.shared.constant.Symbol;

public interface SignRequestBuilderFactory {

  SignRequestBuilder getBuilder(Symbol symbol);
}