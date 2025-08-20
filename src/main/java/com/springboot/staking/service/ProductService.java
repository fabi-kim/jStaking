package com.springboot.staking.service;

import com.springboot.staking.common.constant.ErrorCode;
import com.springboot.staking.common.exception.ApplicationException;
import com.springboot.staking.data.dto.request.ProductRequest;
import com.springboot.staking.data.dto.response.ProductResponse;
import com.springboot.staking.data.entity.Product;
import com.springboot.staking.data.mapper.ProductMapper;
import com.springboot.staking.data.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProductService {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private final ProductRepository productRepository;
  private final ProductMapper productMapper;

  @Transactional()
  public ProductResponse createProduct(ProductRequest product) {
    logger.info("[createProduct] request product : {}", product);

    if (productRepository.existsBySymbolAndProductType(product.symbol(), product.productType())) {
      throw new ApplicationException(ErrorCode.PRODUCT_DUPLICATE, "이미 등록된 상품입니다.");
    }
    Product createdProduct = productRepository.save(productMapper.toEntity(product));
    logger.info("[createProduct] created product : {}", createdProduct);

    return productMapper.toDto(createdProduct);
  }
}
