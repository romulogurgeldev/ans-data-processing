-- Criação do schema
CREATE SCHEMA IF NOT EXISTS ans;

-- Tabela de operadoras
CREATE TABLE IF NOT EXISTS ans.operators (
                                             id SERIAL PRIMARY KEY,
                                             registry VARCHAR(20) NOT NULL,
    cnpj VARCHAR(20) NOT NULL,
    legal_name VARCHAR(255) NOT NULL,
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
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_operator_registry UNIQUE (registry)
    );

-- Tabela de dados financeiros
CREATE TABLE IF NOT EXISTS ans.financial_data (
                                                  id SERIAL PRIMARY KEY,
                                                  operator_id INTEGER REFERENCES ans.operators(id),
    quarter VARCHAR(10) NOT NULL,
    year INTEGER NOT NULL,
    expense_category VARCHAR(255) NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_financial_record UNIQUE (operator_id, quarter, year, expense_category)
    );

-- Índices para otimização
CREATE INDEX IF NOT EXISTS idx_financial_data_category ON ans.financial_data(expense_category);
CREATE INDEX IF NOT EXISTS idx_financial_data_period ON ans.financial_data(year, quarter);