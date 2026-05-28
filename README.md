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
- Maven 3.9+ (ou use o wrapper `./mvnw`)

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

# Upload de arquivos (máx. 10 MB)
spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

> **Nota:** nunca comite credenciais reais. Use variáveis de ambiente em produção.

---

## Banco de Dados

### Novo banco (primeira vez)

Execute o script completo de criação:

```bash
mysql -u root -p < src/main/resources/db/schema.sql
```

### Banco existente (atualização para v1.3.0)

Execute o script de migração:

```bash
mysql -u root -p < src/main/resources/db/migration.sql
```

O Hibernate com `ddl-auto=update` gerencia alterações incrementais automaticamente após isso.

---

## Como executar

### Via Maven (terminal)

```bash
mvn spring-boot:run
```

### Via NetBeans

1. Clique com o botão direito no projeto → **Properties → Actions**
2. Selecione a ação **Run Project**
3. Altere o goal para `spring-boot:run`
4. Clique em OK e use **Run → Run Project** (F6)

### Via JAR

```bash
mvn clean package -DskipTests
java -jar target/esteira-*.jar
```

A API sobe em `http://localhost:8080`.

---

## Endpoints Principais

### Clientes

| Método | Rota | Descrição |
|---|---|---|
| GET | `/clientes` | Lista todos os clientes |
| GET | `/clientes/{id}` | Busca cliente por ID |
| POST | `/clientes` | Cadastra novo cliente |
| PUT | `/clientes/{id}` | Atualiza cliente |
| DELETE | `/clientes/{id}` | Remove cliente |

### Propostas

| Método | Rota | Descrição |
|---|---|---|
| GET | `/propostas` | Lista todas as propostas |
| GET | `/propostas/{id}` | Busca proposta por ID |
| POST | `/propostas` | Cria nova proposta (código gerado automaticamente) |
| PUT | `/propostas/{id}` | Atualiza proposta |
| DELETE | `/propostas/{id}` | Remove proposta |

### Esteira de Aprovação

| Método | Rota | Descrição |
|---|---|---|
| GET | `/esteira/{id}/acoes` | Lista ações disponíveis para o status atual |
| POST | `/esteira/{id}/{acao}` | Executa ação (`ENVIAR_ANALISE`, `APROVAR`, `REPROVAR`, `REABRIR`) |

**Body do POST `/esteira/{id}/{acao}`:**
```json
{
  "observacao": "Comentário opcional",
  "canais": ["SMS", "WHATSAPP"]
}
```
> `EMAIL` é sempre enviado automaticamente. Valores possíveis para `canais`: `SMS`, `WHATSAPP`, `FACEBOOK`.

### Documentos

| Método | Rota | Descrição |
|---|---|---|
| GET | `/documentos/proposta/{id}` | Lista documentos de uma proposta |
| POST | `/documentos/proposta/{id}` | Upload de PDF (`multipart/form-data`, campo `arquivo`) |
| GET | `/documentos/{id}/download` | Download do PDF |
| DELETE | `/documentos/{id}` | Remove documento |

---

## Design Patterns Utilizados

### State Pattern — Ciclo de vida da Proposta
Cada status (`RASCUNHO`, `EM_ANALISE`, `APROVADA`, `REPROVADA`) é representado por uma classe concreta de `EstadoProposta`. A transição é feita pelo próprio objeto de estado, impedindo transições inválidas.

```
EstadoProposta (interface)
├── EstadoRascunho      → permite: enviarParaAnalise()
├── EstadoEmAnalise     → permite: aprovar(), reprovar()
├── EstadoAprovada      → permite: reabrir()
└── EstadoReprovada     → permite: reabrir()
```

### Command Pattern — Ações da esteira
Cada ação é encapsulada em um comando desacoplado do controller.

```
ComandoEsteira (interface)
├── ComandoEnviarAnalise
├── ComandoAprovar
├── ComandoReprovar
└── ComandoReabrir
```
`FabricaComandoEsteira.criar(acao)` instancia o comando correto.

### Decorator Pattern — Notificações
`NotificacaoEmail` é o componente base (sempre executado). Canais opcionais decoram dinamicamente a cadeia em runtime.

```
Notificavel (interface)
└── NotificacaoEmail (base obrigatória)
    └── NotificacaoSMS (opcional)
        └── NotificacaoWhatsApp (opcional)
            └── NotificacaoFacebook (opcional)
```

### Builder Pattern — Entidades JPA
Todas as entidades (`Cliente`, `Proposta`, `Documento`) possuem inner class `Builder` para construção fluente.

### Factory Method — FabricaComandoEsteira
Centraliza a criação dos comandos e a consulta das ações disponíveis por status.

---

## Estrutura de Pastas

```
esteira-backend/
└── src/main/java/com/esteira/
    ├── config/          # Beans Spring (NotificacaoConfig, etc.)
    ├── controller/      # REST Controllers
    ├── model/           # Entidades JPA (Cliente, Proposta, Documento)
    ├── repository/      # Interfaces Spring Data JPA
    ├── service/         # Lógica de negócio
    ├── estado/          # State Pattern
    ├── comando/         # Command Pattern
    ├── fabrica/         # Factory Method
    └── notificacao/     # Decorator Pattern
```

---

## Regras de Negócio

- **RN-01** Código da proposta gerado automaticamente no formato `PROP-YYYYMMDD-NNNNN`.
- **RN-02** Documento PDF obrigatório na criação da proposta.
- **RN-03** PDF validado por Content-Type **e** magic bytes (`%PDF`).
- **RN-04** E-mail sempre enviado; outros canais são opcionais.
- **RN-05** Fluxo de status: `RASCUNHO → EM_ANALISE → APROVADA/REPROVADA → RASCUNHO`.
- **RN-06** Tamanho máximo de upload: 10 MB.

---

## Versão

`v1.3.0` — Documento PDF obrigatório, código de proposta automático, notificações multicanal.
