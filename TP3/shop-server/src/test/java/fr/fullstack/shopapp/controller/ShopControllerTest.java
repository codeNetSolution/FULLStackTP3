package fr.fullstack.shopapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.fullstack.shopapp.model.Shop;
import fr.fullstack.shopapp.model.ShopElestack;
import fr.fullstack.shopapp.service.ShopService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShopController.class)
@TestPropertySource(properties = {
        "spring.profiles.active=test"
})
class ShopControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ShopService shopService;

    @BeforeEach
    void setUp() {
        Mockito.reset(shopService);
    }

    @Test
    void testCreateShop() throws Exception {
        Shop shop = new Shop();
        shop.setName("Shop A");
        shop.setInVacations(false);
        shop.setNbProducts(0L);

        Mockito.when(shopService.createShop(any(Shop.class))).thenReturn(shop);

        mockMvc.perform(post("/api/v1/shops")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shop)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Shop A")))
                .andExpect(jsonPath("$.inVacations", is(false)));
    }

    @Test
    void testGetShopById() throws Exception {
        Shop shop = new Shop();
        shop.setId(1L);
        shop.setName("Shop B");
        shop.setNbProducts(10L);

        Mockito.when(shopService.getShopById(1L)).thenReturn(shop);

        mockMvc.perform(get("/api/v1/shops/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Shop B")));
    }

    @Test
    void testGetAllShops() throws Exception {
        Shop shop1 = new Shop();
        shop1.setId(1L);
        shop1.setName("Shop C");
        shop1.setNbProducts(5L);

        Shop shop2 = new Shop();
        shop2.setId(2L);
        shop2.setName("Shop D");
        shop2.setNbProducts(10L);

        List<Shop> shops = Arrays.asList(shop1, shop2);

        Mockito.when(shopService.getShopList(any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(shops));

        mockMvc.perform(get("/api/v1/shops"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name", is("Shop C")))
                .andExpect(jsonPath("$.content[1].name", is("Shop D")));
    }


    @Test
    void testUpdateShop() throws Exception {
        Shop updatedShop = new Shop();
        updatedShop.setId(1L);
        updatedShop.setName("Updated Shop");
        updatedShop.setNbProducts(0L);

        Mockito.when(shopService.updateShop(any(Shop.class))).thenReturn(updatedShop);

        mockMvc.perform(put("/api/v1/shops")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedShop)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Shop")));
    }

    @Test
    void testDeleteShop() throws Exception {
        Mockito.doNothing().when(shopService).deleteShopById(1L);

        mockMvc.perform(delete("/api/v1/shops/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void testSearchShops() throws Exception {
        ShopElestack shopElestack = new ShopElestack();
        shopElestack.setName("Search Shop");
        shopElestack.setNbProducts(5L);

        Mockito.when(shopService.searchShops(eq("Search Shop"), eq(false), eq(LocalDate.of(2023, 1, 1)), eq(LocalDate.of(2023, 12, 31))))
                .thenReturn(Collections.singletonList(shopElestack));

        mockMvc.perform(get("/api/v1/shops/search")
                        .param("name", "Search Shop")
                        .param("inVacations", "false")
                        .param("startDate", "2023-01-01")
                        .param("endDate", "2023-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("Search Shop")));
    }
}
