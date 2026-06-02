package com.esteira.service;

import com.esteira.model.Cliente;
import com.esteira.model.Proposta;
import com.esteira.repository.PropostaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários — PropostaService.
 *
 * Cobre:
 *   - Geração automática de código (formato PROP-YYYYMMDD-NNNNN)
 *   - Unicidade do código gerado via loop do-while
 *   - Salvar nova proposta vs proposta existente (update)
 *   - Busca por ID com lançamento de exceção para ID inexistente
 *   - Listagem e deleção
 */
@ExtendWith(MockitoExtension.class)
class PropostaServiceTest {

    @Mock
    private PropostaRepository repo;

    @InjectMocks
    private PropostaService service;

    private Cliente cliente;
    private Proposta novaProposta;

    @BeforeEach
    void setUp() {
        cliente = new Cliente.Builder()
                .id(1)
                .nome("Maria Souza")
                .cpfCnpj("987.654.321-00")
                .email("maria@empresa.com")
                .build();

        novaProposta = new Proposta.Builder()
                .titulo("Nova Proposta")
                .valor(12000.0)
                .cliente(cliente)
                .build();
        // id=0 → nova proposta (sem código ainda)
    }

    // ================================================================
    // 1. Geração de código
    // ================================================================


    @Test
    @DisplayName("salvar: código gerado começa com 'PROP-'")
    void salvar_codigoDeveComecaComPROP() {
        when(repo.count()).thenReturn(5L);
        when(repo.existsByCodigo(anyString())).thenReturn(false);
        when(repo.save(any(Proposta.class))).thenAnswer(inv -> inv.getArgument(0));

        Proposta salva = service.salvar(novaProposta);

        assertThat(salva.getCodigo()).startsWith("PROP-");
    }

    @Test
    @DisplayName("salvar: gera código alternativo quando primeiro candidato já existe (unicidade)")
    void salvar_deveGerarCodigoAlternativoSeJaExistir() {
        when(repo.count()).thenReturn(0L);
        // Primeiro candidato (seq=1) já existe, segundo (seq=2) está livre
        when(repo.existsByCodigo(anyString()))
                .thenReturn(true)   // 1ª tentativa — colisão
                .thenReturn(false); // 2ª tentativa — livre
        when(repo.save(any(Proposta.class))).thenAnswer(inv -> inv.getArgument(0));

        Proposta salva = service.salvar(novaProposta);

        // existsByCodigo deve ter sido chamado 2 vezes (loop do-while)
        verify(repo, times(2)).existsByCodigo(anyString());
        assertThat(salva.getCodigo()).matches("PROP-\\d{8}-\\d{5}");
    }

    // ================================================================
    // 2. Salvar proposta existente (update) não gera novo código
    // ================================================================

    @Test
    @DisplayName("salvar: não regenera código quando proposta já possui ID (update)")
    void salvar_naoDeveRegerarCodigoEmUpdate() {
        Proposta existente = new Proposta.Builder()
                .titulo("Proposta Existente")
                .valor(8000.0)
                .cliente(cliente)
                .codigo("PROP-20260601-00001")
                .build();
        existente.setId(42); // id > 0 → update

        when(repo.save(any(Proposta.class))).thenAnswer(inv -> inv.getArgument(0));

        Proposta salva = service.salvar(existente);

        // Não deve ter consultado repo.count() nem existsByCodigo
        verify(repo, never()).count();
        verify(repo, never()).existsByCodigo(anyString());
        assertThat(salva.getCodigo()).isEqualTo("PROP-20260601-00001");
    }

    // ================================================================
    // 3. Busca por ID
    // ================================================================

    @Test
    @DisplayName("buscarPorId: retorna proposta quando ID existe")
    void buscarPorId_deveRetornarPropostaExistente() {
        Proposta esperada = new Proposta.Builder()
                .titulo("Proposta X")
                .valor(3000.0)
                .cliente(cliente)
                .codigo("PROP-20260602-00001")
                .build();
        esperada.setId(10);

        when(repo.findById(10)).thenReturn(Optional.of(esperada));

        Proposta resultado = service.buscarPorId(10);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getCodigo()).isEqualTo("PROP-20260602-00001");
    }

    @Test
    @DisplayName("buscarPorId: lança IllegalArgumentException para ID inexistente")
    void buscarPorId_deveLancarExcecaoParaIdInexistente() {
        when(repo.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(999))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("999");
    }

    // ================================================================
    // 4. Listar propostas
    // ================================================================

    @Test
    @DisplayName("listarTodas: delega para repository e retorna lista")
    void listarTodas_deveRetornarListaDoRepositorio() {
        Proposta p1 = new Proposta.Builder().titulo("A").valor(1000).cliente(cliente).build();
        Proposta p2 = new Proposta.Builder().titulo("B").valor(2000).cliente(cliente).build();
        when(repo.findAllByOrderByDataCriacaoDesc()).thenReturn(List.of(p1, p2));

        List<Proposta> lista = service.listarTodas();

        assertThat(lista).hasSize(2);
        verify(repo, times(1)).findAllByOrderByDataCriacaoDesc();
    }

    @Test
    @DisplayName("listarPorStatus: filtra pelo status informado")
    void listarPorStatus_deveFiltrarPorStatus() {
        Proposta aprovada = new Proposta.Builder()
                .titulo("Aprovada")
                .valor(5000)
                .cliente(cliente)
                .status("APROVADA")
                .build();
        when(repo.findByStatusOrderByDataCriacaoDesc("APROVADA")).thenReturn(List.of(aprovada));

        List<Proposta> lista = service.listarPorStatus("APROVADA");

        assertThat(lista).hasSize(1);
        assertThat(lista.get(0).getStatus()).isEqualTo("APROVADA");
    }

    // ================================================================
    // 5. Deletar
    // ================================================================

    @Test
    @DisplayName("deletar: chama deleteById quando ID existe")
    void deletar_deveChamarDeleteByIdQuandoPropostaExiste() {
        Proposta existente = new Proposta.Builder()
                .titulo("Para deletar")
                .valor(1000)
                .cliente(cliente)
                .build();
        existente.setId(7);
        when(repo.findById(7)).thenReturn(Optional.of(existente));

        service.deletar(7);

        verify(repo, times(1)).deleteById(7);
    }

    @Test
    @DisplayName("deletar: lança exceção quando ID não existe, sem chamar deleteById")
    void deletar_deveLancarExcecaoParaIdInexistente() {
        when(repo.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deletar(99))
                .isInstanceOf(IllegalArgumentException.class);

        verify(repo, never()).deleteById(anyInt());
    }

    // ================================================================
    // 6. Captura de argumento salvo no repositório
    // ================================================================

    @Test
    @DisplayName("salvar: o objeto persistido no repo possui código gerado")
    void salvar_objetoPersistidoDeveConterCodigo() {
        when(repo.count()).thenReturn(0L);
        when(repo.existsByCodigo(anyString())).thenReturn(false);
        when(repo.save(any(Proposta.class))).thenAnswer(inv -> inv.getArgument(0));

        service.salvar(novaProposta);

        ArgumentCaptor<Proposta> captor = ArgumentCaptor.forClass(Proposta.class);
        verify(repo).save(captor.capture());
        assertThat(captor.getValue().getCodigo()).isNotBlank();
    }
}
