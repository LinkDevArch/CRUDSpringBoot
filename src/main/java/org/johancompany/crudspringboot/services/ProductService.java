package org.johancompany.crudspringboot.services;

import org.johancompany.crudspringboot.domain.ProductDTO;
import org.johancompany.crudspringboot.entities.Product;
import org.johancompany.crudspringboot.repositories.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true) //Only read the DB, does not alter it.
    public List<ProductDTO> findAll() {

        List<Product> products = productRepository.findAll();
        List<ProductDTO> productDTOs = new ArrayList<>();
        // Map all objects list to DTO
        for (Product product : products) {
            ProductDTO productDTO = mapToProductDTO(product);
            productDTOs.add(productDTO);
        }

        return productDTOs;
    }

    @Transactional(readOnly = true)
    public Optional<ProductDTO> findById(Long id) {
        return productRepository.findById(id)
                .map(this::mapToProductDTO);
    }

    public Product save(ProductDTO productDTO) {
        Product product = mapToProduct(productDTO);
        return productRepository.save(product);
    }

    public void deleteById (Long id) {
        productRepository.deleteById(id);
    }

    //Private methods

    private Product mapToProduct(ProductDTO productDTO) {
        Product p = new Product();
        p.setId(productDTO.getId());
        p.setCategory(productDTO.getCategory());
        p.setProductName(productDTO.getProductName());
        p.setDescription(productDTO.getDescription());
        p.setPrice(productDTO.getPrice());
        p.setStock(productDTO.getStock());
        return p;
    }

    private ProductDTO mapToProductDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setCategory(product.getCategory());
        dto.setProductName(product.getProductName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        return dto;
    }
}
