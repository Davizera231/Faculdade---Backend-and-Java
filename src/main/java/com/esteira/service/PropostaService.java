package com.esteira.service;

import com.esteira.model.Proposta;
import com.esteira.repository.PropostaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class PropostaService {

    private final PropostaRepository repo;

    public PropostaService(PropostaRepository repo) { this.repo = repo; }

    public List<Proposta> listarTodas() { return repo.findAllByOrderByDataCriacaoDesc(); }

    public List<Proposta> listarPorStatus(String status) { return repo.findByStatusOrderByDataCriacaoDesc(status); }

    public Proposta buscarPorId(int id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Proposta não encontrada: id=" + id));
    }

    @Transactional
    public Proposta salvar(Proposta p) {
        if (p.getId() == 0) {
            // Nova proposta — gera código automaticamente
            p.setCodigo(gerarCodigo());
        }
        return repo.save(p);
    }

    @Transactional
    public void deletar(int id) { buscarPorId(id); repo.deleteById(id); }

    /**
     * Gera código único no formato: PROP-YYYYMMDD-XXXXX
     * Ex: PROP-20260528-00042
     */
    private String gerarCodigo() {
        String data = new SimpleDateFormat("yyyyMMdd").format(new Date());
        long seq = repo.count() + 1;
        String candidato;
        do {
            candidato = String.format("PROP-%s-%05d", data, seq++);
        } while (repo.existsByCodigo(candidato));
        return candidato;
    }
}
