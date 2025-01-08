package fr.fullstack.shopapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.fullstack.shopapp.model.Category;
import fr.fullstack.shopapp.model.LocalizedProduct;
import fr.fullstack.shopapp.model.Product;
import fr.fullstack.shopapp.model.Shop;
import fr.fullstack.shopapp.service.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateProduct() throws Exception {
        Product product = new Product();
        product.setId(1L);
        product.setPrice(100.0f);
        LocalizedProduct localizedProductFr = new LocalizedProduct();
        localizedProductFr.setLocale("FR");
        localizedProductFr.setName("Produit Test");
        localizedProductFr.setDescription("Description du produit");

        product.setLocalizedProducts(Collections.singletonList(localizedProductFr));

        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");
        product.setCategories(Collections.singletonList(category));

        Shop shop = new Shop();
        shop.setId(1L);
        shop.setName("Shop Test");
        product.setShop(shop);

        Mockito.when(productService.createProduct(any(Product.class))).thenReturn(product);
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.price", is(100.0)))
                .andExpect(jsonPath("$.localizedProducts[0].locale", is("FR")))
                .andExpect(jsonPath("$.localizedProducts[0].name", is("Produit Test")))
                .andExpect(jsonPath("$.categories[0].name", is("Electronics")))
                .andExpect(jsonPath("$.shop.name", is("Shop Test")));
    }

    @Test
    void testDeleteProduct() throws Exception {
        Mockito.doNothing().when(productService).deleteProductById(1L);

        mockMvc.perform(delete("/api/v1/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetProductById() throws Exception {
        Product product = new Product();
        product.setId(1L);
        product.setPrice(200.0f);

        LocalizedProduct localizedProductFr = new LocalizedProduct();
        localizedProductFr.setLocale("FR");
        localizedProductFr.setName("Produit Test");
        product.setLocalizedProducts(Collections.singletonList(localizedProductFr));

        Mockito.when(productService.getProductById(1L)).thenReturn(product);

        mockMvc.perform(get("/api/v1/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.price", is(200.0)))
                .andExpect(jsonPath("$.localizedProducts[0].name", is("Produit Test")));
    }

    @Test
    void testGetProductsOfShop() throws Exception {
        Product product1 = new Product();
        product1.setId(1L);
        product1.setPrice(50.0f);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setPrice(150.0f);

        Page<Product> products = new PageImpl<>(Arrays.asList(product1, product2));

        Mockito.when(productService.getShopProductList(any(), any(), any()))
                .thenReturn(products);
        mockMvc.perform(get("/api/v1/products")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].price", is(50.0)))
                .andExpect(jsonPath("$.content[1].price", is(150.0)));
    }

    @Test
    void testUpdateProduct() throws Exception {
        Product product = new Product();
        product.setId(1L);
        product.setPrice(300.0f);

        LocalizedProduct localizedProductFr = new LocalizedProduct();
        localizedProductFr.setLocale("FR");
        localizedProductFr.setName("Produit Mis à Jour");
        product.setLocalizedProducts(Collections.singletonList(localizedProductFr));

        Mockito.when(productService.updateProduct(any(Product.class))).thenReturn(product);

        mockMvc.perform(put("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.price", is(300.0)))
                .andExpect(jsonPath("$.localizedProducts[0].name", is("Produit Mis à Jour")));
    }
}
