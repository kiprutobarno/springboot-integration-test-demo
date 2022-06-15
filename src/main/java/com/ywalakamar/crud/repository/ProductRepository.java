package com.ywalakamar.crud.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ywalakamar.crud.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    Product findByName(String name);
}
