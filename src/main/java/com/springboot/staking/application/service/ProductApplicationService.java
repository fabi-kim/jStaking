package com.springboot.staking.application.service;

import com.springboot.staking.application.dto.request.ProductRequest;
import com.springboot.staking.application.dto.response.ProductResponse;
import com.springboot.staking.infrastructure.mapper.ProductMapper;
import com.springboot.staking.infrastructure.persistence.entity.Product;
import com.springboot.staking.infrastructure.persistence.repository.ProductRepository;
import com.springboot.staking.shared.constant.ErrorCode;
import com.springboot.staking.shared.exception.ApplicationException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductApplicationService {

  private final ProductRepository productRepository;
  private final ProductMapper productMapper;

  @Transactional()
  public ProductResponse createProduct(ProductRequest product) {
    log.info("[createProduct] request product : {}", product);

    if (productRepository.existsBySymbolAndProductType(product.symbol(), product.productType())) {
      throw new ApplicationException(ErrorCode.PRODUCT_DUPLICATE, "이미 등록된 상품입니다.");
    }
    Product createdProduct = productRepository.save(productMapper.toEntity(product));
    log.info("[createProduct] created product : {}", createdProduct);

    return productMapper.toDto(createdProduct);
  }
}
