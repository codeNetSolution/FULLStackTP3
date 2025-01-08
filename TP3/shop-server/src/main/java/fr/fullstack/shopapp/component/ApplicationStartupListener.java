package fr.fullstack.shopapp.component;

import fr.fullstack.shopapp.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartupListener {
    @Autowired
    private ShopService shopService;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        shopService.initializeElasticsearch();
        System.out.println("Boutiques initialisées après le démarrage complet de l'application.");
    }
}
