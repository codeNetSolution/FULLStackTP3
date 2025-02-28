package fr.fullstack.shopapp.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products", indexes = {
        @Index(name = "idx_products_shop_id", columnList = "shop_id"),
        @Index(name = "idx_products_price", columnList = "price")
})
public class Product {
    @ManyToMany
    @JoinTable(name = "products_categories",
            joinColumns = @JoinColumn(name = "product_id", foreignKey = @ForeignKey(name = "FK_products_categories_product_id")),
            inverseJoinColumns = @JoinColumn(name = "category_id", foreignKey = @ForeignKey(name = "FK_products_categories_category_id")),
            indexes = {
                    @Index(name = "idx_products_categories_product_id", columnList = "product_id"),
                    @Index(name = "idx_products_categories_category_id", columnList = "category_id")
            })
    private List<Category> categories = new ArrayList<Category>();

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true)
    @Size(min = 1, message = "At least one name and one description must be provided")
    private List<@Valid LocalizedProduct> localizedProduct = new ArrayList<LocalizedProduct>();

    @Column(nullable = false)
    @PositiveOrZero(message = "Price must be positive")
    @NotNull(message = "Price may not be null")
    private BigDecimal price;

    @ManyToOne
    private Shop shop;

    public List<Category> getCategories() {
        return categories;
    }

    public long getId() {
        return id;
    }

    public List<LocalizedProduct> getLocalizedProducts() {
        return localizedProduct;
    }

    public BigDecimal getPrice() {
        return price.divide(BigDecimal.valueOf(100), 3, RoundingMode.HALF_UP);
    }

    public Shop getShop() {
        return shop;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setLocalizedProducts(List<LocalizedProduct> localizedProduct) {
        this.localizedProduct = localizedProduct;
    }

    public void setPrice(BigDecimal price) {

        this.price = price.multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP);
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public String getFormattedPrice() {
        BigDecimal priceInEuros = price.divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
        return String.format("%.2f€", priceInEuros);
    }
}
