package com.example.scraper.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/operators")
public class OperatorController {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public OperatorController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/search")
    public List<Map<String, Object>> searchOperators(@RequestParam String query) {
        String searchQuery = "%" + query.toLowerCase() + "%";
        return jdbcTemplate.queryForList("""
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
            """, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery);
    }
}