package com.springboot.staking.infrastructure.service;

import com.springboot.staking.common.constant.Symbol;
import com.springboot.staking.data.dto.request.SignRequest;
import com.springboot.staking.domain.staking.model.StakingTransaction;
import com.springboot.staking.domain.staking.service.SignRequestBuilderDomainService;
import com.springboot.staking.service.builder.SignRequestBuilderFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignRequestBuilderDomainServiceImpl implements SignRequestBuilderDomainService {

  private final SignRequestBuilderFactory signRequestBuilderFactory;

  @Override
  public SignRequest buildSignRequest(StakingTransaction transaction, Symbol symbol) {
    var builder = signRequestBuilderFactory.getBuilder(symbol);
    
    // 도메인 모델을 기존 DTO로 변환 (임시적으로)
    var legacyTx = convertToLegacyEntity(transaction);
    
    return builder.buildSignRequest(legacyTx);
  }

  private com.springboot.staking.data.entity.StakingTx convertToLegacyEntity(StakingTransaction domain) {
    var entity = new com.springboot.staking.data.entity.StakingTx();
    entity.setId(domain.getId() != null ? domain.getId().value() : null);
    entity.setRequestId(domain.getRequestId().value());
    entity.setUnsignedTx(domain.getUnsignedTx().value());
    entity.setExtraData(domain.getExtraData().value());
    
    // Product 설정
    var product = new com.springboot.staking.data.entity.Product();
    product.setId(domain.getProduct().getId());
    product.setSymbol(domain.getProduct().getSymbol());
    entity.setProduct(product);
    
    return entity;
  }
}