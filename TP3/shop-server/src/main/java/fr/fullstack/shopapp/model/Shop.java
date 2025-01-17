package fr.fullstack.shopapp.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "shops", indexes = {
        @Index(name = "idx_shops_name", columnList = "name"),
        @Index(name = "idx_shops_created_at", columnList = "created_at")
})
@Indexed(index = "idx_shops")
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shop_seq")
    @SequenceGenerator(name = "shop_seq", sequenceName = "shops_id_seq", allocationSize = 1)
    private long id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdAt;



    @Column(nullable = false)
    @NotNull(message = "InVacations may not be null")
    @GenericField
    private boolean inVacations;

    @Column(nullable = false)
    @Size(min = 1, max = 255, message = "Name must be between 1 and 255 characters")
    @NotNull(message = "Name may not be null")
    @FullTextField
    private String name;

    @Formula(value = "(SELECT COUNT(*) FROM products p WHERE p.shop_id = id)")
    @Field(type= FieldType.Long)
    private Long nbProducts;

    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<@Valid OpeningHoursShop> openingHours = new ArrayList<OpeningHoursShop>();

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Product> products = new ArrayList<Product>();

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public long getId() {
        return id;
    }

    public boolean getInVacations() {
        return inVacations;
    }

    public String getName() {
        return name;
    }

    public Long getNbProducts() {
        return nbProducts != null ? nbProducts : 0L;
    }

    public List<OpeningHoursShop> getOpeningHours() {
        return openingHours;
    }

    public List<Product> getProducts() {
        return this.products;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setInVacations(boolean inVacations) {
        this.inVacations = inVacations;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNbProducts(long nbProducts) {
        this.nbProducts = nbProducts;
    }

    public void setOpeningHours(List<OpeningHoursShop> openingHours) {
        this.openingHours = openingHours;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
