-- Criação do schema
CREATE SCHEMA IF NOT EXISTS ans;

-- Tabela de operadoras
CREATE TABLE IF NOT EXISTS ans.operators (
                                             id SERIAL PRIMARY KEY,
                                             registry VARCHAR(20) NOT NULL UNIQUE,
    cnpj VARCHAR(18) NOT NULL UNIQUE,  -- 14 dígitos + formatação (XX.XXX.XXX/XXXX-XX)
    legal_name VARCHAR(255) NOT NULL,
    trade_name VARCHAR(255),
    modality VARCHAR(100),
    address VARCHAR(255),
    city VARCHAR(100),
    state CHAR(2) CHECK (state ~ '^[A-Z]{2}$'), -- Valida apenas siglas de estados
    postal_code VARCHAR(9) CHECK (postal_code ~ '^\d{5}-\d{3}$'), -- Validação de CEP (XXXXX-XXX)
    phone VARCHAR(20),
    fax VARCHAR(20),
    email VARCHAR(100) CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'), -- Valida formato de e-mail
    representative VARCHAR(255),
    position VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- Índice para melhorar buscas por CNPJ
CREATE INDEX IF NOT EXISTS idx_operators_cnpj ON ans.operators(cnpj);

-- Tabela de dados financeiros
CREATE TABLE IF NOT EXISTS ans.financial_data (
                                                  id SERIAL PRIMARY KEY,
                                                  operator_id INTEGER NOT NULL REFERENCES ans.operators(id) ON DELETE CASCADE,
    quarter VARCHAR(10) NOT NULL CHECK (quarter IN ('Q1', 'Q2', 'Q3', 'Q4')), -- Apenas trimestres válidos
    year INTEGER NOT NULL CHECK (year >= 2000 AND year <= EXTRACT(YEAR FROM CURRENT_DATE)), -- Restringe anos inválidos
    expense_category VARCHAR(255) NOT NULL,
    amount DECIMAL(15, 2) NOT NULL CHECK (amount >= 0), -- Evita valores negativos
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_financial_record UNIQUE (operator_id, quarter, year, expense_category)
    );

-- Índices para otimização de consultas
CREATE INDEX IF NOT EXISTS idx_financial_data_category ON ans.financial_data(expense_category);
CREATE INDEX IF NOT EXISTS idx_financial_data_period ON ans.financial_data(year, quarter);
CREATE INDEX IF NOT EXISTS idx_financial_data_operator ON ans.financial_data(operator_id);
