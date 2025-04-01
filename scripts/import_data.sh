#!/bin/bash

# Configurações
DB_NAME="ans_db"
DB_USER="postgres"
DB_HOST="localhost"
DATA_DIR="ans_data"

# Evita expor senha no script (use .pgpass ou export PGPASSWORD="sua_senha" antes de rodar)
export PGPASSWORD="password"

# Criar diretório se não existir
if [ ! -d "$DATA_DIR" ]; then
    echo "📁 Criando diretório $DATA_DIR..."
    mkdir -p "$DATA_DIR"
fi

# Download dos dados das operadoras
echo "⬇️ Baixando dados das operadoras..."
wget -q --show-progress -O "$DATA_DIR/operadoras.csv" https://dadosabertos.ans.gov.br/FTP/PDA/operadoras_de_plano_de_saude_ativas/Operadoras_ativas.csv
if [ $? -ne 0 ]; then
    echo "❌ Erro ao baixar os dados das operadoras."
    exit 1
fi

# Download dos dados financeiros (últimos 2 anos)
CURRENT_YEAR=$(date +%Y)
for year in $(seq $((CURRENT_YEAR - 1)) $CURRENT_YEAR); do
    echo "⬇️ Baixando dados financeiros de $year..."

    wget -q --show-progress -P "$DATA_DIR/" -A "*.csv" "https://dadosabertos.ans.gov.br/FTP/PDA/demonstracoes_contabeis/$year/"

    if [ $? -ne 0 ]; then
        echo "⚠️ Erro ao baixar os dados financeiros de $year. Continuando..."
    fi
done

# Importação para o PostgreSQL
echo "📥 Iniciando importação para o banco de dados..."

# Verifica conexão antes de importar
psql -h "$DB_HOST" -U "$DB_USER" -d "$DB_NAME" -c "SELECT 1;" &> /dev/null
if [ $? -ne 0 ]; then
    echo "❌ Erro: Não foi possível conectar ao banco de dados."
    exit 1
fi

# Importação de Operadoras
echo "📌 Importando dados das operadoras..."
psql -h "$DB_HOST" -U "$DB_USER" -d "$DB_NAME" -c "\copy ans.operators(registry, cnpj, legal_name, trade_name, modality, address, city, state, postal_code, phone, fax, email, representative, position) FROM '$DATA_DIR/operadoras.csv' DELIMITER ';' CSV HEADER ENCODING 'UTF-8';"

if [ $? -ne 0 ]; then
    echo "❌ Erro ao importar operadoras."
    exit 1
fi

# Importação de Dados Financeiros
echo "📊 Importando dados financeiros..."
for file in $(find "$DATA_DIR" -type f -name "*.csv" | grep -i "demonstracoes"); do
    echo "📄 Processando $file..."

    psql -h "$DB_HOST" -U "$DB_USER" -d "$DB_NAME" -c "\copy ans.financial_data(operator_id, quarter, year, expense_category, amount) FROM '$file' DELIMITER ';' CSV HEADER ENCODING 'UTF-8';"

    if [ $? -ne 0 ]; then
        echo "⚠️ Erro ao importar $file. Continuando..."
    fi
done

echo "✅ Importação concluída com sucesso!"
