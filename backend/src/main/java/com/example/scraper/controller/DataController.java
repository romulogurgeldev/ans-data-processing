package com.example.scraper.controller;

import com.example.scraper.service.DataImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/data")
public class DataController {
    private final DataImportService dataImportService;

    @Autowired
    public DataController(DataImportService dataImportService) {
        this.dataImportService = dataImportService;
    }

    @GetMapping("/import")
    public String importData() {
        try {
            dataImportService.downloadAndImportData();
            return "Data imported successfully!";
        } catch (IOException e) {
            e.printStackTrace();
            return "Error occurred: " + e.getMessage();
        }
    }

    @GetMapping("/top-operators/quarter")
    public List<String> getTopOperatorsLastQuarter(@RequestParam String category) {
        return dataImportService.getTopOperatorsByExpenseLastQuarter(category);
    }

    @GetMapping("/top-operators/year")
    public List<String> getTopOperatorsLastYear(@RequestParam String category) {
        return dataImportService.getTopOperatorsByExpenseLastYear(category);
    }
}