package com.example.scraper.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class DataImportService {
    private static final String ANS_DATA_URL = "https://dadosabertos.ans.gov.br/FTP/PDA/";
    private static final String DOWNLOAD_DIR = "ans_data";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void downloadAndImportData() throws IOException {
        // Create download directory if it doesn't exist
        Files.createDirectories(Paths.get(DOWNLOAD_DIR));

        // Download last 2 years of financial data
        downloadFinancialData();

        // Download active operators data
        downloadOperatorsData();

        // Create tables
        createTables();

        // Import data
        importFinancialData();
        importOperatorsData();
    }

    private void downloadFinancialData() throws IOException {
        LocalDate now = LocalDate.now();
        for (int i = 0; i < 2; i++) {
            LocalDate date = now.minusYears(i);
            String year = date.format(DateTimeFormatter.ofPattern("yyyy"));
            String url = ANS_DATA_URL + "demonstracoes_contabeis/" + year + "/";

            // This is a simplified version - in a real scenario, you would need to parse the directory
            // and download all files for each quarter
            downloadFile(url + "D1Q" + (i+1) + ".csv", DOWNLOAD_DIR + "/financial_" + year + "_Q" + (i+1) + ".csv");
        }
    }

    private void downloadOperatorsData() throws IOException {
        String url = ANS_DATA_URL + "operadoras_de_plano_de_saude_ativas/Operadoras_ativas.csv";
        downloadFile(url, DOWNLOAD_DIR + "/operators.csv");
    }

    private void downloadFile(String fileUrl, String destinationPath) throws IOException {
        URL url = new URL(fileUrl);
        try (var in = url.openStream()) {
            Files.copy(in, Paths.get(destinationPath), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @Transactional
    public void createTables() {
        // Create financial data table
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS financial_data (
                id SERIAL PRIMARY KEY,
                operator_code VARCHAR(20),
                operator_name VARCHAR(255),
                quarter VARCHAR(10),
                year INTEGER,
                expense_category VARCHAR(255),
                amount DECIMAL(15, 2),
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """);

        // Create operators table
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS operators (
                id SERIAL PRIMARY KEY,
                registry VARCHAR(20),
                cnpj VARCHAR(20),
                legal_name VARCHAR(255),
                trade_name VARCHAR(255),
                modality VARCHAR(100),
                address VARCHAR(255),
                city VARCHAR(100),
                state VARCHAR(2),
                postal_code VARCHAR(10),
                phone VARCHAR(20),
                fax VARCHAR(20),
                email VARCHAR(100),
                representative VARCHAR(255),
                position VARCHAR(100),
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """);
    }

    @Transactional
    public void importFinancialData() throws IOException {
        // Get all financial data files
        Files.list(Paths.get(DOWNLOAD_DIR))
                .filter(path -> path.toString().contains("financial_"))
                .forEach(path -> {
                    try (BufferedReader br = new BufferedReader(new FileReader(path.toFile()))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            String[] values = line.split(";");
                            // Assuming CSV structure: operator_code;operator_name;quarter;year;expense_category;amount
                            jdbcTemplate.update("""
                                INSERT INTO financial_data 
                                (operator_code, operator_name, quarter, year, expense_category, amount) 
                                VALUES (?, ?, ?, ?, ?, ?)
                                """,
                                    values[0], values[1], values[2], Integer.parseInt(values[3]), values[4], Double.parseDouble(values[5]));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    @Transactional
    public void importOperatorsData() throws IOException {
        Path path = Paths.get(DOWNLOAD_DIR + "/operators.csv");
        try (BufferedReader br = new BufferedReader(new FileReader(path.toFile()))) {
            String line;
            // Skip header
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");
                jdbcTemplate.update("""
                    INSERT INTO operators 
                    (registry, cnpj, legal_name, trade_name, modality, address, city, state, postal_code, phone, fax, email, representative, position) 
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """,
                        values[0], values[1], values[2], values[3], values[4], values[5], values[6], values[7], values[8],
                        values[9], values[10], values[11], values[12], values[13]);
            }
        }
    }

    public List<String> getTopOperatorsByExpenseLastQuarter(String category) {
        return jdbcTemplate.queryForList("""
            SELECT operator_name, SUM(amount) as total_amount
            FROM financial_data
            WHERE expense_category = ?
            AND year = EXTRACT(YEAR FROM CURRENT_DATE)
            AND quarter = CONCAT('Q', EXTRACT(QUARTER FROM CURRENT_DATE) - 1)
            GROUP BY operator_name
            ORDER BY total_amount DESC
            LIMIT 10
            """, String.class, category);
    }

    public List<String> getTopOperatorsByExpenseLastYear(String category) {
        return jdbcTemplate.queryForList("""
            SELECT operator_name, SUM(amount) as total_amount
            FROM financial_data
            WHERE expense_category = ?
            AND year = EXTRACT(YEAR FROM CURRENT_DATE) - 1
            GROUP BY operator_name
            ORDER BY total_amount DESC
            LIMIT 10
            """, String.class, category);
    }
}