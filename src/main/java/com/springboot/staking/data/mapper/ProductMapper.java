package com.springboot.staking.data.mapper;

import com.springboot.staking.data.dto.request.ProductRequest;
import com.springboot.staking.data.dto.response.ProductResponse;
import com.springboot.staking.data.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

  @Mapping(target = "id", ignore = true)
  Product toEntity(ProductRequest request);

  ProductResponse toDto(Product product);
}
