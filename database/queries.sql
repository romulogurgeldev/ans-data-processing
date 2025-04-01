-- Consulta 1: Top 10 operadoras com maiores despesas no último trimestre
SELECT
    o.trade_name AS operator_name,
    SUM(f.amount) AS total_expenses
FROM
    ans.financial_data f
        JOIN
    ans.operators o ON f.operator_id = o.id
WHERE
    f.expense_category = 'EVENTOS/SINISTROS CONHECIDOS OU AVISADOS DE ASSISTÊNCIA A SAÚDE MÉDICO HOSPITALAR'
  AND f.year = EXTRACT(YEAR FROM CURRENT_DATE)
  AND f.quarter = CONCAT('Q', EXTRACT(QUARTER FROM CURRENT_DATE) - 1)
GROUP BY
    o.trade_name
ORDER BY
    total_expenses DESC
    LIMIT 10;

-- Consulta 2: Top 10 operadoras com maiores despesas no último ano
SELECT
    o.trade_name AS operator_name,
    SUM(f.amount) AS total_expenses
FROM
    ans.financial_data f
        JOIN
    ans.operators o ON f.operator_id = o.id
WHERE
    f.expense_category = 'EVENTOS/SINISTROS CONHECIDOS OU AVISADOS DE ASSISTÊNCIA A SAÚDE MÉDICO HOSPITALAR'
  AND f.year = EXTRACT(YEAR FROM CURRENT_DATE) - 1
GROUP BY
    o.trade_name
ORDER BY
    total_expenses DESC
    LIMIT 10;

-- Consulta 3: Visão analítica consolidada (extra)
SELECT
    f.year,
    f.quarter,
    o.modality,
    COUNT(DISTINCT o.id) AS operators_count,
    SUM(f.amount) AS total_expenses,
    AVG(f.amount) AS avg_expenses
FROM
    ans.financial_data f
        JOIN
    ans.operators o ON f.operator_id = o.id
WHERE
    f.expense_category = 'EVENTOS/SINISTROS CONHECIDOS OU AVISADOS DE ASSISTÊNCIA A SAÚDE MÉDICO HOSPITALAR'
GROUP BY
    f.year, f.quarter, o.modality
ORDER BY
    f.year DESC, f.quarter DESC, total_expenses DESC;