# Esteira de Propostas — Backend

API REST desenvolvida com **Spring Boot 3** para gerenciamento e tramitação de propostas comerciais.

## Stack
- Java 21 + Spring Boot 3.2
- Spring Data JPA + Hibernate + MySQL
- Spring Mail (notificações por e-mail)

## Design Patterns
| Pattern | Onde |
|---------|------|
| State | `state/` — controla transições da esteira |
| Command | `command/` — encapsula cada ação da esteira |
| Factory Method | `factory/FabricaComandoEsteira` |
| Decorator | `notification/` — cadeia Email→SMS→WhatsApp→Facebook |
| Builder | Models `Proposta`, `Cliente`, `Documento` |
| MVC + Repository | Controllers + Services + Repositories |

## Fluxo da Esteira
```
RASCUNHO → ANALISE → APROVADA
                  ↘ REPROVADA → RASCUNHO
```

## Endpoints principais
| Método | URL | Descrição |
|--------|-----|-----------|
| GET | /api/propostas | Lista propostas (filtro ?status=) |
| POST | /api/propostas | Cria proposta |
| POST | /api/esteira/{id}/{acao} | Executa ação na esteira |
| GET | /api/esteira/{id}/acoes | Ações disponíveis |

## Como rodar
```bash
# 1. Configure application.properties (DB + e-mail)
# 2. Execute
mvn spring-boot:run
```
