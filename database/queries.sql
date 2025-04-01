-- Consulta 1: Top 10 operadoras com maiores despesas no último trimestre
WITH last_quarter AS (
    SELECT
        CASE
            WHEN EXTRACT(QUARTER FROM CURRENT_DATE) = 1 THEN EXTRACT(YEAR FROM CURRENT_DATE) - 1
            ELSE EXTRACT(YEAR FROM CURRENT_DATE)
            END AS target_year,
        CASE
            WHEN EXTRACT(QUARTER FROM CURRENT_DATE) = 1 THEN 'Q4'
            ELSE CONCAT('Q', EXTRACT(QUARTER FROM CURRENT_DATE) - 1)
            END AS target_quarter
)
SELECT
    o.trade_name AS operator_name,
    COALESCE(SUM(f.amount), 0) AS total_expenses
FROM
    ans.financial_data f
        JOIN ans.operators o ON f.operator_id = o.id
        JOIN last_quarter lq ON f.year = lq.target_year AND f.quarter = lq.target_quarter
WHERE
    f.expense_category = 'EVENTOS/SINISTROS CONHECIDOS OU AVISADOS DE ASSISTÊNCIA A SAÚDE MÉDICO HOSPITALAR'
GROUP BY
    o.trade_name
ORDER BY
    total_expenses DESC
    LIMIT 10;


-- Consulta 2: Top 10 operadoras com maiores despesas no último ano
SELECT
    o.trade_name AS operator_name,
    COALESCE(SUM(f.amount), 0) AS total_expenses
FROM
    ans.financial_data f
        JOIN ans.operators o ON f.operator_id = o.id
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
    COALESCE(SUM(f.amount), 0) AS total_expenses,
    COALESCE(AVG(f.amount), 0) AS avg_expenses
FROM
    ans.financial_data f
        JOIN ans.operators o ON f.operator_id = o.id
WHERE
    f.expense_category = 'EVENTOS/SINISTROS CONHECIDOS OU AVISADOS DE ASSISTÊNCIA A SAÚDE MÉDICO HOSPITALAR'
GROUP BY
    f.year, f.quarter, o.modality
ORDER BY
    f.year DESC,
    CASE f.quarter
        WHEN 'Q4' THEN 4
        WHEN 'Q3' THEN 3
        WHEN 'Q2' THEN 2
        WHEN 'Q1' THEN 1
        ELSE 0
        END DESC,
    total_expenses DESC;
