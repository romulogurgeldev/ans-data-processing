#!/bin/bash

# Configuração do banco de dados
DB_NAME="ans_db"
DB_USER="ans_user"
DB_PASS="password"
DB_HOST="localhost"
SQL_SCRIPT="database/create_tables.sql"

echo "📌 Iniciando configuração do banco de dados ANS..."

# Verifica se o PostgreSQL está instalado
if ! command -v psql &> /dev/null; then
    echo "❌ PostgreSQL não está instalado. Instale primeiro."
    exit 1
fi

# Verifica se o banco de dados já existe
DB_EXISTS=$(sudo -u postgres psql -tAc "SELECT 1 FROM pg_database WHERE datname='$DB_NAME'")
if [ "$DB_EXISTS" == "1" ]; then
    echo "✅ Banco de dados $DB_NAME já existe. Pulando criação..."
else
    echo "📌 Criando banco de dados $DB_NAME..."
    sudo -u postgres psql <<EOF
    CREATE DATABASE $DB_NAME;
    CREATE USER $DB_USER WITH PASSWORD '$DB_PASS';
    GRANT ALL PRIVILEGES ON DATABASE $DB_NAME TO $DB_USER;
EOF
    echo "✅ Banco de dados e usuário configurados com sucesso!"
fi

# Verifica se o script SQL existe antes de executar
if [ ! -f "$SQL_SCRIPT" ]; then
    echo "❌ O arquivo $SQL_SCRIPT não foi encontrado! Verifique o caminho."
    exit 1
fi

# Define senha temporária apenas para execução
export PGPASSWORD="$DB_PASS"

# Executa scripts SQL
echo "📥 Executando script de criação de tabelas..."
psql -h "$DB_HOST" -U "$DB_USER" -d "$DB_NAME" -f "$SQL_SCRIPT"

if [ $? -eq 0 ]; then
    echo "✅ Banco de dados configurado com sucesso!"
else
    echo "❌ Erro ao executar o script SQL."
    exit 1
fi

# Limpa a variável de ambiente por segurança
unset PGPASSWORD
