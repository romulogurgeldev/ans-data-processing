package com.example.scraper.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OperatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JdbcTemplate jdbcTemplate;

    @Test
    void searchOperators_shouldReturnOperators_whenQueryMatches() throws Exception {
        // Arrange
        String testQuery = "test";
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

        // Corrigindo o mock para retornar a lista de Map<String, Object> corretamente
        when(jdbcTemplate.queryForList(anyString(), eq(testQuery), eq(testQuery), eq(testQuery), eq(testQuery), eq(testQuery)))
                .thenReturn(mockResults);

        // Act & Assert
        mockMvc.perform(get("/api/operators/search")
                        .param("query", testQuery))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].legal_name", is("Test Operator")))
                .andExpect(jsonPath("$[0].cnpj", is("00.000.000/0001-00")));

        verify(jdbcTemplate).queryForList(anyString(), eq(testQuery), eq(testQuery), eq(testQuery), eq(testQuery), eq(testQuery));
    }

    @Test
    void searchOperators_shouldReturnEmptyList_whenNoResults() throws Exception {
        // Arrange
        when(jdbcTemplate.queryForList(anyString(), any(), any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/operators/search")
                        .param("query", "nonexistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", empty()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "a"})
    void searchOperators_shouldReturnBadRequest_whenQueryTooShort(String query) throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/operators/search")
                        .param("query", query))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchOperators_shouldReturnInternalError_whenDatabaseFails() throws Exception {
        // Arrange
        when(jdbcTemplate.queryForList(anyString(), any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(get("/api/operators/search")
                        .param("query", "error"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message", containsString("Database error")));
    }
}
