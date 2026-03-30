# Documento de Escopo do Projeto
# Renovação Inteligente de Receitas no SUS
### Sistema de Nova Receita Médica Digital

**Versão:** 1.0
**Data:** 29/03/2026
**Status:** Rascunho para revisão da equipe

---

## Sumário

1. Visão Geral do Projeto
2. Funcionalidades no Escopo
3. Fora do Escopo
4. Resumo da Arquitetura do Sistema
5. Endpoints da API
6. Regras de Negócio
7. Fluxos de Erro e Exceção
8. Modelos de Dados
9. Pontos de Integração
10. Requisitos Não-Funcionais
11. Critérios de Aceitação
12. Stack Tecnológica

---

## 1. Visão Geral do Projeto

### 1.1 Descrição

O sistema **Renovação Inteligente de Receitas no SUS** — internamente denominado **Nova Receita Médica** — é uma plataforma digital que permite a pacientes do Sistema Único de Saúde (SUS) solicitar a geração de uma nova receita médica para medicamentos de uso contínuo, com base em receita anterior já existente no histórico do paciente.

O sistema **não** renova a mesma receita: ele gera uma receita nova com base nos dados clínicos conhecidos, dentro de critérios rígidos de segurança, validando evidências documentais via OCR e aplicando regras clínicas automatizadas antes de qualquer aprovação.

### 1.2 Problema que Resolve

Pacientes com doenças crônicas (hipertensão, diabetes, hipotireoidismo, etc.) precisam comparecer presencialmente a uma UBS ou consulta para obter uma nova receita de medicamentos de uso contínuo, mesmo quando o quadro clínico é estável e o medicamento já faz parte do histórico do paciente. Isso gera:

- Sobrecarga desnecessária nas unidades de saúde
- Deslocamento e absenteísmo para pacientes vulneráveis
- Risco de interrupção do tratamento por dificuldade de acesso
- Consumo de tempo de profissionais de saúde para casos de baixa complexidade

### 1.3 Usuários do Sistema

| Perfil | Descrição | Canal de Acesso |
|---|---|---|
| Paciente SUS | Cidadão com cadastro ativo no SUS, com histórico de medicamento de uso contínuo | Aplicativo móvel (frontend externo ao escopo) |
| Médico/Revisor | Profissional de saúde responsável por revisar casos em fila de análise manual | Portal web administrativo (frontend externo ao escopo) |
| Administrador do Sistema | Equipe técnica com acesso a configurações, logs e regras de bloqueio | API administrativa direta |

### 1.4 Premissas

- O paciente já possui cadastro ativo no sistema do SUS com CPF validado.
- O frontend (aplicativo móvel) é responsabilidade de outra equipe e consome as APIs documentadas neste escopo.
- As receitas geradas passam por validação automática e, em caso de aprovação, são emitidas em formato PDF digital assinado.
- Medicamentos controlados (portaria 344/1998 e similares) estão bloqueados por padrão e nunca serão contemplados pelo sistema.

---

## 2. Funcionalidades no Escopo

### 2.1 API Gateway (Spring Cloud Gateway)

- Roteamento autenticado de todas as requisições
- Validação e decodificação de token JWT em todas as rotas protegidas
- Rate limiting por usuário/IP (camada de anti-spam)
- Redirecionamento para serviços via Service Discovery (Eureka)
- Registro de logs de acesso centralizados

### 2.2 Service Discovery (Eureka Server)

- Registro automático de todas as instâncias de serviço
- Health checks periódicos
- Balanceamento de carga entre instâncias do mesmo serviço

### 2.3 Serviço de Prescrição — Escrita (Prescription Service / CQRS Write Side)

- Recebimento da solicitação de nova receita médica (endpoint `POST`)
- Validação inicial da solicitação (autenticação, anti-spam, consistência do paciente)
- Verificação de elegibilidade: histórico do paciente, medicamento de uso contínuo, janela de 10 dias antes da expiração
- Contagem de renovações consecutivas — bloquear após limite configurável (padrão: 2)
- Consulta ao Medicine DB para validação de medicamento e verificação de lista de bloqueio
- Publicação do evento `PrescriptionRequested` no Kafka
- Consumo do evento `EvidenceValidated` para executar regras clínicas finais
- Publicação de `PrescriptionApproved`, `PrescriptionRejected` ou `PrescriptionPendingReview`
- Geração do documento de nova receita em formato PDF (integração com biblioteca de geração)
- Armazenamento do estado transacional no PostgreSQL (Write DB)

### 2.4 Serviço de Consulta de Prescrição (Prescription Query / CQRS Read Side)

- Consumo dos eventos `PrescriptionRequested`, `EvidenceValidated`, `PrescriptionApproved`, `PrescriptionRejected`, `PrescriptionPendingReview` para atualização da visão materializada no MongoDB
- Endpoint de consulta de status da solicitação por ID
- Endpoint de consulta do histórico de solicitações do paciente
- Endpoint de download da receita aprovada (PDF)

### 2.5 Serviço de Evidência (Evidence Service)

- Recebimento de upload de arquivo de receita anterior (imagem JPG/PNG ou PDF)
- Armazenamento do arquivo no Azure Blob Storage
- Execução de OCR sobre o arquivo via integração com serviço externo
- Validação do resultado do OCR: presença de data válida, nome de medicamento, dados mínimos identificáveis
- Classificação do resultado: Válido / Inválido / Parcialmente Válido (para revisão manual)
- Publicação do evento `EvidenceValidated` (com resultado: aprovado, rejeitado ou revisão manual) no Kafka
- Consumo do evento `PrescriptionRequested` para iniciar o processo de validação de evidência

### 2.6 Autenticação e Autorização (transversal)

- Geração de token JWT no login (endpoint dedicado no API Gateway ou serviço de auth)
- Validação de token em todas as rotas protegidas
- Controle de perfis: `PATIENT`, `REVIEWER`, `ADMIN`
- Invalidação de token (logout)

---

## 3. Fora do Escopo

Os itens a seguir **não serão construídos** nesta fase do projeto:

| Item | Justificativa |
|---|---|
| Frontend / Aplicativo Móvel | Responsabilidade de equipe separada; consome as APIs documentadas aqui |
| Portal Web para Médicos/Revisores | Segunda fase; a fila de revisão manual existe via API, a interface visual fica para depois |
| Integração com sistemas legados do SUS (RNDS, HÓRUS) | Escopo futuro; dependência de acordos institucionais |
| Emissão de receitas para medicamentos controlados (portaria 344) | Bloqueado por regra de negócio; requer fluxo regulatório distinto |
| Criação de receita para medicamentos novos (primeira prescrição) | O sistema só gera receita para medicamentos já no histórico do paciente |
| Telemedicina ou consulta virtual integrada | Fora do escopo técnico e regulatório desta fase |
| Notificações push / SMS / email | Primeira fase usa polling via GET; notificações são escopo futuro |
| Módulo de pagamento ou cobrança | Sistema público, sem cobrança ao paciente |
| Integração com farmácias para dispensação direta | Escopo futuro |
| Módulo de agendamento de consulta | Redirecionamento para UBS é responsabilidade do sistema externo |

---

## 4. Resumo da Arquitetura do Sistema

### 4.1 Visão dos Serviços

```
[App Mobile]
     |
     v
[API Gateway - Spring Cloud Gateway]
     |  (JWT validation, rate limiting, routing)
     |
     |-----> [Prescription Service]  <---> [PostgreSQL - Write DB]
     |            |  (publica/consome eventos Kafka)
     |            |
     |-----> [Evidence Service]      <---> [Azure Blob Storage]
     |            |  (OCR, validação de arquivo)
     |
     |-----> [Prescription Query]    <---> [MongoDB - Read DB]
     |
     |-----> [Auth Service]          <---> [PostgreSQL - Auth DB]
     |
     v
[Eureka Server - Service Discovery]
```

### 4.2 Infraestrutura de Mensageria

- **Broker:** Apache Kafka
- **Tópicos:**

| Tópico Kafka | Produtor | Consumidor(es) | Descrição |
|---|---|---|---|
| `prescription.requested` | Prescription Service | Evidence Service, Prescription Query | Nova solicitação de receita registrada |
| `evidence.validated` | Evidence Service | Prescription Service, Prescription Query | Resultado da validação de evidência |
| `prescription.approved` | Prescription Service | Prescription Query | Receita aprovada e gerada |
| `prescription.rejected` | Prescription Service / Evidence Service | Prescription Query | Solicitação rejeitada com motivo |
| `prescription.pending-review` | Prescription Service / Evidence Service | Prescription Query | Caso enviado para revisão manual |

### 4.3 Bancos de Dados

| Banco | Tipo | Serviço Responsável | Finalidade |
|---|---|---|---|
| PostgreSQL (Write DB) | Relacional | Prescription Service | Dados transacionais: solicitações, pacientes, medicamentos, contadores |
| PostgreSQL (Medicine DB) | Relacional | Prescription Service | Catálogo de medicamentos, lista de bloqueio, regras de uso contínuo |
| PostgreSQL (Auth DB) | Relacional | Auth Service | Usuários, credenciais, tokens, sessões |
| MongoDB (Read DB) | Documental | Prescription Query | Visão materializada de status e histórico de solicitações |
| Azure Blob Storage | Objeto | Evidence Service | Armazenamento de arquivos de receita (imagens, PDFs) |

### 4.4 Fluxo do Caminho Feliz (Happy Path)

```
1. Paciente autentica → obtém JWT
2. Paciente faz upload do arquivo de receita anterior + dados da solicitação
   → POST /api/v1/receitas/solicitacoes (com JWT)
3. API Gateway valida JWT → roteia para Prescription Service
4. Prescription Service valida anti-spam, elegibilidade e histórico
   → Persiste solicitação com status PENDING_EVIDENCE
   → Publica evento `prescription.requested`
5. Evidence Service consome `prescription.requested`
   → Armazena arquivo no Azure Blob Storage
   → Executa OCR sobre o arquivo
   → Valida dados extraídos pelo OCR
   → Publica `evidence.validated` (resultado: VALID)
6. Prescription Service consome `evidence.validated`
   → Executa regras clínicas (medicamento contínuo, bloqueio, limite de renovações)
   → Gera PDF da nova receita
   → Persiste com status APPROVED
   → Publica `prescription.approved`
7. Prescription Query consome todos os eventos
   → Atualiza visão materializada no MongoDB
8. Paciente consulta status → GET /api/v1/receitas/{id}
   → Prescription Query retorna status APPROVED + URL do PDF
```

---

## 5. Endpoints da API

### 5.1 Convenções

- Todas as rotas são prefixadas por `/api/v1`
- Todas as rotas (exceto `/auth/*`) exigem header `Authorization: Bearer <JWT>`
- Respostas de erro seguem o padrão RFC 7807 (Problem Details)
- Paginação via query params `page` e `size` onde aplicável

### 5.2 Auth Service

| Método | Caminho | Descrição | Auth Obrigatório |
|---|---|---|---|
| `POST` | `/api/v1/auth/login` | Autenticação com CPF + senha; retorna JWT | Não |
| `POST` | `/api/v1/auth/logout` | Invalida o token atual | Sim |
| `POST` | `/api/v1/auth/refresh` | Renova token JWT usando refresh token | Sim (refresh token) |

### 5.3 Prescription Service (Escrita)

| Método | Caminho | Descrição | Perfil Necessário | Auth Obrigatório |
|---|---|---|---|---|
| `POST` | `/api/v1/receitas/solicitacoes` | Cria nova solicitação de receita médica (multipart: arquivo + dados) | `PATIENT` | Sim |
| `PATCH` | `/api/v1/receitas/solicitacoes/{id}/revisar` | Médico registra decisão sobre caso em revisão manual | `REVIEWER` | Sim |
| `GET` | `/api/v1/admin/receitas/bloqueios` | Lista medicamentos bloqueados | `ADMIN` | Sim |
| `POST` | `/api/v1/admin/receitas/bloqueios` | Adiciona medicamento à lista de bloqueio | `ADMIN` | Sim |
| `DELETE` | `/api/v1/admin/receitas/bloqueios/{codigoMedicamento}` | Remove medicamento da lista de bloqueio | `ADMIN` | Sim |

### 5.4 Prescription Query (Leitura)

| Método | Caminho | Descrição | Perfil Necessário | Auth Obrigatório |
|---|---|---|---|---|
| `GET` | `/api/v1/receitas/{id}` | Consulta status e detalhes de uma solicitação específica | `PATIENT`, `REVIEWER` | Sim |
| `GET` | `/api/v1/receitas` | Lista histórico de solicitações do paciente autenticado (paginado) | `PATIENT` | Sim |
| `GET` | `/api/v1/receitas/{id}/documento` | Download do PDF da receita aprovada | `PATIENT` | Sim |
| `GET` | `/api/v1/admin/receitas/revisao` | Lista solicitações aguardando revisão manual (paginado) | `REVIEWER`, `ADMIN` | Sim |
| `GET` | `/api/v1/admin/receitas` | Lista todas as solicitações com filtros (admin) | `ADMIN` | Sim |

### 5.5 Evidence Service

| Método | Caminho | Descrição | Perfil Necessário | Auth Obrigatório |
|---|---|---|---|---|
| `GET` | `/api/v1/evidencias/{solicitacaoId}` | Consulta resultado da validação de evidência (uso interno/debug) | `ADMIN` | Sim |

---

## 6. Regras de Negócio

### 6.1 Validação de Arquivo (RN-001 a RN-005)

| Código | Regra |
|---|---|
| **RN-001** | O arquivo enviado deve ser JPG, PNG ou PDF com tamanho máximo de 10 MB. Arquivos corrompidos, vazios ou em formato não suportado são rejeitados imediatamente, antes de qualquer processamento. |
| **RN-002** | O OCR deve extrair, no mínimo, uma data legível e o nome de um medicamento. Se não conseguir extrair ambos, a solicitação é rejeitada. |
| **RN-003** | Se o OCR conseguir extrair dados parciais (somente um dos dois campos mínimos), a solicitação é encaminhada para fila de revisão manual em vez de ser rejeitada automaticamente. |
| **RN-004** | Imagens ilegíveis, com rasuras que cobrem dados essenciais (data ou nome do medicamento) resultam em rejeição com mensagem orientando o paciente a enviar foto mais nítida ou sem rasuras. |
| **RN-005** | O arquivo original é armazenado no Azure Blob Storage independentemente do resultado da validação, para fins de auditoria. |

### 6.2 Validação de Tempo (RN-006 a RN-008)

| Código | Regra |
|---|---|
| **RN-006** | Uma nova receita só pode ser solicitada se a receita anterior estiver dentro da janela de 10 dias antes de sua data de validade. A data de validade é consultada no histórico do paciente no banco de dados (Write DB), não extraída via OCR. |
| **RN-007** | Se a receita anterior ainda tiver mais de 10 dias de validade, a solicitação é rejeitada com data a partir da qual o pedido poderá ser realizado. |
| **RN-008** | Se a receita anterior já estiver expirada há mais de 30 dias, a solicitação é rejeitada e o paciente é orientado a agendar consulta na UBS. |

### 6.3 Regras de Prescrição — Elegibilidade (RN-009 a RN-013)

| Código | Regra |
|---|---|
| **RN-009** | Somente medicamentos classificados como "uso contínuo" no Medicine DB são elegíveis para o processo de nova receita automática. |
| **RN-010** | O paciente deve ter ao menos 1 (uma) receita válida anterior para o mesmo medicamento registrada em seu histórico no sistema. Primeiro uso de um medicamento sempre requer consulta médica presencial. |
| **RN-011** | Medicamentos presentes na lista de bloqueio do sistema (ex.: controlados conforme Portaria 344/98 ou qualquer medicamento marcado pelo administrador) são automaticamente rejeitados, sem exceção. |
| **RN-012** | O medicamento solicitado deve coincidir com o medicamento identificado pelo OCR na receita anterior enviada como evidência. Divergências resultam em rejeição ou encaminhamento para revisão manual. |
| **RN-013** | Não é permitida a inclusão de medicamentos novos (não presentes no histórico do paciente) via este sistema. |

### 6.4 Limite de Renovações Consecutivas (RN-014 a RN-016)

| Código | Regra |
|---|---|
| **RN-014** | O sistema permite no máximo 2 (dois) ciclos consecutivos de nova receita automática para o mesmo medicamento. O contador `consecutive_renewal_count` é armazenado no registro do paciente-medicamento no Write DB. |
| **RN-015** | Após atingir o limite de 2 renovações consecutivas, a solicitação é automaticamente rejeitada com orientação para o paciente agendar consulta médica. O contador só é zerado quando uma nova receita emitida por médico (fora do sistema) é registrada no histórico. |
| **RN-016** | O limite de 2 renovações consecutivas é configurável via variável de ambiente (`MAX_CONSECUTIVE_RENEWALS`), permitindo ajuste futuro sem redeploy de código. |

### 6.5 Consistência e Identidade do Paciente (RN-017 a RN-019)

| Código | Regra |
|---|---|
| **RN-017** | O CPF do paciente autenticado (extraído do JWT) deve corresponder ao CPF identificado na receita enviada como evidência. Discrepância resulta em rejeição imediata por suspeita de fraude. |
| **RN-018** | O medicamento solicitado deve constar no histórico de prescrições ativas do paciente autenticado. Tentativas de solicitar medicamentos de outros pacientes resultam em rejeição e registro de evento de segurança. |
| **RN-019** | Todas as tentativas de fraude detectadas (RN-017, RN-018) são registradas em log de auditoria de segurança, com IP de origem, user-agent e identidade do solicitante. |

### 6.6 Validação de Evidência Mínima pelo OCR (RN-020 a RN-021)

| Código | Regra |
|---|---|
| **RN-020** | O OCR deve identificar e extrair: (a) data da receita original, (b) nome ou código do medicamento, e (c) nome do paciente. Ausência de qualquer um desses três campos ativa a avaliação de RN-002 e RN-003. |
| **RN-021** | O resultado do OCR é registrado em banco (campo `ocr_result`) com todos os campos extraídos, nível de confiança por campo e versão do modelo OCR utilizado, para rastreabilidade. |

### 6.7 Anti-Spam e Limitação de Tentativas (RN-022 a RN-024)

| Código | Regra |
|---|---|
| **RN-022** | Cada paciente pode realizar no máximo 3 (três) solicitações por dia, independentemente do resultado. O contador diário é mantido no Write DB com timestamp de reset à meia-noite (horário de Brasília). |
| **RN-023** | Ao atingir o limite de 3 tentativas, o sistema retorna HTTP 429 (Too Many Requests) com header `Retry-After` indicando quando o limite será renovado. |
| **RN-024** | O API Gateway aplica rate limiting global de 60 requisições/minuto por token JWT para todas as rotas protegidas, como segunda camada de proteção. |

### 6.8 Fallback Inteligente — Revisão Manual (RN-025 a RN-027)

| Código | Regra |
|---|---|
| **RN-025** | Solicitações com evidência parcialmente válida (RN-003) ou com inconsistência não-crítica detectada (RN-012, divergência leve) são encaminhadas para fila de revisão manual, não rejeitadas. |
| **RN-026** | Solicitações na fila de revisão manual recebem o status `PENDING_REVIEW` e ficam visíveis para revisores (perfil `REVIEWER`) via endpoint `GET /api/v1/admin/receitas/revisao`. |
| **RN-027** | O prazo máximo para resolução de uma solicitação em revisão manual é de 5 dias úteis. Após esse prazo sem ação do revisor, o sistema rejeita automaticamente a solicitação e notifica o paciente via status. |

---

## 7. Fluxos de Erro e Exceção

### 7.1 Tabela de Cenários de Erro

| Cenário | Serviço Responsável | Ação do Sistema | Status HTTP | Status da Solicitação | Evento Publicado |
|---|---|---|---|---|---|
| Arquivo corrompido, vazio ou formato inválido | Evidence Service | Rejeita imediatamente, solicita reenvio | 422 | `REJECTED_INVALID_FILE` | `prescription.rejected` |
| OCR não extrai dados mínimos (nenhum campo) | Evidence Service | Rejeita, orienta enviar foto mais nítida | 422 | `REJECTED_UNREADABLE` | `prescription.rejected` |
| OCR extrai dados parciais (1 de 3 campos) | Evidence Service | Encaminha para revisão manual | 202 | `PENDING_REVIEW` | `prescription.pending-review` |
| CPF do paciente não corresponde ao da receita | Prescription Service | Rejeita, registra evento de segurança | 422 | `REJECTED_IDENTITY_MISMATCH` | `prescription.rejected` |
| Medicamento na lista de bloqueio | Prescription Service | Rejeita, orienta atendimento presencial | 422 | `REJECTED_BLOCKED_MEDICATION` | `prescription.rejected` |
| Medicamento não é de uso contínuo | Prescription Service | Rejeita | 422 | `REJECTED_NOT_CONTINUOUS` | `prescription.rejected` |
| Sem histórico de prescrição anterior | Prescription Service | Rejeita, orienta consulta inicial | 422 | `REJECTED_NO_HISTORY` | `prescription.rejected` |
| Receita ainda com mais de 10 dias de validade | Prescription Service | Rejeita com data de disponibilidade | 422 | `REJECTED_TOO_EARLY` | `prescription.rejected` |
| Receita expirada há mais de 30 dias | Prescription Service | Rejeita, orienta agendar consulta | 422 | `REJECTED_EXPIRED` | `prescription.rejected` |
| Limite de 2 renovações consecutivas atingido | Prescription Service | Rejeita, orienta agendar consulta | 422 | `REJECTED_RENEWAL_LIMIT` | `prescription.rejected` |
| Limite diário de 3 tentativas atingido | API Gateway / Prescription Service | Retorna 429 com Retry-After | 429 | N/A (não criada) | Nenhum |
| Token JWT ausente ou inválido | API Gateway | Retorna 401 | 401 | N/A | Nenhum |
| Token JWT sem permissão para a rota | API Gateway | Retorna 403 | 403 | N/A | Nenhum |
| Evidence Service indisponível | Prescription Service | Solicitação fica em `PENDING_EVIDENCE` — retry via Kafka | 202 | `PENDING_EVIDENCE` | Nenhum novo |
| Falha na geração do PDF | Prescription Service | Registra falha, alerta admin | 500 | `FAILED_PDF_GENERATION` | `prescription.rejected` |
| Revisão manual sem resposta em 5 dias úteis | Prescription Service (job agendado) | Rejeita automaticamente | N/A (assíncrono) | `REJECTED_REVIEW_TIMEOUT` | `prescription.rejected` |

### 7.2 Padrão de Resposta de Erro (RFC 7807)

```json
{
  "type": "https://sus-receitas.gov.br/errors/medicamento-bloqueado",
  "title": "Medicamento bloqueado para nova receita automática",
  "status": 422,
  "detail": "O medicamento 'Clonazepam 2mg' está na lista de medicamentos que requerem consulta médica presencial.",
  "instance": "/api/v1/receitas/solicitacoes",
  "timestamp": "2026-03-29T20:00:00-03:00",
  "solicitacaoId": null
}
```

### 7.3 Resiliência e Retentativas

- Mensagens Kafka com falha de processamento são reenviadas até 3 vezes com backoff exponencial (1s, 2s, 4s).
- Após 3 falhas, a mensagem é encaminhada para Dead Letter Topic (`*.dlq`).
- Monitoramento de DLQ gera alerta operacional para a equipe de sustentação.

---

## 8. Modelos de Dados

### 8.1 `solicitacao_receita` (Write DB — PostgreSQL)

| Campo | Tipo | Descrição |
|---|---|---|
| `id` | UUID | Identificador único da solicitação |
| `paciente_cpf` | VARCHAR(11) | CPF do paciente (hash SHA-256) |
| `paciente_id` | UUID | Referência ao cadastro do paciente |
| `medicamento_codigo` | VARCHAR(20) | Código do medicamento no Medicine DB |
| `medicamento_nome` | VARCHAR(200) | Nome do medicamento solicitado |
| `status` | ENUM | Status atual da solicitação |
| `status_motivo` | VARCHAR(500) | Motivo do status (especialmente rejeições) |
| `evidence_blob_url` | VARCHAR(1000) | URL do arquivo no Azure Blob Storage |
| `evidence_validation_result` | JSONB | Resultado detalhado do OCR e validação |
| `receita_anterior_id` | UUID | Referência à receita anterior |
| `receita_anterior_validade` | DATE | Data de validade da receita anterior |
| `receita_gerada_url` | VARCHAR(1000) | URL do PDF da nova receita gerada |
| `consecutive_renewal_count` | INTEGER | Contador de renovações consecutivas |
| `tentativas_dia` | INTEGER | Contador diário de tentativas do paciente |
| `data_solicitacao` | TIMESTAMPTZ | Data e hora de criação |
| `data_atualizacao` | TIMESTAMPTZ | Data e hora da última atualização |
| `criado_por` | UUID | ID do usuário autenticado |
| `revisado_por` | UUID | ID do revisor (se aplicável) |
| `data_revisao` | TIMESTAMPTZ | Data da revisão manual (se aplicável) |

**Status possíveis:** `PENDING_EVIDENCE`, `PENDING_REVIEW`, `PROCESSING`, `APPROVED`, `REJECTED_INVALID_FILE`, `REJECTED_UNREADABLE`, `REJECTED_IDENTITY_MISMATCH`, `REJECTED_BLOCKED_MEDICATION`, `REJECTED_NOT_CONTINUOUS`, `REJECTED_NO_HISTORY`, `REJECTED_TOO_EARLY`, `REJECTED_EXPIRED`, `REJECTED_RENEWAL_LIMIT`, `REJECTED_REVIEW_TIMEOUT`, `FAILED_PDF_GENERATION`

### 8.2 `medicamento` (Medicine DB — PostgreSQL)

| Campo | Tipo | Descrição |
|---|---|---|
| `codigo` | VARCHAR(20) | Código único do medicamento |
| `nome_generico` | VARCHAR(200) | Nome genérico |
| `nome_comercial` | VARCHAR(200) | Nome comercial (pode ser nulo) |
| `classe_terapeutica` | VARCHAR(100) | Classe terapêutica |
| `uso_continuo` | BOOLEAN | Se é classificado como uso contínuo |
| `bloqueado` | BOOLEAN | Se está na lista de bloqueio |
| `motivo_bloqueio` | VARCHAR(500) | Motivo do bloqueio |
| `data_inclusao_bloqueio` | TIMESTAMPTZ | Data em que foi bloqueado |

### 8.3 `historico_prescricao_paciente` (Write DB — PostgreSQL)

| Campo | Tipo | Descrição |
|---|---|---|
| `id` | UUID | Identificador único |
| `paciente_id` | UUID | Referência ao paciente |
| `medicamento_codigo` | VARCHAR(20) | Código do medicamento |
| `data_prescricao` | DATE | Data da prescrição original |
| `data_validade` | DATE | Data de validade da receita |
| `origem` | ENUM | `MANUAL` (médico) ou `SISTEMA` (automático) |
| `consecutive_renewal_count` | INTEGER | Contador de renovações consecutivas |
| `solicitacao_id` | UUID | Referência à solicitação (se `SISTEMA`) |

### 8.4 `visao_solicitacao` (Read DB — MongoDB)

Documento desnormalizado para leitura rápida:

```json
{
  "_id": "uuid-da-solicitacao",
  "pacienteId": "uuid-do-paciente",
  "medicamentoNome": "Metformina 500mg",
  "status": "APPROVED",
  "statusDescricao": "Receita gerada com sucesso",
  "dataSolicitacao": "2026-03-29T20:00:00-03:00",
  "dataAtualizacao": "2026-03-29T20:05:00-03:00",
  "receitaUrl": "https://blob.azure.com/.../receita-uuid.pdf",
  "historicoStatus": [
    {"status": "PENDING_EVIDENCE", "timestamp": "2026-03-29T20:00:00-03:00"},
    {"status": "PROCESSING", "timestamp": "2026-03-29T20:02:00-03:00"},
    {"status": "APPROVED", "timestamp": "2026-03-29T20:05:00-03:00"}
  ]
}
```

### 8.5 Payloads dos Eventos Kafka

**`prescription.requested`**
```json
{
  "eventId": "uuid",
  "eventType": "PrescriptionRequested",
  "eventTimestamp": "2026-03-29T20:00:00Z",
  "solicitacaoId": "uuid",
  "pacienteId": "uuid",
  "medicamentoCodigo": "string",
  "evidenceBlobUrl": "string",
  "receitaAnteriorValidade": "2026-04-08"
}
```

**`evidence.validated`**
```json
{
  "eventId": "uuid",
  "eventType": "EvidenceValidated",
  "eventTimestamp": "2026-03-29T20:02:00Z",
  "solicitacaoId": "uuid",
  "resultado": "VALID | INVALID | PARTIAL",
  "motivoRejeicao": "string | null",
  "ocrData": {
    "dataReceita": "2025-12-01",
    "nomeMedicamento": "Metformina",
    "nomePaciente": "João da Silva",
    "confiancaGeral": 0.92
  }
}
```

**`prescription.approved`**
```json
{
  "eventId": "uuid",
  "eventType": "PrescriptionApproved",
  "eventTimestamp": "2026-03-29T20:05:00Z",
  "solicitacaoId": "uuid",
  "pacienteId": "uuid",
  "receitaUrl": "string",
  "validadeNovaReceita": "2026-06-29"
}
```

**`prescription.rejected`**
```json
{
  "eventId": "uuid",
  "eventType": "PrescriptionRejected",
  "eventTimestamp": "2026-03-29T20:03:00Z",
  "solicitacaoId": "uuid",
  "pacienteId": "uuid",
  "motivoRejeicao": "REJECTED_BLOCKED_MEDICATION",
  "mensagemUsuario": "O medicamento solicitado requer consulta presencial.",
  "origemRejeicao": "PRESCRIPTION_SERVICE | EVIDENCE_SERVICE"
}
```

**`prescription.pending-review`**
```json
{
  "eventId": "uuid",
  "eventType": "PrescriptionPendingReview",
  "eventTimestamp": "2026-03-29T20:02:00Z",
  "solicitacaoId": "uuid",
  "pacienteId": "uuid",
  "motivoPendencia": "OCR extraiu dados parciais — revisão humana necessária",
  "ocrData": {}
}
```

---

## 9. Pontos de Integração

### 9.1 OCR — Azure AI Document Intelligence

- **Serviço responsável:** Evidence Service
- **Onde no fluxo:** Passo 5 — após armazenamento no Blob, antes de publicar `evidence.validated`
- **Modelo:** `prebuilt-document` (pode evoluir para modelo customizado)
- **Campos esperados:** data, nome do medicamento, nome do paciente, CRM do médico (opcional)
- **Fallback:** Se OCR indisponível (timeout/5xx) → encaminha para `PENDING_REVIEW`
- **Configuração:** `OCR_ENDPOINT`, `OCR_API_KEY` via variáveis de ambiente

### 9.2 Regras Clínicas / "IA"

- Nesta fase: **lógica de negócio codificada** no Prescription Service (Java), não LLM ou ML externo.
- Justificativa: regras são determinísticas, auditáveis e seguras. LLM introduziria risco de alucinação em contexto médico.
- Expansão futura: motor de regras (ex.: Drools) ou serviço de ML dedicado.

### 9.3 Autenticação — JWT (RS256)

- **Claims obrigatórios:** `sub` (UUID do usuário), `cpf` (somente PATIENT), `role`, `exp`, `iat`
- **Access token:** expiração de 1 hora
- **Refresh token:** expiração de 7 dias
- **Validação:** API Gateway valida assinatura e expiração antes de rotear
- **Chaves:** Par RSA via `JWT_PRIVATE_KEY`, `JWT_PUBLIC_KEY`

### 9.4 Azure Blob Storage

- **Organização:** `evidencias/{pacienteId}/{solicitacaoId}/{arquivo}`
- **Retenção:** 5 anos para fins de auditoria
- **Segurança:** Acesso público desabilitado; URLs com SAS token de 1 hora
- **Configuração:** `AZURE_STORAGE_CONNECTION_STRING`, `AZURE_BLOB_CONTAINER_NAME`

### 9.5 Apache Kafka

- **Versão:** Kafka 3.x
- **Configuração:** Retenção 7 dias, 3 partições, fator de replicação 3 (produção)
- **Dead Letter Topics:** `*.dlq` para cada tópico principal
- **Serialização:** JSON com campo `eventType` como discriminador de versão
- **Idempotência:** `eventId` UUID usado para deduplicação nos consumers

---

## 10. Requisitos Não-Funcionais

### 10.1 Performance

| Métrica | Meta |
|---|---|
| Latência de criação de solicitação (POST) | p95 < 500ms (excluindo upload) |
| Latência de upload de arquivo (multipart) | p95 < 3s para arquivos de até 10 MB |
| Latência de consulta de status (GET) | p95 < 100ms (MongoDB) |
| Tempo de processamento ponta a ponta (happy path) | < 30 segundos em 95% dos casos |
| Throughput inicial | 100 solicitações simultâneas |

### 10.2 Segurança

| Requisito | Descrição |
|---|---|
| Autenticação | Todos os endpoints protegidos exigem JWT válido (RS256) |
| Dados sensíveis | CPF armazenado como hash (SHA-256); CPF em texto plano mascarado nos logs |
| Comunicação | HTTPS obrigatório em rotas externas; TLS entre serviços internos |
| Auditoria | Todas as ações registradas em tabela de auditoria imutável |
| LGPD | Dados pessoais sob base legal de saúde pública; política de retenção e exclusão documentada |

### 10.3 Disponibilidade e Resiliência

| Requisito | Meta |
|---|---|
| Disponibilidade do sistema | 99,5% (excluindo janelas de manutenção) |
| Timeout de OCR | 10 segundos; fallback para `PENDING_REVIEW` |
| Retentativas Kafka | 3 tentativas com backoff exponencial antes de DLQ |
| Circuit Breaker | Resilience4j em chamadas ao OCR e Azure Blob |

### 10.4 Escalabilidade

- Todos os serviços são stateless e escaláveis horizontalmente
- Prescription Query e Evidence Service escalam independentemente
- Kafka suporta aumento de partições sem redesign
- MongoDB suporta sharding por `pacienteId`

### 10.5 Observabilidade

- Logs estruturados (JSON) com correlation ID por solicitação
- Métricas via Actuator + Prometheus
- Rastreamento distribuído via OpenTelemetry + Jaeger
- Alertas para: DLQ com mensagens pendentes, error rate > 5%, latência p95 acima da meta

---

## 11. Critérios de Aceitação

### 11.1 Autenticação e Autorização

| ID | Critério |
|---|---|
| CA-001 | Paciente não autenticado que chama `POST /api/v1/receitas/solicitacoes` recebe HTTP 401 |
| CA-002 | Paciente autenticado com perfil `PATIENT` consegue criar uma solicitação no caminho feliz |
| CA-003 | Paciente com perfil `PATIENT` que acessa `GET /api/v1/admin/receitas/revisao` recebe HTTP 403 |
| CA-004 | Token expirado resulta em HTTP 401 com mensagem clara |

### 11.2 Validação de Arquivo

| ID | Critério | Regra |
|---|---|---|
| CA-005 | Upload de arquivo corrompido resulta em `REJECTED_INVALID_FILE` com HTTP 422 | RN-001 |
| CA-006 | Upload de imagem ilegível resulta em `REJECTED_UNREADABLE` | RN-002, RN-004 |
| CA-007 | Upload de imagem com dados parciais resulta em `PENDING_REVIEW` | RN-003 |
| CA-008 | Arquivo com mais de 10 MB é rejeitado no API Gateway antes de processar | RN-001 |

### 11.3 Regras de Prescrição

| ID | Critério | Regra |
|---|---|---|
| CA-009 | Medicamento bloqueado resulta em `REJECTED_BLOCKED_MEDICATION` | RN-011 |
| CA-010 | Medicamento não de uso contínuo resulta em `REJECTED_NOT_CONTINUOUS` | RN-009 |
| CA-011 | Sem histórico prévio resulta em `REJECTED_NO_HISTORY` | RN-010 |
| CA-012 | CPF divergente entre JWT e OCR resulta em `REJECTED_IDENTITY_MISMATCH` | RN-017 |
| CA-013 | Solicitação dentro da janela de 10 dias é processada normalmente | RN-006 |
| CA-014 | Mais de 10 dias de antecedência resulta em `REJECTED_TOO_EARLY` com data de liberação | RN-007 |
| CA-015 | Receita expirada há mais de 30 dias resulta em `REJECTED_EXPIRED` | RN-008 |
| CA-016 | Após 2 renovações consecutivas, resulta em `REJECTED_RENEWAL_LIMIT` | RN-014, RN-015 |

### 11.4 Anti-Spam

| ID | Critério | Regra |
|---|---|---|
| CA-017 | 4ª solicitação no mesmo dia retorna HTTP 429 com header `Retry-After` | RN-022, RN-023 |
| CA-018 | Contador de tentativas diárias reseta à meia-noite horário de Brasília | RN-022 |

### 11.5 Caminho Feliz

| ID | Critério |
|---|---|
| CA-019 | Solicitação válida com evidência válida resulta em `APPROVED` com URL do PDF em até 30s |
| CA-020 | O PDF contém: nome do paciente, medicamento, dosagem, data de emissão e validade |
| CA-021 | `GET /api/v1/receitas/{id}` retorna histórico completo de transições de status |

### 11.6 Revisão Manual

| ID | Critério | Regra |
|---|---|---|
| CA-022 | Solicitações `PENDING_REVIEW` aparecem no endpoint de revisão para `REVIEWER` | RN-025, RN-026 |
| CA-023 | Revisor que aprova um caso em `PENDING_REVIEW` dispara geração do PDF | RN-026 |
| CA-024 | Sem ação após 5 dias úteis → rejeição automática com `REJECTED_REVIEW_TIMEOUT` | RN-027 |

---

## 12. Stack Tecnológica

### 12.1 Backend

| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 21 (LTS) | Linguagem principal de todos os serviços |
| Spring Boot | 3.3.x | Framework base dos microserviços |
| Spring Cloud Gateway | 4.x | API Gateway |
| Spring Cloud Netflix Eureka | 4.x | Service Discovery |
| Spring Security | 6.x | Autenticação e autorização JWT |
| Spring Data JPA | 3.x | ORM para PostgreSQL |
| Spring Data MongoDB | 4.x | Acesso ao MongoDB (Read DB) |
| Spring Kafka | 3.x | Integração com Apache Kafka |
| Resilience4j | 2.x | Circuit Breaker, Retry, Rate Limiter |
| OpenAPI / Springdoc | 2.x | Documentação automática dos endpoints |

### 12.2 Infraestrutura

| Tecnologia | Versão | Uso |
|---|---|---|
| Apache Kafka | 3.6.x | Mensageria assíncrona entre serviços |
| PostgreSQL | 16.x | Write DB e Medicine DB |
| MongoDB | 7.x | Read DB (visão materializada) |
| Azure Blob Storage | — | Armazenamento de arquivos de evidência |
| Azure AI Document Intelligence | v3.1 | Serviço de OCR |
| Docker | 25.x | Containerização dos serviços |
| Kubernetes | 1.29.x | Orquestração de containers (produção) |
| Docker Compose | 2.x | Ambiente local de desenvolvimento |

### 12.3 Observabilidade

| Tecnologia | Uso |
|---|---|
| OpenTelemetry | Rastreamento distribuído e métricas |
| Prometheus | Coleta de métricas |
| Grafana | Dashboards de monitoramento |
| Jaeger | Visualização de traces distribuídos |
| Loki | Agregação de logs estruturados |

### 12.4 Testes

| Tecnologia | Uso |
|---|---|
| JUnit 5 | Testes unitários |
| Mockito | Mocks em testes unitários |
| Testcontainers | Testes de integração com containers reais |
| WireMock | Mock de serviços externos (OCR, Azure Blob) |

### 12.5 Decisões de Arquitetura Abertas

| Decisão | Opção A (recomendada) | Opção B | Critério |
|---|---|---|---|
| Modelo OCR | Azure AI Document Intelligence (pré-treinado) | Modelo customizado para receitas SUS | Custo vs. precisão; começar com pré-treinado |
| Geração de PDF | Apache PDFBox (open source) | iText 7 (comercial) | Custo de licença; PDFBox para MVP |
| Contador anti-spam (RN-022) | Coluna `tentativas_dia` no PostgreSQL | Redis com TTL automático | Redis preferível se já estiver no stack |
| Auth Service | Serviço dedicado no monorepo | Keycloak (IAM externo) | Keycloak reduz código mas adiciona dependência operacional |
| Regras clínicas futuras | Drools (motor de regras) | Serviço Python com ML | Drools para regras determinísticas; ML apenas quando comprovado |

---

## 13. Mapeamento de Nomenclatura

| Contexto | Termo Anterior | Termo Novo |
|---|---|---|
| Interface com o usuário (PT-BR) | Renovação | Nova Receita Médica |
| Endpoint da API | `POST /renewal` | `POST /api/v1/receitas/solicitacoes` |
| Evento Kafka | `RenewalRequested` | `PrescriptionRequested` |
| Evento Kafka | `RenewalApproved` | `PrescriptionApproved` |
| Evento Kafka (novo) | — | `PrescriptionRejected` |
| Evento Kafka (novo) | — | `PrescriptionPendingReview` |
| Entidade de banco | `renewal` | `solicitacao_receita` |
