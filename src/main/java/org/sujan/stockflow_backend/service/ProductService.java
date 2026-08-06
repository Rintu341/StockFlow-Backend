package org.sujan.stockflow_backend.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.sujan.stockflow_backend.entity.Product;
import org.sujan.stockflow_backend.exception.ProductNotFoundException;
import org.sujan.stockflow_backend.repository.ProductRepository;
import org.sujan.stockflow_backend.security.UserPrincipal;

import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    /*Find All
    * find by Id
    * create Product
    * update product
    * delete product
    *
    *  */
    public List<Product> getAllProducts() {
        return productRepository.findAllByTenantId(getCurrentTenantId());
    }

    private Long getCurrentTenantId() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return principal.getTenantId();
    }

    public Product getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("product not found with id: " + id));

        if(!product.getTenantId().equals(getCurrentTenantId())){
            throw new ProductNotFoundException("Product not found with id: "+id);
        }
        return product;
    }

    public Product createProduct(Product product) {
        product.setTenantId(getCurrentTenantId());
        return productRepository.save(product);
    }

    public Product updateProduct(Long id,Product product) {
        Product existing = getProductById(id);
        existing.setName(product.getName());
        existing.setPrice(product.getPrice());
        existing.setCategory(product.getCategory());
        existing.setStockQuantity(product.getStockQuantity());
        return productRepository.save(existing);
    }

    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
        if (productRepository.existsById(id)) {
            throw new RuntimeException("Failed to delete product with id: " + id);
        }
    }


}
