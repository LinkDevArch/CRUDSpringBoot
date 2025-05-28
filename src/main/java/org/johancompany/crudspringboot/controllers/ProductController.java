package org.johancompany.crudspringboot.controllers;

import jakarta.validation.Valid;
import org.apache.poi.ss.usermodel.Workbook;
import org.johancompany.crudspringboot.dtos.ProductDTO;
import org.johancompany.crudspringboot.dtos.ProductStatisticsDTO;
import org.johancompany.crudspringboot.services.ProductService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String getProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String filter,
            Model model) {

        List<ProductDTO> products = productService.searchProducts(search);
        products = productService.filterProducts(filter, products);

        ProductStatisticsDTO statistics = productService.getProductStatistics();

        model.addAttribute("products", products);
        model.addAttribute("totalProducts", statistics.getTotalProducts());
        model.addAttribute("uniqueCategories", statistics.getUniqueCategories());
        model.addAttribute("lowStockCount", statistics.getLowStockCount());
        model.addAttribute("searchTerm", search);
        model.addAttribute("currentFilter", filter);

        return "productsCrud";
    }


    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        ProductDTO product = productService.findById(id)
                .orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado con id " + id)
                );
        model.addAttribute("product", product);
        return "productForm";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new ProductDTO());
        return "productForm";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("product") @Valid ProductDTO dto, BindingResult br) {
        if (br.hasErrors()) {
            return "productForm";
        }
        productService.save(dto);
        return "redirect:/products";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        productService.deleteById(id);
        return "redirect:/products";
    }

    @GetMapping("/export/excel")
    public ResponseEntity<ByteArrayResource> exportarExcel() {
        List<ProductDTO> productos = productService.findAll();
        Workbook wb = productService.genExcel(productos);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            wb.write(out);
            byte[] bytes = out.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(
                    ContentDisposition.builder("attachment")
                            .filename("productos.xlsx")
                            .build()
            );
            headers.setContentType(
                    MediaType.parseMediaType(
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                    )
            );

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new ByteArrayResource(bytes));

        } catch (IOException e) {
            throw new RuntimeException("Error al generar Excel", e);
        }
    }
}
