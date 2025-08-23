package com.springboot.staking.service.builder;

import com.springboot.staking.common.constant.Symbol;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignRequestBuilderFactory {

  private final List<SignRequestBuilder> signRequestBuilders;
  private final DefaultSignRequestBuilder defaultSignRequestBuilder;
  private final Map<Symbol, SignRequestBuilder> builderMap = new HashMap<>();

  @PostConstruct
  public void init() {
    signRequestBuilders.forEach(builder -> {
      Symbol symbol = builder.getSymbol();
      if (symbol != null) { // null이 아닌 경우만 등록 (Default는 제외)
        builderMap.put(symbol, builder);
      }
    });
    
    // Cosmos 체인들을 CosmosSignRequestBuilder로 매핑
    SignRequestBuilder cosmosBuilder = findCosmosBuilder();
    if (cosmosBuilder != null) {
      builderMap.put(Symbol.ATOM, cosmosBuilder);
      builderMap.put(Symbol.TIA, cosmosBuilder);
    }
  }

  public SignRequestBuilder getBuilder(Symbol symbol) {
    SignRequestBuilder builder = builderMap.get(symbol);
    if (builder == null) {
      // 등록된 빌더가 없으면 기본 빌더 사용
      return defaultSignRequestBuilder;
    }
    return builder;
  }
  
  private SignRequestBuilder findCosmosBuilder() {
    return signRequestBuilders.stream()
        .filter(builder -> builder instanceof CosmosSignRequestBuilder)
        .findFirst()
        .orElse(null);
  }
}