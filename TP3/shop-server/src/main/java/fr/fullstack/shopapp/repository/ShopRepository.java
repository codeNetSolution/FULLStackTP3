package fr.fullstack.shopapp.repository;

import fr.fullstack.shopapp.model.Shop;
import fr.fullstack.shopapp.model.ShopElestack;
import fr.fullstack.shopapp.util.ShopDetailsProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ShopRepository extends JpaRepository<Shop, Long> {
    Page<Shop> findByCreatedAtBetween(LocalDate dateStart, LocalDate dateEnd, Pageable pageable);

    Page<Shop> findByCreatedAtGreaterThan(LocalDate date, Pageable pageable);

    Page<Shop> findByCreatedAtLessThan(LocalDate date, Pageable pageable);

    // FILTERS
    Page<Shop> findByInVacations(boolean inVacations, Pageable pageable);

    Page<Shop> findByInVacationsAndCreatedAtGreaterThan(boolean inVacations, LocalDate date, Pageable pageable);

    Page<Shop> findByInVacationsAndCreatedAtGreaterThanAndCreatedAtLessThan(
            boolean inVacations, LocalDate dateStart,
            LocalDate dateEnd, Pageable pageable
    );


    Page<Shop> findByInVacationsAndCreatedAtLessThan(boolean inVacations, LocalDate date, Pageable pageable);

    Page<Shop> findByOrderByCreatedAtAsc(Pageable pageable);

    Page<Shop> findByOrderByIdAsc(Pageable pageable);

    // SORT
    Page<Shop> findByOrderByNameAsc(Pageable pageable);

    @Query(
            value = "SELECT *,"
                    + "(SELECT COUNT(*) FROM products p WHERE p.shop_id = s.id) as nbProducts, "
                    + "(SELECT COUNT(DISTINCT pc.category_id) FROM products_categories pc WHERE pc.product_id IN "
                    + "(SELECT p.id FROM products p WHERE p.shop_id = s.id)) as nbCategories "
                    + "FROM shops s "
                    + "ORDER BY (SELECT COUNT(*) FROM products p WHERE p.shop_id = s.id) DESC",
            countQuery = "SELECT * "
                    + "FROM shops s "
                    + "ORDER BY (SELECT COUNT(*) FROM products p WHERE p.shop_id = s.id) DESC",
            nativeQuery = true
    )
    Page<Shop> findByOrderByNbProductsAsc(Pageable pageable);


    @Query("""
        SELECT s.name AS name, 
               s.createdAt AS createdAt, 
               COUNT(p.id) AS nbProducts, 
               COUNT(DISTINCT pc.id) AS nbCategories, 
               s.inVacations AS inVacations
        FROM Shop s
        LEFT JOIN Product p ON p.shop.id = s.id
        LEFT JOIN p.categories pc
        GROUP BY s.id
    """)
    Page<ShopDetailsProjection> findShopDetails(Pageable pageable);

    @Query("""
        SELECT s.name, s.createdAt, 
               (SELECT COUNT(*) FROM Product p WHERE p.shop.id = s.id) AS nbProducts,
               (SELECT COUNT(DISTINCT pc.id) 
                FROM Product p JOIN p.categories pc 
                WHERE p.shop.id = s.id) AS nbCategories,
               s.inVacations
        FROM Shop s
        WHERE (:name IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%')))
          AND (:inVacations IS NULL OR s.inVacations = :inVacations)
          AND (:createdBefore IS NULL OR s.createdAt <= :createdBefore)
          AND (:createdAfter IS NULL OR s.createdAt >= :createdAfter)
    """)
    List<Object[]> findShopsByCriteria(
            @Param("name") String name,
            @Param("inVacations") Boolean inVacations,
            @Param("createdBefore") LocalDate createdBefore,
            @Param("createdAfter") LocalDate createdAfter
    );


    List<Shop> findByInVacationsAndCreatedAtBefore(boolean inVacations, LocalDate endDate);

    List<Shop> findByInVacationsAndCreatedAtAfter(boolean inVacations, LocalDate startDate);

    List<Shop> findByInVacationsAndCreatedAtBetween(boolean inVacations, LocalDate startDate, LocalDate endDate);

    List<Shop> findByNameContaining(String name);

}

