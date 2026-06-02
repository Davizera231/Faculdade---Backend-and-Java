package com.esteira.repository;

import com.esteira.model.Cliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Testes de integração com banco H2 em memória — ClienteRepository.
 */
@DataJpaTest
@ActiveProfiles("test")
class ClienteRepositoryTest {

    @Autowired private ClienteRepository repo;

    @BeforeEach
    void setUp() {
        repo.deleteAll();
    }

    // ================================================================
    // 1. Salvar e buscar
    // ================================================================

    @Test
    @DisplayName("Deve salvar cliente e recuperar pelo ID")
    void deveSalvarEBuscarPorId() {
        Cliente salvo = repo.save(new Cliente.Builder()
                .nome("Ana Paula")
                .cpfCnpj("111.222.333-44")
                .email("ana@empresa.com")
                .build());

        Optional<Cliente> encontrado = repo.findById(salvo.getId());

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNome()).isEqualTo("Ana Paula");
        assertThat(encontrado.get().getEmail()).isEqualTo("ana@empresa.com");
    }

    // ================================================================
    // 2. existsByCpfCnpj
    // ================================================================

    @Test
    @DisplayName("existsByCpfCnpj: retorna true para CPF/CNPJ já cadastrado")
    void deveRetornarTrueParaCpfCnpjExistente() {
        repo.save(new Cliente.Builder()
                .nome("Carlos Lima")
                .cpfCnpj("999.888.777-66")
                .email("carlos@empresa.com")
                .build());

        assertThat(repo.existsByCpfCnpj("999.888.777-66")).isTrue();
    }

    @Test
    @DisplayName("existsByCpfCnpj: retorna false para CPF/CNPJ não cadastrado")
    void deveRetornarFalseParaCpfCnpjInexistente() {
        assertThat(repo.existsByCpfCnpj("000.000.000-00")).isFalse();
    }

    // ================================================================
    // 3. Unicidade de CPF/CNPJ
    // ================================================================

    @Test
    @DisplayName("Não deve permitir dois clientes com o mesmo CPF/CNPJ")
    void naoDevePermitirCpfCnpjDuplicado() {
        repo.save(new Cliente.Builder()
                .nome("Maria")
                .cpfCnpj("123.456.789-00")
                .email("maria@empresa.com")
                .build());

        assertThatThrownBy(() ->
                repo.saveAndFlush(new Cliente.Builder()
                        .nome("João")
                        .cpfCnpj("123.456.789-00")
                        .email("joao@empresa.com")
                        .build())
        ).isInstanceOf(Exception.class); // ConstraintViolationException / DataIntegrityViolationException
    }

    // ================================================================
    // 4. Deleção
    // ================================================================

    @Test
    @DisplayName("Deve deletar cliente pelo ID")
    void deveDeletarPorId() {
        Cliente salvo = repo.save(new Cliente.Builder()
                .nome("Para Deletar")
                .cpfCnpj("555.444.333-22")
                .email("deletar@empresa.com")
                .build());

        repo.deleteById(salvo.getId());

        assertThat(repo.findById(salvo.getId())).isEmpty();
    }

    // ================================================================
    // 5. Listagem
    // ================================================================

    @Test
    @DisplayName("Deve listar todos os clientes cadastrados")
    void deveListarTodos() {
        repo.save(new Cliente.Builder().nome("A").cpfCnpj("001").email("a@a.com").build());
        repo.save(new Cliente.Builder().nome("B").cpfCnpj("002").email("b@b.com").build());
        repo.save(new Cliente.Builder().nome("C").cpfCnpj("003").email("c@c.com").build());

        assertThat(repo.findAll()).hasSize(3);
    }
}
