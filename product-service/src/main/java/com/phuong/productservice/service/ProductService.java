package com.phuong.productservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.phuong.productservice.dto.ProductRequest;
import com.phuong.productservice.dto.ProductResponse;
import com.phuong.productservice.model.Product;
import com.phuong.productservice.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor // Của lombok, nó sẽ tạo 1 constuctor của class này với các tham số có đánh dấu final, @NotNull or @NonNull
public class ProductService {
    private final ProductRepository productRepository;

    public void createProduct(ProductRequest productRequest) {
        Product product = Product.builder()
                          .name(productRequest.getName())
                          .description(productRequest.getDescription())
                          .price(productRequest.getPrice())
                          .build();

        log.info(String.format("New product is create with name is %s", product.getName()));
        productRepository.save(product);
        log.info("Product {} is save", product.getId());
    }

    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();

        // this::mapToProductResponse tương đương với product -> mapToProductRespose(product)
        return products.stream().map(this::mapToProductResponse).toList();
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
            .id(product.getId())
            .name(product.getName())
            .description(product.getDescription())
            .price(product.getPrice())
            .build();
    }   
}
