package fr.fullstack.shopapp.service;

import fr.fullstack.shopapp.model.OpeningHoursShop;
import fr.fullstack.shopapp.model.Product;
import fr.fullstack.shopapp.model.Shop;
import fr.fullstack.shopapp.model.ShopElestack;
import fr.fullstack.shopapp.repository.ShopElasticsearchRepository;
import fr.fullstack.shopapp.repository.ShopRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.hibernate.search.mapper.orm.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ShopService {
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private ShopElasticsearchRepository shopElasticsearchRepository;

    public List<ShopElestack> searchShops(String name, Boolean inVacations, LocalDate startDate, LocalDate endDate) {
        // Si aucun critère n'est fourni
        if ((name == null || name.isEmpty()) && inVacations == null && startDate == null && endDate == null) {
            return new ArrayList<>();
        }
        if ((inVacations == null && startDate == null && endDate == null) && name != null && !name.isEmpty()) {
            return shopElasticsearchRepository.findByNameContaining(name);
        }
        if ((name == null || name.isEmpty()) && startDate == null && endDate == null) {
            return shopElasticsearchRepository.findByInVacations(inVacations);
        }
        if (name == null || name.isEmpty()) {
            if (startDate == null) {
                return shopElasticsearchRepository.findByInVacationsAndCreatedAtBefore(inVacations, endDate);
            } else if (endDate == null) {
                return shopElasticsearchRepository.findByInVacationsAndCreatedAtAfter(inVacations, startDate);
            } else {
                return shopElasticsearchRepository.findByInVacationsAndCreatedAtBetween(inVacations, startDate, endDate);
            }
        }

        // Si le nom est fourni avec d'autres paramètres
        if (startDate == null && endDate == null) {
            return shopElasticsearchRepository.findByInVacationsAndNameContaining(inVacations, name);
        }

        if (startDate == null) {
            return shopElasticsearchRepository.findByInVacationsAndNameContainingAndCreatedAtBefore(inVacations, name, endDate);
        } else if (endDate == null) {
            return shopElasticsearchRepository.findByInVacationsAndNameContainingAndCreatedAtAfter(inVacations, name, startDate);
        } else {
            return shopElasticsearchRepository.findByInVacationsAndNameContainingAndCreatedAtBetween(inVacations, name, startDate, endDate);
        }
    }





    @Transactional
    public void initializeElasticsearch() {
        List<Shop> allShops = shopRepository.findAll();
        List<ShopElestack> elasticsearchShops = allShops.stream().map(this::toElasticsearchModel).toList();
        shopElasticsearchRepository.saveAll(elasticsearchShops);
        System.out.println("Boutiques initialisées dans Elasticsearch.");
    }

    private ShopElestack toElasticsearchModel(Shop shop) {
        ShopElestack shopElestack = new ShopElestack();
        shopElestack.setId(shop.getId());
        shopElestack.setName(shop.getName());
        shopElestack.setInVacations(shop.getInVacations());
        shopElestack.setCreatedAt(shop.getCreatedAt());
        shopElestack.setNbProducts((long) shop.getProducts().size());
        return shopElestack;
    }


    @Transactional
    public Shop createShop(Shop shop) {
        try {
            validateOpeningHours(shop.getOpeningHours());
            Shop newShop = shopRepository.save(shop);
            em.flush();
            em.refresh(newShop);

            ShopElestack shopElestack = toElasticsearchModel(newShop);
            shopElasticsearchRepository.save(shopElestack);

            return newShop;
        } catch (ValidationException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Erreur de validation des horaires d'ouverture : " + e.getMessage()
            );
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Conflit d'intégrité des données : " + e.getMessage()
            );
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Une erreur inattendue est survenue lors de la création de la boutique : " + e.getMessage()
            );
        }
    }


    @Transactional
    public void deleteShopById(long id) throws Exception {
        try {
            Shop shop = getShop(id);
            // delete nested relations with products
            deleteNestedRelations(shop);
            shopRepository.deleteById(id);

            shopElasticsearchRepository.deleteById(id);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public Shop getShopById(long id) throws Exception {
        try {
            return getShop(id);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public Page<Shop> getShopList(
            Optional<String> sortBy,
            Optional<Boolean> inVacations,
            Optional<String> createdBefore,
            Optional<String> createdAfter,
            Pageable pageable
    ) {
        // SORT
        if (sortBy.isPresent()) {
            switch (sortBy.get()) {
                case "name":
                    return shopRepository.findByOrderByNameAsc(pageable);
                case "createdAt":
                    return shopRepository.findByOrderByCreatedAtAsc(pageable);
                default:
                    return shopRepository.findByOrderByNbProductsAsc(pageable);
            }
        }

        // FILTERS
        Page<Shop> shopList = getShopListWithFilter(inVacations, createdBefore, createdAfter, pageable);
        if (shopList != null) {
            return shopList;
        }

        // NONE
        return shopRepository.findByOrderByIdAsc(pageable);
    }

    @Transactional
    public Shop updateShop(Shop shop) {
        try {
            getShop(shop.getId());

            // Validation des horaires d'ouverture
            validateOpeningHours(shop.getOpeningHours());
            Shop updatedShop = this.createShop(shop);

            // Synchronisation avec Elasticsearch
            ShopElestack shopElestack = toElasticsearchModel(updatedShop);
            shopElasticsearchRepository.save(shopElestack);

            return updatedShop;

        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "La boutique avec l'identifiant " + shop.getId() + " n'existe pas."
            );
        } catch (ValidationException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Erreur de validation des horaires d'ouverture : " + e.getMessage()
            );
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Conflit d'intégrité des données : " + e.getMessage()
            );
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Une erreur inattendue est survenue lors de la mise à jour de la boutique : " + e.getMessage()
            );
        }
    }


    private void deleteNestedRelations(Shop shop) {
        List<Product> products = shop.getProducts();
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            product.setShop(null);
            em.merge(product);
            em.flush();
        }
    }

    private Shop getShop(Long id) throws Exception {
        Optional<Shop> shop = shopRepository.findById(id);
        if (!shop.isPresent()) {
            throw new Exception("Shop with id " + id + " not found");
        }
        return shop.get();
    }

    private Page<Shop> getShopListWithFilter(
            Optional<Boolean> inVacations,
            Optional<String> createdAfter,
            Optional<String> createdBefore,
            Pageable pageable
    ) {
        if (inVacations.isPresent() && createdBefore.isPresent() && createdAfter.isPresent()) {
            return shopRepository.findByInVacationsAndCreatedAtGreaterThanAndCreatedAtLessThan(
                    inVacations.get(),
                    LocalDate.parse(createdAfter.get()),
                    LocalDate.parse(createdBefore.get()),
                    pageable
            );
        }

        if (inVacations.isPresent() && createdBefore.isPresent()) {
            return shopRepository.findByInVacationsAndCreatedAtLessThan(
                    inVacations.get(), LocalDate.parse(createdBefore.get()), pageable
            );
        }

        if (inVacations.isPresent() && createdAfter.isPresent()) {
            return shopRepository.findByInVacationsAndCreatedAtGreaterThan(
                    inVacations.get(), LocalDate.parse(createdAfter.get()), pageable
            );
        }

        if (inVacations.isPresent()) {
            return shopRepository.findByInVacations(inVacations.get(), pageable);
        }

        if (createdBefore.isPresent() && createdAfter.isPresent()) {
            return shopRepository.findByCreatedAtBetween(
                    LocalDate.parse(createdAfter.get()), LocalDate.parse(createdBefore.get()), pageable
            );
        }

        if (createdBefore.isPresent()) {
            return shopRepository.findByCreatedAtLessThan(
                    LocalDate.parse(createdBefore.get()), pageable
            );
        }

        if (createdAfter.isPresent()) {
            return shopRepository.findByCreatedAtGreaterThan(
                    LocalDate.parse(createdAfter.get()), pageable
            );
        }

        return null;
    }

    private void validateOpeningHours(List<OpeningHoursShop> openingHours) throws Exception {
        for (int i = 0; i < openingHours.size(); i++) {
            OpeningHoursShop current = openingHours.get(i);
            for (int j = i + 1; j < openingHours.size(); j++) {
                OpeningHoursShop compare = openingHours.get(j);
                if (current.getDay() == compare.getDay()) {
                    if (!(current.getCloseAt().isBefore(compare.getOpenAt()) || current.getOpenAt().isAfter(compare.getCloseAt()))) {
                        throw new Exception("Les horaires se chevauchent pour le jour " + current.getDay());
                    }
                }
            }
        }
    }

}
