package org.johancompany.crudspringboot.controllers;

import jakarta.validation.Valid;
import org.johancompany.crudspringboot.domain.ProductDTO;
import org.johancompany.crudspringboot.services.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String getProducts(Model model) {
        model.addAttribute("products", productService.findAll());

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

}
