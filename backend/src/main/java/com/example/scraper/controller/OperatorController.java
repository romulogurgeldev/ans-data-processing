package com.example.scraper.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class OperatorController {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public OperatorController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/operators/search")
    public ResponseEntity<?> searchOperators(@RequestParam String query) {
        if (query.length() < 2) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Query must be at least 2 characters long.");
        }

        try {
            String searchQuery = "%" + query.toLowerCase() + "%";
            List<Map<String, Object>> result = jdbcTemplate.queryForList(
                    """
                    SELECT id, registry, cnpj, legal_name, trade_name, modality, city, state
                    FROM operators
                    WHERE LOWER(legal_name) LIKE ? OR LOWER(trade_name) LIKE ? OR registry LIKE ?
                    ORDER BY 
                        CASE 
                            WHEN LOWER(legal_name) LIKE ? THEN 0 
                            WHEN LOWER(trade_name) LIKE ? THEN 1 
                            ELSE 2 
                        END
                    LIMIT 50
                    """,
                    searchQuery, searchQuery, searchQuery, searchQuery, searchQuery
            );

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Database error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
