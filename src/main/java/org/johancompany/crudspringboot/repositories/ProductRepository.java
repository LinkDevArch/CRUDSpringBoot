package org.johancompany.crudspringboot.repositories;

import org.johancompany.crudspringboot.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}