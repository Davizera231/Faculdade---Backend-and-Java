package com.esteira.service;
import com.esteira.model.Cliente;
import com.esteira.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service
public class ClienteService {
    private final ClienteRepository repo;
    public ClienteService(ClienteRepository repo) { this.repo = repo; }
    public List<Cliente> listarTodos() { return repo.findAll(); }
    public Cliente buscarPorId(int id) { return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: id=" + id)); }
    @Transactional public Cliente salvar(Cliente c) {
        if (c.getId()==0 && repo.existsByCpfCnpj(c.getCpfCnpj())) throw new IllegalStateException("Já existe um cliente com este CPF/CNPJ.");
        return repo.save(c);
    }
    @Transactional public void deletar(int id) { buscarPorId(id); repo.deleteById(id); }
}
