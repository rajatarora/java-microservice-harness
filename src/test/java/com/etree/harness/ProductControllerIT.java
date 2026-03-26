package com.etree.harness;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.etree.harness.example.dto.ProductCreateDto;
import com.etree.harness.example.dto.ProductResponseDto;
import com.etree.harness.example.dto.ProductUpdateDto;
import com.etree.harness.example.repository.ProductRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ProductControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    private ObjectMapper objectMapper;

    @BeforeAll
    void initMapper() {
                objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());
                objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    private ProductCreateDto sampleCreateDto() {
        ProductCreateDto dto = new ProductCreateDto();
        dto.setName("Widget");
        dto.setDescription("A useful widget");
        dto.setPrice(BigDecimal.valueOf(9.99));
        dto.setStock(10);
        return dto;
    }

    @Test
    void createProduct_success() throws Exception {
        ProductCreateDto dto = sampleCreateDto();

        String payload = objectMapper.writeValueAsString(dto);

        String location = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn().getResponse().getHeader("Location");

        assertThat(location).isNotNull();
        // id present in location
        assertThat(location).contains("/api/v1/products/");
    }

    @Test
    void createProduct_validationErrors() throws Exception {
        ProductCreateDto dto = sampleCreateDto();
        dto.setName(""); // NotBlank violation
        String payload = objectMapper.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("name")));
    }

    @Test
    void getProduct_notFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/99999"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void getProduct_success() throws Exception {
        ProductCreateDto dto = sampleCreateDto();
        String payload = objectMapper.writeValueAsString(dto);

        String response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn().getResponse().getContentAsString();

        ProductResponseDto created = objectMapper.readValue(response, ProductResponseDto.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/" + created.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(created.getId()));
    }

    @Test
    void listProducts_returnsAll() throws Exception {
        ProductCreateDto a = sampleCreateDto();
        a.setName("A");
        ProductCreateDto b = sampleCreateDto();
        b.setName("B");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(a)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(b)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0]").exists());
    }

    @Test
    void replaceProduct_success_and_notFound() throws Exception {
        ProductCreateDto dto = sampleCreateDto();
        String createdJson = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn().getResponse().getContentAsString();

        ProductResponseDto created = objectMapper.readValue(createdJson, ProductResponseDto.class);

        ProductCreateDto replacement = sampleCreateDto();
        replacement.setName("Replaced");

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/products/" + created.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(replacement)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Replaced"));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/products/99999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(replacement)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void patchProduct_partialUpdate_and_validation() throws Exception {
        ProductCreateDto dto = sampleCreateDto();
        String createdJson = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn().getResponse().getContentAsString();

        ProductResponseDto created = objectMapper.readValue(createdJson, ProductResponseDto.class);

        ProductUpdateDto update = new ProductUpdateDto();
        update.setDescription("Updated desc");

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/products/" + created.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Updated desc"));

        update.setStock(-1);
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/products/" + created.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void deleteProduct_success_and_notFound() throws Exception {
        ProductCreateDto dto = sampleCreateDto();
        String createdJson = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn().getResponse().getContentAsString();

        ProductResponseDto created = objectMapper.readValue(createdJson, ProductResponseDto.class);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/products/" + created.getId()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/products/99999"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

}
