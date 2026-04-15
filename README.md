# Nova Receita Médica Digital — SUS

> Plataforma que permite pacientes do SUS solicitarem a geração de uma nova receita médica para medicamentos de uso contínuo, com validação por OCR e regras clínicas automatizadas — sem precisar comparecer presencialmente a uma UBS.

Projeto de pós-graduação (TCC) · FIAP · 2026

---

## O Problema

Pacientes com doenças crônicas (hipertensão, diabetes, hipotireoidismo) precisam ir pessoalmente a uma unidade de saúde só para renovar receitas de medicamentos estáveis que já fazem parte de seu histórico. Isso gera:

- Sobrecarga nas UBS com casos de baixa complexidade
- Risco de interrupção do tratamento por dificuldade de acesso
- Deslocamento desnecessário para pacientes vulneráveis

## A Solução

O paciente envia uma foto ou PDF da receita anterior pelo aplicativo. O sistema valida automaticamente via OCR, aplica regras clínicas e, se tudo estiver correto, gera a nova receita digitalmente — sem nenhuma interação humana. Casos ambíguos vão para uma fila de revisão médica.

---

## Arquitetura

```
[App Mobile / Postman / Swagger UI]
              |
              v
        [API Gateway :8080]
        Spring Cloud Gateway
        JWT RS256 · Rate Limiting · Roteamento estático
           |              |
           |              └──> [Auth Service :8081]
           |                   Spring Boot · PostgreSQL (schema: auth)
           |
           ├──> [Prescription Service :8082]
           |    Spring Boot · PostgreSQL (schemas: prescription, medicine)
           |         |
           |         | Kafka (eventos assíncronos)
           |         |
           └──> [Evidence Service :8083]
                Spring Boot
                     |
                     ├──> Azure Blob Storage
                     └──> Azure AI Document Intelligence (OCR)

[Apache Kafka]
  prescription.requested  |  evidence.validated
  prescription.approved   |  prescription.rejected  |  prescription.pending-review
```

**Sem Eureka.** O Gateway usa os nomes de serviço do Docker como hostnames estáticos. Service discovery é resolvido pelo DNS interno do Docker Compose.

---

## Serviços

| Serviço | Porta | Responsabilidade |
|---|---|---|
| `api-gateway` | 8080 | Roteamento, validação JWT, rate limiting |
| `auth-service` | 8081 | Login/logout/refresh, JWT RS256, perfis de usuário |
| `prescription-service` | 8082 | Regras de negócio, máquina de estados da receita |
| `evidence-service` | 8083 | Upload, OCR via Azure, publicação de resultado |

### Perfis de usuário

| Perfil | Acesso |
|---|---|
| `PATIENT` | Solicitar receita, consultar status |
| `REVIEWER` | Revisar casos em fila manual, listar solicitações pendentes |
| `ADMIN` | Gerenciar lista de medicamentos bloqueados |

---

## Stack Tecnológica

| Tecnologia | Versão |
|---|---|
| Java | 21 (LTS) |
| Spring Boot | 3.3.4 |
| Spring Cloud Gateway | 2023.0.3 |
| Spring Security (JWT RS256) | 6.x |
| Spring Data JPA + Flyway | 3.x |
| Spring Kafka | 3.x |
| PostgreSQL | 16 |
| Apache Kafka | 3.6 (Confluent 7.6.1) |
| Azure Blob Storage | 12.27.1 |
| Azure AI Document Intelligence | 4.1.9 |
| JUnit 5 + Testcontainers | 1.20.1 |
| Docker / Docker Compose | 25.x / 2.x |

---

## Pré-requisitos

- **Java 21** — [Instalar via SDKMAN](https://sdkman.io/): `sdk install java 21-tem`
- **Docker** e **Docker Compose** (v2)
- **Git**
- Um par de chaves RSA para o JWT (instruções abaixo)

---

## Configuração inicial

### 1. Clone o repositório

```bash
git clone <url-do-repositorio>
cd pos-tech-final
```

### 2. Gere o par de chaves RSA para o JWT

O Auth Service assina tokens com a chave privada; o API Gateway valida com a pública.

**Opção recomendada — script automático:**

```bash
./docker/generate-keys.sh
```

O script gera o par de chaves RSA 2048-bit no formato correto (PKCS#8 DER para privada, X.509 DER para pública), converte para Base64 e escreve automaticamente no arquivo `.env` na raiz do projeto. Cada desenvolvedor precisa rodar isso uma única vez.

<details>
<summary>Gerar manualmente (alternativa)</summary>

```bash
# Gera chave privada RSA 2048 bits
openssl genrsa -out jwt_private.pem 2048

# Extrai a chave pública correspondente
openssl rsa -in jwt_private.pem -pubout -out jwt_public.pem

# Converte para Base64 em uma única linha (formato esperado pelas variáveis de ambiente)
export JWT_PRIVATE_KEY=$(cat jwt_private.pem | base64 | tr -d '\n')
export JWT_PUBLIC_KEY=$(cat jwt_public.pem | base64 | tr -d '\n')
```

</details>

### 3. Verifique o arquivo `.env`

O script cria/atualiza o `.env` automaticamente. Se precisar adicionar variáveis extras, edite-o diretamente. Este arquivo está no `.gitignore` e **nunca deve ser commitado**.

```bash
# .env (gerado pelo script — não commitar)
JWT_PRIVATE_KEY=<gerado automaticamente>
JWT_PUBLIC_KEY=<gerado automaticamente>

# Opcional — sobrescreve os defaults do docker-compose
DB_PASSWORD=susreceita123
```

---

## Como executar

### Subir toda a infraestrutura (modo Docker)

```bash
docker compose up --build
```

Aguarde os health checks. A ordem de inicialização é gerenciada automaticamente:
1. PostgreSQL e Kafka sobem primeiro
2. Auth Service aguarda o PostgreSQL estar saudável
3. API Gateway aguarda o Auth Service estar saudável

Serviços disponíveis após a inicialização:

| Endpoint | Descrição |
|---|---|
| `http://localhost:8080` | API Gateway (ponto de entrada) |
| `http://localhost:8081/swagger-ui.html` | Swagger UI — Auth Service |
| `http://localhost:8081/actuator/health` | Health check — Auth Service |
| `http://localhost:5432` | PostgreSQL (usuário: `susreceita`) |
| `http://localhost:9092` | Kafka broker |

### Subir apenas a infraestrutura (banco + Kafka)

Útil para desenvolver um serviço localmente enquanto mantém a infra rodando em Docker.

```bash
docker compose up postgres kafka zookeeper
```

### Executar um serviço individualmente (desenvolvimento local)

```bash
# Auth Service
./gradlew :auth-service:bootRun

# API Gateway
./gradlew :api-gateway:bootRun
```

---

## Testando a Autenticação

O banco de dados é inicializado com três usuários de teste via Flyway (`V3__seed_users.sql`). Todos usam a senha `senha123`.

| CPF | Nome | Perfil |
|---|---|---|
| `12345678901` | Paciente Teste | `PATIENT` |
| `98765432100` | Revisor Teste | `REVIEWER` |
| `11122233344` | Admin Teste | `ADMIN` |

Com a stack rodando (`docker compose up --build`), execute o fluxo completo:

### 1. Login

```bash
curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"cpf":"12345678901","password":"senha123"}' | jq
```

Resposta esperada:

```json
{
  "accessToken": "eyJhbGciOiJSUzI1NiJ9...",
  "refreshToken": "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
  "expiresIn": 3600,
  "role": "PATIENT"
}
```

### 2. Usar o token em uma rota protegida

```bash
ACCESS_TOKEN="<accessToken retornado acima>"

curl -s http://localhost:8080/api/v1/receitas \
  -H "Authorization: Bearer $ACCESS_TOKEN" | jq
```

> Até o Prescription Service ser implementado, retorna 503. O importante é que o Gateway valide o token e roteia a requisição (não retorna 401).

### 3. Renovar o access token

O access token expira em 1 hora. Use o refresh token (válido por 7 dias) para renová-lo sem precisar fazer login novamente:

```bash
REFRESH_TOKEN="<refreshToken retornado no login>"

curl -s -X POST http://localhost:8080/api/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d "{\"refreshToken\":\"$REFRESH_TOKEN\"}" | jq
```

### 4. Logout (revoga o refresh token)

```bash
curl -s -X POST http://localhost:8080/api/v1/auth/logout \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -d "{\"refreshToken\":\"$REFRESH_TOKEN\"}"
# Resposta: 204 No Content
```

Após o logout, tentar usar o mesmo `refreshToken` retorna `401`.

### Swagger UI

Para explorar os endpoints do Auth Service interativamente:

```
http://localhost:8081/swagger-ui.html
```

---

## Desenvolvimento

### Build completo

```bash
./gradlew build
```

### Rodar todos os testes

```bash
./gradlew test
```

Os testes de integração usam **Testcontainers** — o Docker deve estar rodando localmente para que subam PostgreSQL e Kafka em containers efêmeros.

### Estrutura do monorepo

```
pos-tech-final/
├── settings.gradle              # declara todos os subprojetos
├── build.gradle                 # configuração compartilhada (plugins, BOMs)
├── gradle/
│   └── libs.versions.toml       # fonte única de versões (Version Catalog)
├── docker-compose.yml
├── docker/
│   └── postgres/
│       └── init.sql             # cria os schemas: auth, prescription, medicine
├── api-gateway/
│   ├── build.gradle
│   ├── Dockerfile
│   └── src/
├── auth-service/
│   ├── build.gradle
│   ├── Dockerfile
│   └── src/
├── prescription-service/        # (em desenvolvimento)
└── evidence-service/            # (em desenvolvimento)
```

### Convenções

- **Versões de dependências** ficam exclusivamente em `gradle/libs.versions.toml`. Nunca declarar versão direto no `build.gradle` de um subprojeto.
- **Cada serviço acessa apenas seu próprio schema** PostgreSQL — simula o isolamento de múltiplos bancos sem a complexidade operacional.
- **Variáveis sensíveis** (chaves JWT, credenciais Azure) somente via variáveis de ambiente ou `.env`. Nunca hardcoded.

---

## Endpoints da API

Todos os endpoints passam pelo API Gateway em `http://localhost:8080`. O header `Authorization: Bearer <token>` é obrigatório nas rotas protegidas.

### Auth

| Método | Rota | Descrição | Auth |
|---|---|---|---|
| `POST` | `/api/v1/auth/login` | Autenticação com CPF + senha; retorna access token (1h) + refresh token (7d) | Público |
| `POST` | `/api/v1/auth/refresh` | Renova access token via refresh token | Público |
| `POST` | `/api/v1/auth/logout` | Invalida o refresh token | Autenticado |

### Receitas (Prescription Service)

| Método | Rota | Descrição | Perfil |
|---|---|---|---|
| `POST` | `/api/v1/receitas/solicitacoes` | Cria nova solicitação (multipart: arquivo + dados) | `PATIENT` |
| `GET` | `/api/v1/receitas/{id}` | Consulta status e histórico de uma solicitação | `PATIENT`, `REVIEWER` |
| `GET` | `/api/v1/receitas` | Lista solicitações do paciente autenticado (paginado) | `PATIENT` |
| `PATCH` | `/api/v1/receitas/solicitacoes/{id}/revisar` | Revisor aprova ou rejeita caso em revisão manual | `REVIEWER` |
| `GET` | `/api/v1/receitas/receitas/revisao` | Lista solicitações em fila de revisão manual | `REVIEWER`, `ADMIN` |
| `GET` | `/api/v1/admin/receitas/bloqueios` | Lista medicamentos bloqueados | `ADMIN` |
| `POST` | `/api/v1/admin/receitas/bloqueios` | Adiciona medicamento à lista de bloqueio | `ADMIN` |
| `DELETE` | `/api/v1/admin/receitas/bloqueios/{codigo}` | Remove medicamento da lista de bloqueio | `ADMIN` |

---

## Fluxo principal (Happy Path)

```
1. POST /api/v1/auth/login  →  recebe JWT

2. POST /api/v1/receitas/solicitacoes  (multipart: arquivo da receita anterior + dados)
   └─ API Gateway valida JWT e roteia para Prescription Service

3. Prescription Service
   ├─ Valida anti-spam, elegibilidade e identidade do paciente
   ├─ Armazena arquivo no Azure Blob
   ├─ Persiste solicitação com status PENDING_EVIDENCE
   └─ Publica evento  →  prescription.requested

4. Evidence Service consome prescription.requested
   ├─ Executa OCR (Azure AI Document Intelligence)
   ├─ Valida: data + medicamento + nome do paciente
   └─ Publica evento  →  evidence.validated (VALID | INVALID | PARTIAL)

5. Prescription Service consome evidence.validated
   ├─ Aplica regras clínicas (janela de 10 dias, limite de renovações, bloqueios)
   ├─ Persiste status APPROVED
   └─ Publica evento  →  prescription.approved

6. GET /api/v1/receitas/{id}  →  retorna status APPROVED + dados da nova receita
```

---

## Regras de Negócio Principais

| Regra | Descrição |
|---|---|
| RN-006 | Janela de 10 dias antes do vencimento para solicitar nova receita |
| RN-010 | Máximo 2 renovações consecutivas automáticas (configurável via `MAX_CONSECUTIVE_RENEWALS`) |
| RN-011 | Apenas medicamentos de uso contínuo com histórico prévio do paciente |
| RN-012 | Medicamentos controlados (Portaria 344/98) bloqueados por padrão |
| RN-017 | CPF da receita enviada deve corresponder ao CPF do paciente autenticado |
| RN-022 | Anti-spam: máximo 3 tentativas/dia por paciente (HTTP 429 com `Retry-After`) |
| RN-023 | OCR deve extrair no mínimo 3 campos: data, medicamento, nome do paciente |
| RN-024 | Dados parciais (menos de 3 campos) encaminham para revisão manual — não rejeitam |

O escopo completo com todas as 27 regras (RN-001 a RN-027) e 24 critérios de aceitação está em [`ESCOPO.md`](ESCOPO.md). O subconjunto implementado no MVP está em [`escopo-mvp.md`](escopo-mvp.md).

---

## Banco de Dados

Uma única instância PostgreSQL com schemas separados por domínio:

```
PostgreSQL (porta 5432 · banco: susreceita)
├── schema: auth
│   ├── users
│   └── refresh_tokens
├── schema: prescription
│   ├── solicitacao_receita
│   └── historico_prescricao_paciente
└── schema: medicine
    ├── medicamento
    └── lista_bloqueio
```

Migrações gerenciadas pelo **Flyway** em cada serviço, executadas automaticamente na inicialização.

---

## Mensageria Kafka

| Tópico | Produtor | Consumidor |
|---|---|---|
| `prescription.requested` | Prescription Service | Evidence Service |
| `evidence.validated` | Evidence Service | Prescription Service |
| `prescription.approved` | Prescription Service | _(log / futuro)_ |
| `prescription.rejected` | Prescription / Evidence | _(log / futuro)_ |
| `prescription.pending-review` | Evidence Service | Prescription Service |

Dead Letter Topics (`*.dlq`) com retentativa 3x e backoff exponencial para cada tópico principal.

---

## Documentação adicional

- [`ESCOPO.md`](ESCOPO.md) — Visão completa do produto: arquitetura, todos os endpoints, 27 regras de negócio, modelos de dados, critérios de aceitação
- [`escopo-mvp.md`](escopo-mvp.md) — Subconjunto implementado no MVP, o que foi cortado e por quê, ordem de implementação
- [`CLAUDE.md`](CLAUDE.md) — Instruções de build, convenções e regras do projeto para desenvolvimento assistido
