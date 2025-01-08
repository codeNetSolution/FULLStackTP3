package fr.fullstack.shopapp;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ShopAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopAppApplication.class, args);
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Shop Application API")
                        .version("1.0.0")
                        .description("Documentation of the ShopApp APIs"));
    }

    @Bean
    public GroupedOpenApi shopApi() {
        return GroupedOpenApi.builder()
                .group("shop-app")
                .packagesToScan("fr.fullstack.shopapp.controller")
                .build();
    }
}
