# Esteira de Propostas — Backend

API REST desenvolvida em **Spring Boot 3.2.5 / Java 21** que gerencia o ciclo de vida de propostas comerciais, incluindo fluxo de aprovação por esteira, upload de documentos PDF e notificações multicanal.

---

## Tecnologias

| Tecnologia | Versão |
|---|---|
| Java | 21 |
| Spring Boot | 3.2.5 |
| Spring Data JPA / Hibernate | — |
| Spring Mail | — |
| MySQL | 8.x |
| Maven | 3.9+ |

---

## Pré-requisitos

- JDK 21 instalado e configurado no `JAVA_HOME`
- MySQL 8 rodando localmente (ou acessível via rede)
- Conta Gmail com **App Password** habilitado (autenticação de dois fatores)
- Maven 3.9+

---

## Configuração

Edite `src/main/resources/application.properties`:

```properties
# Banco de dados
spring.datasource.url=jdbc:mysql://localhost:3306/esteira_db
spring.datasource.username=SEU_USUARIO
spring.datasource.password=SUA_SENHA

# E-mail (Gmail com App Password)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=SEU_EMAIL@gmail.com
spring.mail.password=SUA_APP_PASSWORD
app.email.from=SEU_EMAIL@gmail.com

# Upload (máx. 10 MB)
spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

> **Atenção:** nunca comite credenciais reais. Use variáveis de ambiente em produção.

---

## Banco de Dados

### Novo banco (primeira vez)
```bash
mysql -u root -p < src/main/resources/db/schema.sql
```

### Banco existente (migração para v1.3.0)
```bash
mysql -u root -p < src/main/resources/db/migration.sql
```

---

## Como executar

```bash
# Desenvolvimento
mvn spring-boot:run

# JAR de produção
mvn clean package -DskipTests
java -jar target/esteira-*.jar
```

**NetBeans:** botão direito no projeto → Properties → Actions → Run Project → altere o goal para `spring-boot:run`.

A API sobe em `http://localhost:8080`.

---

## Referência completa da API

> Base URL: `http://localhost:8080`

---

### Clientes — `/clientes`

#### `GET /clientes`
Lista todos os clientes.

**Resposta 200:**
```json
[
  {
    "id": 1,
    "nome": "João Silva",
    "cpfCnpj": "123.456.789-00",
    "email": "joao@email.com",
    "telefone": "11999990000",
    "tipo": "PESSOA_FISICA",
    "ativo": true
  }
]
```

---

#### `GET /clientes/{id}`
Busca cliente por ID.

**Resposta 200:** objeto cliente.

**Erros:**
| Código | Corpo | Situação |
|---|---|---|
| 404 | `{"erro": "Cliente não encontrado: id=99"}` | ID não existe |

---

#### `POST /clientes`
Cadastra novo cliente.

**Body:**
```json
{
  "nome": "João Silva",
  "cpfCnpj": "123.456.789-00",
  "email": "joao@email.com",
  "telefone": "11999990000",
  "endereco": "Rua A, 100",
  "cidade": "São Paulo",
  "estado": "SP",
  "cep": "01310-100",
  "tipo": "PESSOA_FISICA"
}
```

**Resposta 201:** objeto cliente criado.

**Erros:**
| Código | Corpo | Situação |
|---|---|---|
| 400 | `{"erro": "Já existe um cliente com este CPF/CNPJ."}` | CPF/CNPJ duplicado |
| 400 | `{"erro": "Nome é obrigatório", "campos": {"nome": "..."}}` | Campo `nome` vazio |
| 400 | `{"erro": "CPF/CNPJ é obrigatório", "campos": {"cpfCnpj": "..."}}` | Campo `cpfCnpj` vazio |
| 400 | `{"erro": "E-mail é obrigatório", "campos": {"email": "..."}}` | Campo `email` vazio |

---

#### `PUT /clientes/{id}`
Atualiza cliente. Mesmo body do POST.

**Erros:**
| Código | Corpo | Situação |
|---|---|---|
| 404 | `{"erro": "Cliente não encontrado: id=99"}` | ID não existe |

---

#### `DELETE /clientes/{id}`
Remove cliente.

**Resposta 204:** sem corpo.

**Erros:**
| Código | Corpo | Situação |
|---|---|---|
| 404 | `{"erro": "Cliente não encontrado: id=99"}` | ID não existe |

---

### Propostas — `/propostas`

#### `GET /propostas`
Lista todas as propostas ordenadas por data de criação (desc).

Filtro opcional por status: `GET /propostas?status=ANALISE`

Valores válidos para `status`: `RASCUNHO`, `ANALISE`, `APROVADA`, `REPROVADA`.

---

#### `GET /propostas/{id}`
Busca proposta por ID.

**Erros:**
| Código | Corpo | Situação |
|---|---|---|
| 404 | `{"erro": "Proposta não encontrada: id=99"}` | ID não existe |

---

#### `POST /propostas`
Cria nova proposta. O campo `codigo` é gerado automaticamente pelo backend no formato `PROP-YYYYMMDD-NNNNN`.

**Body:**
```json
{
  "titulo": "Desenvolvimento de Sistema",
  "descricao": "Sistema de gestão para a empresa X",
  "valor": 25000.00,
  "observacoes": "Prazo: 3 meses",
  "cliente": { "id": 1 }
}
```

**Resposta 201:**
```json
{
  "id": 10,
  "codigo": "PROP-20260602-00010",
  "titulo": "Desenvolvimento de Sistema",
  "valor": 25000.00,
  "status": "RASCUNHO",
  "etapaAtual": "CADASTRO",
  "dataCriacao": "2026-06-02T10:00:00.000+00:00"
}
```

**Erros:**
| Código | Corpo | Situação |
|---|---|---|
| 400 | `{"erro": "Título obrigatório."}` | Campo `titulo` ausente ou vazio |
| 400 | `{"erro": "Valor deve ser maior que zero."}` | Campo `valor` zero ou negativo |
| 400 | `{"erro": "Cliente obrigatório."}` | Campo `cliente` ausente |

---

#### `PUT /propostas/{id}`
Atualiza proposta. **Permitido apenas no status `RASCUNHO`.**

**Erros:**
| Código | Corpo | Situação |
|---|---|---|
| 400 | `{"erro": "Edição permitida apenas em RASCUNHO."}` | Proposta não está em RASCUNHO |
| 404 | `{"erro": "Proposta não encontrada: id=99"}` | ID não existe |

---

#### `DELETE /propostas/{id}`
Remove proposta.

**Resposta 204:** sem corpo.

**Erros:**
| Código | Corpo | Situação |
|---|---|---|
| 404 | `{"erro": "Proposta não encontrada: id=99"}` | ID não existe |

---

### Documentos — `/documentos`

#### `GET /documentos/proposta/{id}`
Lista os metadados dos documentos de uma proposta (sem os bytes do PDF).

---

#### `POST /documentos/proposta/{id}`
Upload de PDF vinculado a uma proposta.

**Content-Type:** `multipart/form-data`

| Campo | Tipo | Obrigatório |
|---|---|---|
| `arquivo` | File (PDF) | Sim |
| `descricao` | Text | Não |

**Exemplo no Postman:**
- Método: `POST`
- URL: `http://localhost:8080/documentos/proposta/10`
- Body: `form-data` → chave `arquivo` (tipo File) → selecione o PDF

**Resposta 201:**
```json
{
  "id": 5,
  "nome": "contrato.pdf",
  "tipo": "application/pdf",
  "descricao": "Contrato assinado"
}
```

**Erros:**
| Código | Corpo | Situação |
|---|---|---|
| 400 | `{"erro": "Nenhum arquivo enviado."}` | Campo `arquivo` vazio |
| 400 | `{"erro": "Apenas arquivos PDF são aceitos. Tipo recebido: image/png"}` | Arquivo não é PDF pelo Content-Type |
| 400 | `{"erro": "O arquivo não é um PDF válido (assinatura de arquivo inválida)."}` | Arquivo com extensão .pdf mas conteúdo inválido (magic bytes errados) |
| 404 | `{"erro": "Proposta não encontrada: id=99"}` | Proposta não existe |

---

#### `GET /documentos/{id}/download`
Retorna o binário do PDF.

**Resposta 200:** binário com `Content-Type: application/pdf`.

**Erros:**
| Código | Corpo | Situação |
|---|---|---|
| 404 | `{"erro": "Documento não encontrado: id=99"}` | Documento não existe |

---

#### `DELETE /documentos/{id}`
Remove documento.

**Resposta 204:** sem corpo.

**Erros:**
| Código | Corpo | Situação |
|---|---|---|
| 404 | `{"erro": "Documento não encontrado: id=99"}` | Documento não existe |

---

### Esteira de Aprovação — `/esteira`

#### `GET /esteira/{propostaId}/acoes`
Retorna as ações disponíveis para o status atual da proposta.

**Resposta 200:**
```json
{
  "propostaId": 10,
  "acoesDisponiveis": ["ENVIAR_ANALISE"]
}
```

Mapeamento de ações por status:

| Status atual | Ações disponíveis |
|---|---|
| `RASCUNHO` | `ENVIAR_ANALISE` |
| `ANALISE` | `APROVAR`, `REPROVAR` |
| `REPROVADA` | `REABRIR` |
| `APROVADA` | *(nenhuma)* |

**Erros:**
| Código | Corpo | Situação |
|---|---|---|
| 404 | `{"erro": "Proposta não encontrada: id=99"}` | Proposta não existe |

---

#### `POST /esteira/{propostaId}/{acao}`
Executa uma ação na esteira.

Valores válidos para `{acao}`: `ENVIAR_ANALISE`, `APROVAR`, `REPROVAR`, `REABRIR`.

**Body (todos os campos opcionais):**
```json
{
  "observacao": "Aprovado após análise jurídica",
  "canais": ["SMS", "WHATSAPP"]
}
```

> `EMAIL` é sempre enviado automaticamente. Valores possíveis para `canais`: `SMS`, `WHATSAPP`, `FACEBOOK`.

**Resposta 200:**
```json
{
  "mensagem": "Ação executada com sucesso.",
  "propostaId": 10,
  "codigo": "PROP-20260602-00010",
  "status": "ANALISE",
  "etapa": "ANALISE"
}
```

**Erros:**
| Código | Corpo | Situação |
|---|---|---|
| 400 | `{"erro": "Não é possível reprovar uma proposta em rascunho."}` | Ação inválida para o status atual |
| 400 | `{"erro": "Proposta já aprovada. Nenhuma transição disponível."}` | Tentar avançar proposta aprovada |
| 400 | `{"erro": "Não é possível reabrir uma proposta em análise."}` | Tentar reabrir proposta em análise |
| 400 | `{"erro": "Proposta já está reprovada."}` | Tentar reprovar proposta já reprovada |
| 400 | `{"erro": "Use 'reabrir' para retornar ao rascunho."}` | Tentar avançar proposta reprovada |
| 400 | `{"erro": "Ação desconhecida: XPTO"}` | Ação enviada não existe |
| 404 | `{"erro": "Proposta não encontrada: id=99"}` | Proposta não existe |

---

## Erros Globais

Erros simples retornam:
```json
{ "erro": "Mensagem descritiva do problema." }
```

Erros de validação de campos retornam adicionalmente o mapa de campos:
```json
{
  "erro": "Nome é obrigatório. E-mail é obrigatório",
  "campos": {
    "nome": "Nome é obrigatório",
    "email": "E-mail é obrigatório"
  }
}
```

| Código HTTP | Situação |
|---|---|
| 400 Bad Request | Regra de negócio violada, campo inválido, ação inválida, arquivo inválido |
| 404 Not Found | Recurso não encontrado (cliente, proposta ou documento inexistente) |
| 500 Internal Server Error | Erro inesperado no servidor |

---

## Testes

```bash
# Rodar todos os testes
mvn test

# Rodar um teste específico
mvn test -Dtest=NotificacaoDecoratorTest
mvn test -Dtest=PropostaServiceTest
mvn test -Dtest=PropostaTest
```

Cobertura dos testes:

**Testes unitários (sem banco):**
- `NotificacaoDecoratorTest` — cadeia Decorator de notificações (7 cenários)
- `PropostaServiceTest` — geração de código, unicidade, CRUD com Mockito (9 cenários)
- `PropostaTest` — Builder, validações e transições de estado (12 cenários)

**Testes de integração (banco H2 em memória):**
- `PropostaRepositoryTest` — salvar, listar, filtrar por status, existsByCodigo, deletar (6 cenários)
- `ClienteRepositoryTest` — salvar, existsByCpfCnpj, unicidade, listar, deletar (5 cenários)

---

## Design Patterns

| Pattern | Onde | Finalidade |
|---|---|---|
| **State** | `estado/` | Controla transições de status da proposta |
| **Command** | `command/` | Encapsula cada ação da esteira |
| **Factory Method** | `factory/FabricaComandoEsteira` | Instancia o comando correto por ação |
| **Decorator** | `notification/` | Monta cadeia de notificações em runtime |
| **Builder** | Entidades JPA | Construção fluente de `Cliente`, `Proposta`, `Documento` |

---

## Versão

`v1.6.0` — Testes de integração H2. `v1.5.1` — E-mail obrigatório, toast por campo, campos com borda vermelha. `v1.5.0` — Validação de campos via backend. `v1.4.0` — Testes unitários JUnit. `v1.3.0` — PDF obrigatório, código automático, notificações multicanal.
