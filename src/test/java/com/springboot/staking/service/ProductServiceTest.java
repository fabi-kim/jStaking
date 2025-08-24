package com.springboot.staking.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.springboot.staking.data.dto.request.ProductRequest;
import com.springboot.staking.data.dto.response.ProductResponse;
import com.springboot.staking.data.mapper.ProductMapper;
import com.springboot.staking.data.mapper.ProductMapperImpl;
import com.springboot.staking.data.repository.ProductRepository;
import com.springboot.staking.shared.constant.ErrorCode;
import com.springboot.staking.shared.constant.ProductType;
import com.springboot.staking.shared.exception.ApplicationException;
import net.datafaker.Faker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

  @Spy
  final ProductMapper productMapper = new ProductMapperImpl();
  Faker faker = new Faker();
  @Mock
  private ProductRepository productRepository;
  @InjectMocks
  private ProductService productService;
  private String symbol;
  private ProductType productType;

  @BeforeEach
  public void setup() {
    symbol = faker.cryptoCoin().coin();
    productType = faker.options().option(ProductType.class);
  }

  @Test
  @DisplayName("createProduct: 신규 상품 생성 성공")
  void createProduct_success() {
    //given

    ProductRequest request = new ProductRequest(productType, symbol);

    when(productRepository.existsBySymbolAndProductType(eq(symbol), eq(productType))).thenReturn(
        false);
    when(productRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    //when
    ProductResponse result = productService.createProduct(request);

    //then
    Assertions.assertNotNull(result);
    assertEquals(symbol, result.symbol());
    assertEquals(productType, result.productType());

    verify(productRepository).existsBySymbolAndProductType(symbol, productType);
    verify(productRepository).save(any());
    verifyNoMoreInteractions(productRepository);

  }

  @Test
  @DisplayName("createProduct: 실패 - Symbol, type이 동일하면 ApplicationException(PRODUCT_DUPLICATE) 에러 발생")
  void createProduct_fail_duplicate() {
    ProductRequest request = new ProductRequest(productType, symbol);
    when(productRepository.existsBySymbolAndProductType(eq(symbol), eq(productType))).thenReturn(
        true);

    ApplicationException ex = assertThrows(ApplicationException.class,
        () -> productService.createProduct(request));

    assertEquals(ErrorCode.PRODUCT_DUPLICATE, ex.code());
    verify(productRepository, never()).save(any());
    verifyNoMoreInteractions(productRepository);
  }
}
