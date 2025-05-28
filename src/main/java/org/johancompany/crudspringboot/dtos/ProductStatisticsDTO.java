package org.johancompany.crudspringboot.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductStatisticsDTO {
    private int totalProducts;
    private long uniqueCategories;
    private long lowStockCount;
}
