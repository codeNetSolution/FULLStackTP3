package fr.fullstack.shopapp.controller;

import fr.fullstack.shopapp.model.Product;
import fr.fullstack.shopapp.service.ProductService;
import fr.fullstack.shopapp.util.ErrorValidation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);
    @Autowired
    private ProductService service;

    @Operation(summary = "Create a product")
    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product, Errors errors) {
        if (errors.hasErrors()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, ErrorValidation.getErrorValidationMessage(errors));
        }
        try {
            log.info("product: {}", product);
            log.info("price of product is {}", product.getPrice());
            return ResponseEntity.ok(service.createProduct(product));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Operation(summary = "Delete a product by its id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable long id) {
        try {
            service.deleteProductById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Operation(summary = "Get a product by id")
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable long id) {
        try {
            Product product = service.getProductById(id);
            String formattedPrice = product.getFormattedPrice();
            return ResponseEntity.ok().body(product);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Operation(summary = "Get products (filtering by shop and category is possible)")
    @GetMapping
    @Parameters({@Parameter(name = "page", description = "Results page you want to retrieve (0..N)", example = "0", in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY),
            @Parameter(name = "size", description = "Number of records per page", example = "5", in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY),})
    public ResponseEntity<Page<Product>> getProductsOfShop(
            Pageable pageable,
            @Parameter(description = "Id of the shop") @RequestParam(required = false) Optional<Long> shopId,
            @Parameter(description = "Id of the category", example = "1") @RequestParam(required = false)
            Optional<Long> categoryId
    ) {
        return ResponseEntity.ok(
                service.getShopProductList(shopId, categoryId, pageable)
        );
    }

    @Operation(summary = "Update a product")
    @PutMapping
    public ResponseEntity<Product> updateProduct(@Valid @RequestBody Product product, Errors errors) {
        if (errors.hasErrors()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, ErrorValidation.getErrorValidationMessage(errors));
        }

        try {
            return ResponseEntity.ok().body(service.updateProduct(product));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
