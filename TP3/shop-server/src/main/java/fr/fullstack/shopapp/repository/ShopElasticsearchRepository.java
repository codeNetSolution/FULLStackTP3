package fr.fullstack.shopapp.repository;

import fr.fullstack.shopapp.model.ShopElestack;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.time.LocalDate;
import java.util.List;

public interface ShopElasticsearchRepository extends ElasticsearchRepository<ShopElestack, Long> {

    List<ShopElestack> findByNameContaining(String name);

    List<ShopElestack> findByInVacations(boolean inVacations);

    List<ShopElestack> findByInVacationsAndNameContaining(boolean inVacations, String name);

    List<ShopElestack> findByInVacationsAndCreatedAtBefore(boolean inVacations, LocalDate endDate);

    List<ShopElestack> findByInVacationsAndCreatedAtAfter(boolean inVacations, LocalDate startDate);

    List<ShopElestack> findByInVacationsAndCreatedAtBetween(boolean inVacations, LocalDate startDate, LocalDate endDate);

    List<ShopElestack> findByInVacationsAndNameContainingAndCreatedAtBefore(boolean inVacations, String name, LocalDate endDate);

    List<ShopElestack> findByInVacationsAndNameContainingAndCreatedAtAfter(boolean inVacations, String name, LocalDate startDate);

    List<ShopElestack> findByInVacationsAndNameContainingAndCreatedAtBetween(boolean inVacations, String name, LocalDate startDate, LocalDate endDate);


}