package com.example.scraper.controller;

import com.example.scraper.service.PdfScraperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/scrape")
public class ScraperController {
    private final PdfScraperService pdfScraperService;

    @Autowired
    public ScraperController(PdfScraperService pdfScraperService) {
        this.pdfScraperService = pdfScraperService;
    }

    @GetMapping
    public String scrapeAndDownload() {
        try {
            pdfScraperService.downloadAndZipPdfs();
            return "PDFs downloaded and zipped successfully!";
        } catch (IOException e) {
            e.printStackTrace();
            return "Error occurred: " + e.getMessage();
        }
    }
}