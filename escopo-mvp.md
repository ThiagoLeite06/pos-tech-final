# Escopo MVP — Nova Receita Médica Digital (SUS)

**Versão:** 1.0
**Data:** 29/03/2026
**Referência:** ESCOPO.md (visão completa do produto)

> Este documento descreve a versão mínima viável do projeto. O ESCOPO.md representa a visão de produto completa; este arquivo define o subconjunto a ser implementado e apresentado.

---

## O que foi cortado e por quê

| Item removido | Motivo |
|---|---|
| **Eureka Server** | Docker Compose tem DNS interno nativo; rotas estáticas no Gateway resolvem sem overhead adicional |
| **Prescription Query Service** | Leituras de status via PostgreSQL atendem o MVP sem necessidade de serviço separado |
| **MongoDB (Read DB)** | Otimização prematura; PostgreSQL suporta as queries necessárias |
| **Medicine DB como banco separado** | Vira schema no mesmo PostgreSQL |
| **Stack de observabilidade** (Prometheus, Grafana, Jaeger, Loki) | Logs estruturados no console suficientes para apresentação |
| **Geração de PDF** | Resposta JSON com dados completos da receita demonstra o fluxo; PDF é melhoria futura |
| **Job de expiração de revisão manual** (5 dias úteis) | Endpoint de revisão existe, mas sem scheduler automático |
| **Portal web para revisores** | Fila de revisão manual acessível via API (ex: Postman/Swagger) |

---

## Arquitetura MVP

```
[App Mobile / Postman / Swagger UI]
              |
              v
      [API Gateway]
      Spring Cloud Gateway
      JWT validation, rate limiting, static routing
         |          |
         |          └──> [Auth Service]
         |                Spring Boot + PostgreSQL (schema: auth)
         |
         ├──> [Prescription Service]
         |    Spring Boot + PostgreSQL (schemas: prescription, medicine)
         |         |
         |         | produz/consome eventos via Kafka
         |         |
         └──> [Evidence Service]
              Spring Boot
                   |
                   ├──> Azure Blob Storage (armazena arquivo original)
                   └──> Azure AI Document Intelligence (OCR)

[Apache Kafka]
Tópicos: prescription.requested | evidence.validated | prescription.approved | prescription.rejected | prescription.pending-review
```

**Total: 4 serviços + Kafka + PostgreSQL (1 instância) + Azure Blob**

---

## Serviços

### API Gateway (Spring Cloud Gateway)

- Validação e decodificação de JWT (RS256) em todas as rotas protegidas
- Rate limiting por token: 60 req/min por usuário
- Roteamento estático via `application.yml` para os demais serviços (sem Eureka)
- Rejeição de requisições sem token (HTTP 401) ou token expirado

### Auth Service

- `POST /api/v1/auth/login` — autenticação com CPF + senha; retorna access token (1h) + refresh token (7d)
- `POST /api/v1/auth/logout` — invalida o token
- `POST /api/v1/auth/refresh` — renova access token via refresh token
- JWT assimétrico RS256; claims: `sub`, `cpf`, `role`, `exp`, `iat`
- Perfis: `PATIENT`, `REVIEWER`, `ADMIN`

### Prescription Service

Responsabilidades:

- Receber e validar a solicitação de nova receita
- Aplicar regras de elegibilidade e regras clínicas
- Publicar e consumir eventos Kafka
- Expor endpoints de leitura de status (sem serviço separado)

Endpoints:

| Método | Caminho | Descrição | Perfil |
|---|---|---|---|
| `POST` | `/api/v1/receitas/solicitacoes` | Cria nova solicitação (multipart: arquivo + dados) | `PATIENT` |
| `GET` | `/api/v1/receitas/{id}` | Consulta status e histórico de uma solicitação | `PATIENT`, `REVIEWER` |
| `GET` | `/api/v1/receitas` | Lista solicitações do paciente autenticado (paginado) | `PATIENT` |
| `PATCH` | `/api/v1/receitas/solicitacoes/{id}/revisar` | Revisor aprova ou rejeita caso em revisão manual | `REVIEWER` |
| `GET` | `/api/v1/admin/receitas/revisao` | Lista solicitações em fila de revisão manual | `REVIEWER`, `ADMIN` |
| `GET` | `/api/v1/admin/receitas/bloqueios` | Lista medicamentos bloqueados | `ADMIN` |
| `POST` | `/api/v1/admin/receitas/bloqueios` | Adiciona medicamento à lista de bloqueio | `ADMIN` |
| `DELETE` | `/api/v1/admin/receitas/bloqueios/{codigo}` | Remove medicamento da lista de bloqueio | `ADMIN` |

### Evidence Service

Responsabilidades:

- Receber o arquivo via evento (URL do Blob já salva pelo Prescription Service no upload)
- Armazenar arquivo no Azure Blob Storage
- Executar OCR via Azure AI Document Intelligence
- Classificar resultado: `VALID`, `INVALID`, `PARTIAL`
- Publicar evento `evidence.validated` com resultado e dados extraídos
- Fallback: se OCR indisponível (timeout/5xx) → publica `PARTIAL` → revisão manual

---

## Banco de Dados

Uma única instância PostgreSQL com schemas separados por domínio:

```
PostgreSQL (única instância)
├── schema: auth
│   ├── users
│   └── refresh_tokens
├── schema: prescription
│   ├── solicitacao_receita
│   └── historico_prescricao_paciente
└── schema: medicine
    ├── medicamento
    └── lista_bloqueio  (coluna bloqueado + motivo_bloqueio na tabela medicamento)
```

Cada serviço acessa **apenas seu próprio schema** via datasource dedicado — simula isolamento de banco sem a complexidade operacional de múltiplos containers de banco.

---

## Mensageria Kafka

| Tópico | Produtor | Consumidor(es) |
|---|---|---|
| `prescription.requested` | Prescription Service | Evidence Service |
| `evidence.validated` | Evidence Service | Prescription Service |
| `prescription.approved` | Prescription Service | *(log / futuro Query Service)* |
| `prescription.rejected` | Prescription Service / Evidence Service | *(log / futuro Query Service)* |
| `prescription.pending-review` | Evidence Service | Prescription Service |

Configuração mínima: 1 partição por tópico, fator de replicação 1 (desenvolvimento). Dead Letter Topics (`*.dlq`) com retentativa 3x + backoff exponencial.

---

## Regras de Negócio (MVP)

Todas as 27 regras do ESCOPO.md são mantidas, exceto:

| Regra | Status no MVP |
|---|---|
| RN-027 (expiração automática de revisão manual após 5 dias úteis) | **Removida** — sem job agendado; revisão fica em aberto até ação do revisor |

As demais regras RN-001 a RN-026 estão em vigor.

---

## Fluxos de Erro e Exceção

Todos os 15 cenários da tabela do ESCOPO.md são mantidos, incluindo os fluxos de:

- Arquivo inválido / ilegível / parcial
- CPF divergente (fraude)
- Medicamento bloqueado / não contínuo / sem histórico
- Janela de tempo (muito cedo / expirado)
- Limite de renovações consecutivas
- Anti-spam (HTTP 429)
- JWT ausente / expirado / sem permissão

Padrão de resposta de erro: RFC 7807 (Problem Details) em todos os endpoints.

---

## Fluxo do Caminho Feliz (Happy Path)

```
1. Paciente autentica → POST /api/v1/auth/login → recebe JWT

2. Paciente envia solicitação com arquivo anexo
   → POST /api/v1/receitas/solicitacoes (multipart, JWT no header)

3. API Gateway valida JWT → roteia para Prescription Service

4. Prescription Service:
   → Valida anti-spam (RN-022), elegibilidade (RN-006/009/010/011), identidade (RN-017)
   → Armazena arquivo no Azure Blob
   → Persiste solicitação com status PENDING_EVIDENCE
   → Publica evento prescription.requested

5. Evidence Service consome prescription.requested:
   → Executa OCR sobre o arquivo
   → Valida campos extraídos (data + medicamento + nome do paciente)
   → Publica evidence.validated com resultado VALID

6. Prescription Service consome evidence.validated:
   → Executa regras clínicas (RN-009 a RN-016)
   → Persiste status APPROVED com dados da nova receita
   → Publica prescription.approved

7. Paciente consulta status:
   → GET /api/v1/receitas/{id}
   → Retorna status APPROVED + dados completos da nova receita (JSON)
```

---

## Fluxo de Exceção — Arquivo Inválido

```
1-3. (mesmo que happy path)

4. Prescription Service publica prescription.requested

5. Evidence Service:
   → OCR não consegue extrair campos mínimos (arquivo rasurado/ilegível)
   → Publica evidence.validated com resultado INVALID

6. Prescription Service consome evidence.validated:
   → Persiste status REJECTED_UNREADABLE
   → Publica prescription.rejected

7. Paciente consulta status:
   → GET /api/v1/receitas/{id}
   → Retorna status REJECTED_UNREADABLE + mensagem orientando reenvio de arquivo mais nítido
```

---

## Infraestrutura Local (Docker Compose)

Serviços no `docker-compose.yml`:

```yaml
services:
  postgres:       # PostgreSQL 16 — porta 5432
  kafka:          # Apache Kafka 3.6
  zookeeper:      # Zookeeper (dependência do Kafka)
  api-gateway:    # Spring Cloud Gateway — porta 8080
  auth-service:   # porta 8081
  prescription-service:  # porta 8082
  evidence-service:      # porta 8083
```

Sem Eureka. O Gateway usa os nomes dos serviços Docker como hostnames estáticos nas rotas (`uri: http://prescription-service:8082`).

---

## Stack Tecnológica

| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 21 (LTS) | Linguagem de todos os serviços |
| Spring Boot | 3.3.x | Framework base |
| Spring Cloud Gateway | 4.x | API Gateway (sem Eureka) |
| Spring Security | 6.x | JWT RS256 |
| Spring Data JPA | 3.x | ORM para PostgreSQL |
| Spring Kafka | 3.x | Integração com Kafka |
| Resilience4j | 2.x | Circuit Breaker nas chamadas ao OCR e Azure Blob |
| PostgreSQL | 16.x | Banco único, múltiplos schemas |
| Apache Kafka | 3.6.x | Mensageria assíncrona |
| Azure Blob Storage | — | Armazenamento de arquivos |
| Azure AI Document Intelligence | v3.1 | OCR (modelo prebuilt-document) |
| Docker / Docker Compose | 25.x / 2.x | Containerização e ambiente local |
| JUnit 5 + Testcontainers | — | Testes unitários e de integração |
| WireMock | — | Mock do OCR e Azure Blob nos testes |
| OpenAPI / Springdoc | 2.x | Documentação automática (Swagger UI) |

---

## Ordem de Implementação

| Fase | O que entregar |
|---|---|
| **1. Base** | `docker-compose.yml` com PostgreSQL, Kafka e Zookeeper funcionando |
| **2. Auth** | Auth Service completo com login, refresh e logout |
| **3. Gateway** | API Gateway com rotas estáticas e validação JWT |
| **4. Evidence** | Evidence Service: upload → Blob → OCR → evento Kafka |
| **5. Prescription** | Prescription Service: validações, regras de negócio, máquina de estados |
| **6. Integração** | Testes de integração cobrindo happy path + principais fluxos de erro |

---

## Caminho para a Versão Completa

Os itens cortados neste MVP são adições incrementais sem quebra de contrato:

- **Eureka** → plugável via dependência `spring-cloud-starter-netflix-eureka-client`; Gateway já suporta `lb://` prefix
- **Prescription Query Service + MongoDB** → novo serviço que consome os mesmos eventos Kafka já existentes; zero impacto nos outros serviços
- **PDF** → novo endpoint `GET /api/v1/receitas/{id}/documento` no Prescription Service
- **Job de expiração de revisão manual** → `@Scheduled` no Prescription Service
- **Stack de observabilidade** → adicionar dependências Micrometer/OTEL sem mudança de lógica
