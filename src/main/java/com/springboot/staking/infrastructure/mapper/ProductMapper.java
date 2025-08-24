package com.springboot.staking.infrastructure.mapper;

import com.springboot.staking.application.dto.request.ProductRequest;
import com.springboot.staking.application.dto.response.ProductResponse;
import com.springboot.staking.infrastructure.persistence.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

  @Mapping(target = "id", ignore = true)
  Product toEntity(ProductRequest request);

  ProductResponse toDto(Product product);
}
