Arquitetura do Sistema ANS Data Processing
Autor: Rômulo Régis Gurgel
Última Atualização: {{31-03-2025}}

🔍 Visão Geral
Sistema de processamento de dados da ANS com arquitetura moderna e containerizada, projetado para:

Coleta automatizada de dados públicos

Transformação eficiente de documentos PDF

Armazenamento otimizado para análises

Exposição segura via API REST e interface web

📐 Diagrama de Componentes
mermaid
Copy
graph TD  
A[Frontend Vue.js] -->|HTTP/API| B[Backend Spring Boot]  
B -->|JDBC| C[(PostgreSQL)]  
B -->|HTTP Client| D[ANS Open Data]  
B -->|JSoup| E[Gov.br]  
C -->|Relatórios| F[Metabase*]  
G[Usuário] -->|Interface| A  
H[Sistema Externo] -->|API REST| B

    classDef tool fill:#f9f,stroke:#333;  
    class F tool;  
*Opcional: Ferramenta de BI para análises avançadas

⚙️ Fluxo de Dados
1. Coleta
   Fonte	Tecnologia	Saída
   Portal Gov.br	JSoup (Scraping)	PDFs (Anexos I/II)
   Dados Abertos ANS	HTTP Client	CSVs/JSON
2. Processamento
   mermaid
   Copy
   flowchart LR  
   A[PDF] --> B(PDFBox)  
   B --> C[CSV Bruto]  
   C --> D{Normalização}  
   D -->|OD → Odontológico| E[CSV Estruturado]  
   D -->|AMB → Ambulatorial| E
3. Armazenamento
   PostgreSQL 13+ com:

Schema dedicado (ans)

Índices otimizados

Particionamento por período (opcional)

4. Exposição
   API REST:

Documentação Swagger (/swagger-ui.html)

Versionamento (/v1/endpoints)

Frontend:

Vue 3 Composition API

Axios para chamadas HTTP

🛠 Stack Tecnológica
Camada	Tecnologias
Frontend	Vue 3, Pinia, Axios, TailwindCSS
Backend	Spring Boot 3, JPA/Hibernate, JSoup, PDFBox, SpringDoc (OpenAPI)
Banco	PostgreSQL 13, Flyway (migrações), QueryDSL (consultas complexas)
Infra	Docker, Docker Compose, Nginx, Prometheus (monitoramento)
Qualidade	JUnit 5, Mockito, Postman (testes), SonarQube*
*Recomendado para análise estática de código

🔗 Dependências Externas
Serviço	Tipo	Autenticação
dados.ans.gov.br	API REST	Token (opcional)
gov.br	Web Scraping	-
📊 Decisões Arquiteturais
Containerização:

Isolamento completo dos serviços

Portas expostas:

8080 → Backend

8081 → Frontend

5432 → PostgreSQL

Resiliência:

Retry automático para requests HTTP

Circuit Breaker (Spring Cloud CircuitBreaker)

Segurança:

CORS configurado por ambiente

HTTPS obrigatório em produção

Monitoramento:

Endpoints Actuator (/actuator)

Métricas no formato Prometheus

📌 Próximas Evoluções
Adicionar Kafka para processamento assíncrono

Implementar cache com Redis

Dashboard de métricas com Grafana

📄 Versão: 1.1.0
🔄 Histórico de Atualizações:

1.0.0: Versão inicial

1.1.0: Adicionado diagrama de fluxo e decisões arquiteturais

✉️ Contato: rgurgel.romulo@gmail.com

Documentação técnica complementar disponível em /documentation/api_postman.json e via Swagger UI.