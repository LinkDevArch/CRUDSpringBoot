package org.johancompany.crudspringboot.mapper;

import org.johancompany.crudspringboot.domain.Product;
import org.johancompany.crudspringboot.dtos.ProductDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductDTO toProductDTO(Product product);
    Product toProduct(ProductDTO productDTO);
}
