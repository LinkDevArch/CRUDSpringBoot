package org.johancompany.crudspringboot.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.johancompany.crudspringboot.dtos.ProductDTO;
import org.johancompany.crudspringboot.domain.Product;
import org.johancompany.crudspringboot.dtos.ProductStatisticsDTO;
import org.johancompany.crudspringboot.mapper.ProductMapper;
import org.johancompany.crudspringboot.repositories.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Transactional(readOnly = true) //Only read the DB, does not alter it.
    public List<ProductDTO> findAll() {

        List<Product> products = productRepository.findAll();
        List<ProductDTO> productDTOs = new ArrayList<>();
        // Map all objects list to DTO
        for (Product product : products) {
            ProductDTO productDTO = productMapper.toProductDTO(product);
            productDTOs.add(productDTO);
        }

        log.info("Número total de productos encontrados: {}", products.size());


        return productDTOs;
    }

    @Transactional(readOnly = true)
    public Optional<ProductDTO> findById(Long id) {
        return productRepository.findById(id)
                .map(productMapper::toProductDTO);
    }

    public void save(ProductDTO productDTO) {
        Product product = productMapper.toProduct(productDTO);
        productRepository.save(product);
    }

    public void deleteById (Long id) {
        productRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> searchProducts(String term) {
        if (term == null || term.isEmpty()) {
            return findAll();
        }

        List<Product> products = productRepository.findByProductNameContainingIgnoreCaseOrCategoryContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                term, term, term);
        return products.stream()
                .map(productMapper::toProductDTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<ProductDTO> filterProducts(String filter, List<ProductDTO> products) {
        if (filter == null || filter.isEmpty()) {
            return products;
        }

        switch (filter) {
            case "stock-bajo":
                return products.stream()
                        .filter(p -> p.getStock() <= 10)
                        .collect(Collectors.toList());
            case "precio-alto":
                return products.stream()
                        .sorted((p1, p2) -> p2.getPrice().compareTo(p1.getPrice()))
                        .collect(Collectors.toList());
            case "precio-bajo":
                return products.stream()
                        .sorted(Comparator.comparing(ProductDTO::getPrice))
                        .collect(Collectors.toList());
            default:
                return products;
        }
    }


    @Transactional(readOnly = true)
    public ProductStatisticsDTO getProductStatistics() {
        List<Product> products = productRepository.findAll();
        int totalProducts = products.size();

        Set<String> categories = new HashSet<>();
        int lowStockCount = 0;

        for (Product product : products) {
            if (product.getCategory() != null) {
                categories.add(product.getCategory());
            }
            if (product.getStock() <= 10) {
                lowStockCount++;
            }
        }

        return new ProductStatisticsDTO(
                totalProducts,
                categories.size(),
                lowStockCount
        );
    }


    public Workbook genExcel(List<ProductDTO> productos) {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Productos");

        // Cabecera
        Row header = sheet.createRow(0);
        String[] columnas = {"ID", "Nombre", "Precio","Categoria", "Stock"};
        for (int i = 0; i < columnas.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(columnas[i]);
        }

        // Filas de datos
        int rowIdx = 1;
        for (ProductDTO p : productos) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(p.getId());
            row.createCell(1).setCellValue(p.getProductName());
            row.createCell(2).setCellValue(p.getPrice());
            row.createCell(3).setCellValue(p.getCategory());
            row.createCell(4).setCellValue(p.getStock());
        }

        // Ajustar ancho columnas
        for (int i = 0; i < columnas.length; i++) {
            sheet.autoSizeColumn(i);
        }

        return wb;
    }


}
