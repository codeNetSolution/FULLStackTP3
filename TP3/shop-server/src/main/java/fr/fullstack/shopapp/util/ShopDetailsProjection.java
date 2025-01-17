package fr.fullstack.shopapp.util;

import java.time.LocalDate;

public interface ShopDetailsProjection {
    String getName();
    LocalDate getCreatedAt();
    Long getNbProducts();
    Long getNbCategories();
    boolean isInVacations();
}
