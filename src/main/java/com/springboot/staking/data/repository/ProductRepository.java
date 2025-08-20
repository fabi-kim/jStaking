package com.springboot.staking.data.repository;

import com.springboot.staking.common.constant.ProductType;
import com.springboot.staking.data.entity.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

  Product saveAndFlush(Product product);

  Product findBySymbol(String symbol);

  List<Product> getBySymbolAndProductType(String symbol, ProductType productType);

  boolean existsBySymbolAndProductType(String symbol, ProductType productType);
}
