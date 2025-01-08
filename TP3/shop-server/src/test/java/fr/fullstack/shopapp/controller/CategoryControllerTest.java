package fr.fullstack.shopapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.fullstack.shopapp.model.Category;
import fr.fullstack.shopapp.service.CategoryService;
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
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateCategory() throws Exception {
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        Mockito.when(categoryService.createCategory(any(Category.class))).thenReturn(category);

        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Electronics")));
    }

    @Test
    void testDeleteCategory() throws Exception {
        Mockito.doNothing().when(categoryService).deleteCategoryById(1L);

        mockMvc.perform(delete("/api/v1/categories/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetCategoryById() throws Exception {
        Category category = new Category();
        category.setId(1L);
        category.setName("Home Appliances");

        Mockito.when(categoryService.getCategoryById(1L)).thenReturn(category);

        mockMvc.perform(get("/api/v1/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Home Appliances")));
    }

    @Test
    void testGetAllCategories() throws Exception {
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Books");

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Clothing");

        Page<Category> categories = new PageImpl<>(Arrays.asList(category1, category2));

        Mockito.when(categoryService.getCategoryList(any(PageRequest.class))).thenReturn(categories);

        mockMvc.perform(get("/api/v1/categories")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name", is("Books")))
                .andExpect(jsonPath("$.content[1].name", is("Clothing")));
    }

    @Test
    void testUpdateCategory() throws Exception {
        Category category = new Category();
        category.setId(1L);
        category.setName("Sports");

        Mockito.when(categoryService.updateCategory(any(Category.class))).thenReturn(category);

        mockMvc.perform(put("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Sports")));
    }
}
