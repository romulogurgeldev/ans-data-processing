#!/bin/bash

# Configuração do banco de dados
DB_NAME="ans_db"
DB_USER="postgres"
DB_PASS="password"
DB_HOST="localhost"

echo "Configurando banco de dados ANS..."

# Verifica se o PostgreSQL está instalado
if ! command -v psql &> /dev/null; then
    echo "PostgreSQL não está instalado. Por favor, instale primeiro."
    exit 1
fi

# Cria o banco de dados
echo "Criando banco de dados $DB_NAME..."
sudo -u postgres psql -c "CREATE DATABASE $DB_NAME;"

# Configura usuário e permissões
echo "Configurando usuário e permissões..."
sudo -u postgres psql -c "CREATE USER $DB_USER WITH PASSWORD '$DB_PASS';"
sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE $DB_NAME TO $DB_USER;"

# Executa scripts SQL
echo "Executando scripts de criação de tabelas..."
psql -h $DB_HOST -U $DB_USER -d $DB_NAME -f database/create_tables.sql

echo "Configuração do banco de dados concluída com sucesso!"