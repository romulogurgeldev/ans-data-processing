package com.example.scraper.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class PdfScraperService {
    private static final String TARGET_URL = "https://www.gov.br/ans/pt-br/acesso-a-informacao/participacao-da-sociedade/atualizacao-do-rol-de-procedimentos";
    private static final String OUTPUT_DIR = "downloads";
    private static final String ZIP_FILE = "anexos.zip";

    public void downloadAndZipPdfs() throws IOException {
        // Create output directory if it doesn't exist
        Files.createDirectories(Paths.get(OUTPUT_DIR));

        // Connect to the website and get the document
        Document doc = Jsoup.connect(TARGET_URL).get();

        // Find all PDF links (more flexible selection)
        Elements pdfLinks = doc.select("a[href$=.pdf]");
        List<String> downloadedFiles = new ArrayList<>();

        for (Element link : pdfLinks) {
            String href = link.attr("href");
            String fileName = href.substring(href.lastIndexOf('/') + 1);

            // We're interested in Anexo I and Anexo II
            if (fileName.contains("Anexo I") || fileName.contains("Anexo II")) {
                String filePath = downloadPdf(href, fileName);
                downloadedFiles.add(filePath);
            }
        }

        // Zip the downloaded files
        if (!downloadedFiles.isEmpty()) {
            createZipFile(downloadedFiles);
        }
    }

    private String downloadPdf(String pdfUrl, String fileName) throws IOException {
        String filePath = OUTPUT_DIR + "/" + fileName;
        try (BufferedInputStream in = new BufferedInputStream(new URL(pdfUrl).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {

            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        }
        return filePath;
    }

    private void createZipFile(List<String> files) throws IOException {
        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(OUTPUT_DIR + "/" + ZIP_FILE))) {
            for (String filePath : files) {
                Path path = Paths.get(filePath);
                ZipEntry zipEntry = new ZipEntry(path.getFileName().toString());
                zipOut.putNextEntry(zipEntry);
                Files.copy(path, zipOut);
                zipOut.closeEntry();
            }
        }
    }
}