package fr.fullstack.shopapp.service;

import fr.fullstack.shopapp.model.LocalizedProduct;
import fr.fullstack.shopapp.model.Product;
import fr.fullstack.shopapp.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Service
public class ProductService {
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public Product createProduct(Product product) throws Exception {
        Product receiveProduct = product;

        try {
            checkLocalizedProducts(receiveProduct);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid localized products: " + e.getMessage(), e);
        }
        try {
            // Sauvegarde dans la base de données
            Product newProduct = productRepository.save(receiveProduct);
            em.flush(); // Synchronisation avec la base
            em.refresh(newProduct); // Recharge l'entité depuis la base
            return newProduct;
        } catch (DataAccessException e) {
            throw new Exception("Error while saving product to the database: " + e.getMessage(), e);
        }
    }


    public void deleteProductById(long id) throws Exception {
        try {
            getProduct(id);
            productRepository.deleteById(id);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public Product getProductById(long id) throws Exception {
        try {
            return getProduct(id);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public Page<Product> getShopProductList(Optional<Long> shopId, Optional<Long> categoryId, Pageable pageable) {
        if (shopId.isPresent() && categoryId.isPresent()) {
            return productRepository.findByShopAndCategory(shopId.get(), categoryId.get(), pageable);
        }

        if (shopId.isPresent()) {
            return productRepository.findByShop(shopId.get(), pageable);
        }

        return productRepository.findByOrderByIdAsc(pageable);
    }

    @Transactional
    public Product updateProduct(Product product) throws Exception {
        try {
            getProduct(product.getId());
            return this.createProduct(product);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    private void checkLocalizedProducts(Product product) throws Exception {
        Optional<LocalizedProduct> localizedProductFr = product.getLocalizedProducts()
                .stream().filter(o -> o.getLocale().equals("FR")).findFirst();

        // A name in french must be at least provided
        if (!localizedProductFr.isPresent()) {
            throw new Exception("A name in french must be at least provided");
        }
    }

    private Product getProduct(Long id) throws Exception {
        Optional<Product> product = productRepository.findById(id);
        if (!product.isPresent()) {
            throw new Exception("Product with id " + id + " not found");
        }
        return product.get();
    }

}
