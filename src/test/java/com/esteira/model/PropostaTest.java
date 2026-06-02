package com.esteira.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Testes unitários — Proposta (Builder + State transitions).
 *
 * Cobre:
 *   - Builder: criação com dados válidos
 *   - Builder: validações de campos obrigatórios
 *   - Transições de estado via State Pattern:
 *       RASCUNHO → EM_ANALISE → APROVADA
 *       RASCUNHO → EM_ANALISE → REPROVADA → RASCUNHO
 *   - Transições inválidas lançam exceção
 */
class PropostaTest {

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = new Cliente.Builder()
                .nome("Carlos Lima")
                .cpfCnpj("111.222.333-44")
                .email("carlos@empresa.com")
                .build();
    }

    // ================================================================
    // 1. Builder — criação válida
    // ================================================================

    @Test
    @DisplayName("Builder: cria Proposta com campos obrigatórios preenchidos")
    void builder_deveCriarPropostaValida() {
        Proposta p = new Proposta.Builder()
                .titulo("Proposta de Desenvolvimento")
                .descricao("Sistema de gestão")
                .valor(25000.0)
                .cliente(cliente)
                .codigo("PROP-20260602-00001")
                .observacoes("Urgente")
                .build();

        assertThat(p.getTitulo()).isEqualTo("Proposta de Desenvolvimento");
        assertThat(p.getValor()).isEqualTo(25000.0);
        assertThat(p.getCliente()).isEqualTo(cliente);
        assertThat(p.getCodigo()).isEqualTo("PROP-20260602-00001");
        assertThat(p.getStatus()).isEqualTo("RASCUNHO");
    }

    @Test
    @DisplayName("Builder: status inicial é RASCUNHO")
    void builder_statusInicialDeveSerRascunho() {
        Proposta p = new Proposta.Builder()
                .titulo("Proposta")
                .valor(1000.0)
                .cliente(cliente)
                .build();

        assertThat(p.getStatus()).isEqualTo("RASCUNHO");
        assertThat(p.getEstado()).isNotNull();
    }

    // ================================================================
    // 2. Builder — validações
    // ================================================================

    @Test
    @DisplayName("Builder: lança exceção quando título é nulo")
    void builder_deveLancarExcecaoSemTitulo() {
        assertThatThrownBy(() ->
                new Proposta.Builder()
                        .valor(1000.0)
                        .cliente(cliente)
                        .build()
        ).isInstanceOf(IllegalStateException.class)
         .hasMessageContaining("Título");
    }

    @Test
    @DisplayName("Builder: lança exceção quando título é vazio")
    void builder_deveLancarExcecaoComTituloVazio() {
        assertThatThrownBy(() ->
                new Proposta.Builder()
                        .titulo("   ")
                        .valor(1000.0)
                        .cliente(cliente)
                        .build()
        ).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("Builder: lança exceção quando valor é zero")
    void builder_deveLancarExcecaoComValorZero() {
        assertThatThrownBy(() ->
                new Proposta.Builder()
                        .titulo("Proposta")
                        .valor(0)
                        .cliente(cliente)
                        .build()
        ).isInstanceOf(IllegalStateException.class)
         .hasMessageContaining("Valor");
    }

    @Test
    @DisplayName("Builder: lança exceção quando valor é negativo")
    void builder_deveLancarExcecaoComValorNegativo() {
        assertThatThrownBy(() ->
                new Proposta.Builder()
                        .titulo("Proposta")
                        .valor(-500.0)
                        .cliente(cliente)
                        .build()
        ).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("Builder: lança exceção quando cliente é nulo")
    void builder_deveLancarExcecaoSemCliente() {
        assertThatThrownBy(() ->
                new Proposta.Builder()
                        .titulo("Proposta")
                        .valor(1000.0)
                        .build()
        ).isInstanceOf(IllegalStateException.class)
         .hasMessageContaining("Cliente");
    }

    // ================================================================
    // 3. Transições de estado — fluxo de aprovação
    // ================================================================

    @Test
    @DisplayName("Estado: RASCUNHO → EM_ANALISE ao chamar avancar()")
    void estado_rascunhoDeveAvancarParaAnalise() {
        Proposta p = propostaRascunho();

        p.avancar();

        assertThat(p.getStatus()).isEqualTo("ANALISE");
    }

    @Test
    @DisplayName("Estado: EM_ANALISE → APROVADA ao chamar avancar()")
    void estado_analiseDeveAvancarParaAprovada() {
        Proposta p = propostaRascunho();
        p.avancar(); // RASCUNHO → ANALISE

        p.avancar(); // ANALISE → APROVADA

        assertThat(p.getStatus()).isEqualTo("APROVADA");
    }

    @Test
    @DisplayName("Estado: EM_ANALISE → REPROVADA ao chamar reprovar()")
    void estado_analiseDevePoderSerReprovada() {
        Proposta p = propostaRascunho();
        p.avancar(); // RASCUNHO → ANALISE

        p.reprovar();

        assertThat(p.getStatus()).isEqualTo("REPROVADA");
    }

    @Test
    @DisplayName("Estado: REPROVADA → RASCUNHO ao chamar reabrir()")
    void estado_reprovadaDeveReabrirParaRascunho() {
        Proposta p = propostaRascunho();
        p.avancar();  // RASCUNHO → ANALISE
        p.reprovar(); // ANALISE  → REPROVADA

        p.reabrir();  // REPROVADA → RASCUNHO

        assertThat(p.getStatus()).isEqualTo("RASCUNHO");
    }

    @Test
    @DisplayName("Estado: APROVADA → RASCUNHO ao chamar reabrir()")
    void estado_aprovadaDeveReabrirParaRascunho() {
        Proposta p = propostaRascunho();
        p.avancar(); // RASCUNHO → ANALISE
        p.avancar(); // ANALISE  → APROVADA

        p.reabrir(); // APROVADA → RASCUNHO

        assertThat(p.getStatus()).isEqualTo("RASCUNHO");
    }

    // ================================================================
    // 4. Transições inválidas
    // ================================================================

    @Test
    @DisplayName("Estado: RASCUNHO não pode ser reprovado")
    void estado_rascunhoNaoPodeSerReprovado() {
        Proposta p = propostaRascunho();

        assertThatThrownBy(p::reprovar)
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("Estado: RASCUNHO não pode ser reaberto")
    void estado_rascunhoNaoPodeSerReaberto() {
        Proposta p = propostaRascunho();

        assertThatThrownBy(p::reabrir)
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("Estado: APROVADA não pode avançar novamente")
    void estado_aprovadaNaoPodeAvancar() {
        Proposta p = propostaRascunho();
        p.avancar(); // → ANALISE
        p.avancar(); // → APROVADA

        assertThatThrownBy(p::avancar)
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("Estado: REPROVADA não pode avançar diretamente")
    void estado_reprovadaNaoPodeAvancar() {
        Proposta p = propostaRascunho();
        p.avancar();  // → ANALISE
        p.reprovar(); // → REPROVADA

        assertThatThrownBy(p::avancar)
                .isInstanceOf(IllegalStateException.class);
    }

    // ================================================================
    // 5. setEstado sincroniza status e etapa
    // ================================================================

    @Test
    @DisplayName("setEstado: sincroniza status e dataAtualizacao")
    void setEstado_deveSincronizarStatusEData() {
        Proposta p = propostaRascunho();

        p.avancar(); // chama setEstado internamente

        assertThat(p.getStatus()).isEqualTo("ANALISE");
        assertThat(p.getDataAtualizacao()).isNotNull();
        assertThat(p.getEtapaAtual()).isNotBlank();
    }

    // ================================================================
    // Helpers
    // ================================================================

    private Proposta propostaRascunho() {
        return new Proposta.Builder()
                .titulo("Proposta Teste")
                .valor(10000.0)
                .cliente(cliente)
                .build();
    }
}
