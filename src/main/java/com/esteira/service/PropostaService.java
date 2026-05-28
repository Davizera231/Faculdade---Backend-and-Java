package com.esteira.service;
import com.esteira.model.Proposta;
import com.esteira.repository.PropostaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service
public class PropostaService {
    private final PropostaRepository repo;
    public PropostaService(PropostaRepository repo) { this.repo = repo; }
    public List<Proposta> listarTodas() { return repo.findAllByOrderByDataCriacaoDesc(); }
    public List<Proposta> listarPorStatus(String status) { return repo.findByStatusOrderByDataCriacaoDesc(status); }
    public Proposta buscarPorId(int id) { return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Proposta não encontrada: id=" + id)); }
    @Transactional public Proposta salvar(Proposta p) {
        if (p.getId()==0 && repo.existsByCodigo(p.getCodigo())) throw new IllegalStateException("Já existe uma proposta com este código.");
        return repo.save(p);
    }
    @Transactional public void deletar(int id) { buscarPorId(id); repo.deleteById(id); }
}
