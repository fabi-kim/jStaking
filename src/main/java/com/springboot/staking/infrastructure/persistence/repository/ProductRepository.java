package com.springboot.staking.infrastructure.persistence.repository;

import com.springboot.staking.infrastructure.persistence.entity.Product;
import com.springboot.staking.shared.constant.ProductType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

  Product saveAndFlush(Product product);

  Optional<Product> findFirstBySymbolAndProductType(String symbol, ProductType productType);

  List<Product> getBySymbolAndProductType(String symbol, ProductType productType);

  boolean existsBySymbolAndProductType(String symbol, ProductType productType);
}
