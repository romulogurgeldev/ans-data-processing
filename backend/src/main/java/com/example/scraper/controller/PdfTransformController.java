package com.example.scraper.controller;

import com.example.scraper.service.PdfToCsvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/transform")
public class PdfTransformController {
    private final PdfToCsvService pdfToCsvService;

    @Autowired
    public PdfTransformController(PdfToCsvService pdfToCsvService) {
        this.pdfToCsvService = pdfToCsvService;
    }

    @PostMapping
    public String transformPdfToCsv(@RequestParam String pdfPath) {
        try {
            pdfToCsvService.convertPdfToCsv(pdfPath);
            return "PDF transformed to CSV and zipped successfully!";
        } catch (IOException e) {
            e.printStackTrace();
            return "Error occurred: " + e.getMessage();
        }
    }
}