#!/bin/bash

# Configurações
DB_NAME="ans_db"
DB_USER="postgres"
DB_PASS="password"
DATA_DIR="data"

# Verifica se o diretório de dados existe
if [ ! -d "$DATA_DIR" ]; then
    echo "Diretório $DATA_DIR não encontrado. Criando..."
    mkdir -p $DATA_DIR
fi

# Download dos dados
echo "Baixando dados das operadoras..."
wget -q -O $DATA_DIR/operadoras.csv https://dadosabertos.ans.gov.br/FTP/PDA/operadoras_de_plano_de_saude_ativas/Operadoras_ativas.csv

# Download dos dados financeiros (últimos 2 anos)
CURRENT_YEAR=$(date +%Y)
for year in $(seq $((CURRENT_YEAR - 1)) $CURRENT_YEAR); do
    echo "Baixando dados financeiros de $year..."
    wget -q -r -np -nH --cut-dirs=3 -P $DATA_DIR/ https://dadosabertos.ans.gov.br/FTP/PDA/demonstracoes_contabeis/$year/
done

# Processamento e importação dos dados
echo "Importando dados para o banco de dados..."

# Importa operadoras
psql -h localhost -U $DB_USER -d $DB_NAME -c "\copy ans.operators(registry, cnpj, legal_name, trade_name, modality, address, city, state, postal_code, phone, fax, email, representative, position) FROM '$DATA_DIR/operadoras.csv' DELIMITER ';' CSV HEADER ENCODING 'UTF-8';"

# Importa dados financeiros
for file in $(find $DATA_DIR -name "*.csv" | grep -i "demonstracoes"); do
    echo "Processando arquivo $file..."
    psql -h localhost -U $DB_USER -d $DB_NAME -c "\copy ans.financial_data(operator_id, quarter, year, expense_category, amount) FROM '$file' DELIMITER ';' CSV HEADER ENCODING 'UTF-8';"
done

echo "Importação de dados concluída com sucesso!"