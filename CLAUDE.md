# CLAUDE.md — Sus Receita · Nova Receita Médica Digital

## Build

Este projeto usa **Gradle** (não Maven). Use sempre `./gradlew` para build, testes e dependências.

```bash
./gradlew build          # compila todos os módulos
./gradlew test           # roda todos os testes
./gradlew bootRun        # sobe um serviço individualmente
./gradlew :auth-service:bootRun
```

## Estrutura

Monorepo com múltiplos subprojetos Gradle:

```
pos-tech-final/
├── settings.gradle          # declara todos os subprojetos
├── build.gradle             # configuração compartilhada (plugins, deps comuns)
├── gradle/libs.versions.toml  # version catalog (fonte única de versões)
├── api-gateway/
└── auth-service/
```

## Stack

- Java 21
- Spring Boot 3.3.x
- Spring Cloud 2023.0.x
- Kafka (mensageria entre serviços)
- PostgreSQL 16 (schema `auth`)
- Docker Compose para ambiente local

## Regras

- Sem Eureka. O API Gateway usa rotas estáticas apontando para hostnames Docker.
- Cada serviço acessa apenas seu próprio schema PostgreSQL.
- Variáveis de ambiente sensíveis (chaves JWT, Azure) nunca são commitadas. Use `.env` local (já no `.gitignore`).
- Versões de dependências ficam exclusivamente em `gradle/libs.versions.toml`. Não declarar versão direto nos `build.gradle` dos subprojetos.
