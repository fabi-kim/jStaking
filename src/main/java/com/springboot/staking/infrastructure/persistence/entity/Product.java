package com.springboot.staking.infrastructure.persistence.entity;

import com.springboot.staking.shared.constant.ProductType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"productType", "symbol"})
    }
)
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Product extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  ProductType productType;

  @Column(nullable = false)
  String symbol;

}

/*
  create table staking.product ( id           bigint auto_increment primary key, symbol
  varchar(255) not null, product_type varchar(10)  not null, created_at   TIMESTAMP not null
  DEFAULT CURRENT_TIMESTAMP, updated_at   TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP ON UPDATE
  CURRENT_TIMESTAMP, constraint unique_product_product_type_symbol unique (product_type, symbol)
  );
 */