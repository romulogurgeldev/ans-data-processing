package com.example.scraper.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class PdfToCsvService {
    private static final String OUTPUT_DIR = "csv_output";
    private static final String CSV_FILE = "procedimentos.csv";
    private static final String ZIP_FILE = "Teste_Seu_Nome.zip";

    public void convertPdfToCsv(String pdfPath) throws IOException {
        // Create output directory if it doesn't exist
        Files.createDirectories(Paths.get(OUTPUT_DIR));

        // Load PDF document
        PDDocument document = PDDocument.load(new File(pdfPath));
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(document);
        document.close();

        // Process the text to extract table data
        List<String[]> tableData = extractTableData(text);

        // Write to CSV
        writeCsvFile(tableData);

        // Create zip file
        createZipFile();
    }

    private List<String[]> extractTableData(String text) {
        List<String[]> tableData = new ArrayList<>();
        String[] lines = text.split("\\r?\\n");

        // Pattern to match table rows (adjust according to actual PDF structure)
        Pattern rowPattern = Pattern.compile("^(\\d+)\\s+(.+?)\\s+(OD|AMB|...)\\s+(.*)$");

        for (String line : lines) {
            Matcher matcher = rowPattern.matcher(line);
            if (matcher.find()) {
                String[] row = new String[4];
                row[0] = matcher.group(1); // Code
                row[1] = matcher.group(2).trim(); // Description
                row[2] = expandAbbreviation(matcher.group(3)); // Type (expanded)
                row[3] = matcher.group(4).trim(); // Additional info
                tableData.add(row);
            }
        }

        return tableData;
    }

    private String expandAbbreviation(String abbrev) {
        switch (abbrev) {
            case "OD":
                return "Odontológico";
            case "AMB":
                return "Ambulatorial";
            // Add more abbreviations as needed
            default:
                return abbrev;
        }
    }

    private void writeCsvFile(List<String[]> data) throws IOException {
        try (FileWriter writer = new FileWriter(OUTPUT_DIR + "/" + CSV_FILE)) {
            // Write header
            writer.write("Código,Descrição,Tipo,Informações Adicionais\n");

            // Write data
            for (String[] row : data) {
                writer.write(String.join(",", row) + "\n");
            }
        }
    }

    private void createZipFile() throws IOException {
        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(OUTPUT_DIR + "/" + ZIP_FILE))) {
            Path path = Paths.get(OUTPUT_DIR + "/" + CSV_FILE);
            ZipEntry zipEntry = new ZipEntry(path.getFileName().toString());
            zipOut.putNextEntry(zipEntry);
            Files.copy(path, zipOut);
            zipOut.closeEntry();
        }
    }
}