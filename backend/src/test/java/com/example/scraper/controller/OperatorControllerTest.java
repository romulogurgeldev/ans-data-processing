// src/test/java/com/example/scraper/controller/OperatorControllerTest.java
package com.example.scraper.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OperatorControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JdbcTemplate jdbcTemplate;

    @Test
    void testSearchOperators() throws Exception {
        List<Map<String, Object>> mockResults = Collections.singletonList(
                Map.of(
                        "id", 1,
                        "registry", "123",
                        "cnpj", "00.000.000/0001-00",
                        "legal_name", "Test Operator",
                        "trade_name", "Test",
                        "modality", "Health",
                        "city", "São Paulo",
                        "state", "SP"
                )
        );

        when(jdbcTemplate.queryForList(anyString(), eq("test"))).thenReturn(mockResults);

        mockMvc.perform(get("/api/operators/search").param("query", "test"))
                .andExpect(status().isOk());
    }
}