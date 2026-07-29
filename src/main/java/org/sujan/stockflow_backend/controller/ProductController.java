package org.sujan.stockflow_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.sujan.stockflow_backend.entity.Product;
import org.sujan.stockflow_backend.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping(value = "/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Product addProduct(@RequestBody Product product) {
        return productService.createProduct(product);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE,consumes = MediaType.APPLICATION_JSON_VALUE)
    public Product updateProduct(@PathVariable Long id ,@RequestBody Product product) {
        return productService.updateProduct(id, product);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}
