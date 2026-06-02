package com.esteira.repository;

import com.esteira.model.Cliente;
import com.esteira.model.Proposta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Testes de integração com banco H2 em memória — PropostaRepository.
 *
 * @DataJpaTest sobe apenas a camada JPA (sem controllers nem services).
 * O banco H2 é criado e destruído a cada execução de teste.
 */
@DataJpaTest
@ActiveProfiles("test")
class PropostaRepositoryTest {

    @Autowired private PropostaRepository propostaRepo;
    @Autowired private ClienteRepository  clienteRepo;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        propostaRepo.deleteAll();
        clienteRepo.deleteAll();

        cliente = clienteRepo.save(new Cliente.Builder()
                .nome("Integração Teste")
                .cpfCnpj("000.000.000-01")
                .email("integracao@test.com")
                .build());
    }

    // ================================================================
    // 1. Salvar e buscar por ID
    // ================================================================

    @Test
    @DisplayName("Deve salvar proposta e recuperar pelo ID")
    void deveSalvarEBuscarPorId() {
        Proposta salva = salvarProposta("Proposta A", 1000.0, "RASCUNHO");

        Optional<Proposta> encontrada = propostaRepo.findById(salva.getId());

        assertThat(encontrada).isPresent();
        assertThat(encontrada.get().getTitulo()).isEqualTo("Proposta A");
        assertThat(encontrada.get().getValor()).isEqualTo(1000.0);
    }

    // ================================================================
    // 2. Listagem ordenada por data
    // ================================================================

    @Test
    @DisplayName("Deve listar propostas ordenadas por dataCriacao desc")
    void deveListarOrdenadoPorDataDesc() {
        salvarProposta("Primeira", 1000.0, "RASCUNHO");
        salvarProposta("Segunda",  2000.0, "APROVADA");

        List<Proposta> lista = propostaRepo.findAllByOrderByDataCriacaoDesc();

        assertThat(lista).hasSize(2);
        // A mais recente vem primeiro
        assertThat(lista.get(0).getTitulo()).isEqualTo("Segunda");
    }

    // ================================================================
    // 3. Filtro por status
    // ================================================================

    @Test
    @DisplayName("Deve filtrar propostas por status APROVADA")
    void deveFiltrarPorStatus() {
        salvarProposta("Rascunho 1",  500.0,  "RASCUNHO");
        salvarProposta("Aprovada 1",  1500.0, "APROVADA");
        salvarProposta("Aprovada 2",  3000.0, "APROVADA");
        salvarProposta("Reprovada 1", 800.0,  "REPROVADA");

        List<Proposta> aprovadas = propostaRepo.findByStatusOrderByDataCriacaoDesc("APROVADA");

        assertThat(aprovadas).hasSize(2);
        assertThat(aprovadas).allMatch(p -> p.getStatus().equals("APROVADA"));
    }

    @Test
    @DisplayName("Deve retornar lista vazia para status sem registros")
    void deveRetornarVazioParaStatusSemRegistros() {
        salvarProposta("Rascunho", 1000.0, "RASCUNHO");

        List<Proposta> analise = propostaRepo.findByStatusOrderByDataCriacaoDesc("ANALISE");

        assertThat(analise).isEmpty();
    }

    // ================================================================
    // 4. Verificação de código único (existsByCodigo)
    // ================================================================

    @Test
    @DisplayName("existsByCodigo: retorna true para código existente")
    void deveRetornarTrueParaCodigoExistente() {
        salvarPropostaComCodigo("PROP-20260602-00001");

        assertThat(propostaRepo.existsByCodigo("PROP-20260602-00001")).isTrue();
    }

    @Test
    @DisplayName("existsByCodigo: retorna false para código inexistente")
    void deveRetornarFalseParaCodigoInexistente() {
        assertThat(propostaRepo.existsByCodigo("PROP-99999999-99999")).isFalse();
    }

    // ================================================================
    // 5. Contagem
    // ================================================================

    @Test
    @DisplayName("count: retorna total correto após inserções")
    void deveContarPropostasCorretamente() {
        assertThat(propostaRepo.count()).isZero();

        salvarProposta("A", 100.0, "RASCUNHO");
        salvarProposta("B", 200.0, "RASCUNHO");

        assertThat(propostaRepo.count()).isEqualTo(2);
    }

    // ================================================================
    // 6. Deleção
    // ================================================================

    @Test
    @DisplayName("Deve deletar proposta pelo ID")
    void deveDeletarPorId() {
        Proposta salva = salvarProposta("Para deletar", 500.0, "RASCUNHO");

        propostaRepo.deleteById(salva.getId());

        assertThat(propostaRepo.findById(salva.getId())).isEmpty();
        assertThat(propostaRepo.count()).isZero();
    }

    // ================================================================
    // Helpers
    // ================================================================

    private Proposta salvarProposta(String titulo, double valor, String status) {
        Proposta p = new Proposta.Builder()
                .titulo(titulo)
                .valor(valor)
                .cliente(cliente)
                .status(status)
                .build();
                String nano = String.valueOf(System.nanoTime());
                p.setCodigo("PT-" + nano.substring(Math.max(0, nano.length() - 17)));
        return propostaRepo.save(p);
    }

    private Proposta salvarPropostaComCodigo(String codigo) {
        Proposta p = new Proposta.Builder()
                .titulo("Proposta " + codigo)
                .valor(1000.0)
                .cliente(cliente)
                .build();
        p.setCodigo(codigo);
        return propostaRepo.save(p);
    }
}
