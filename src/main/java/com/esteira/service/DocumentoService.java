package com.esteira.service;
import com.esteira.model.*;
import com.esteira.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service
public class DocumentoService {
    private final DocumentoRepository docRepo;
    private final PropostaRepository  propRepo;
    public DocumentoService(DocumentoRepository d, PropostaRepository p) { docRepo=d; propRepo=p; }
    public List<Documento> listarPorProposta(int id) { return docRepo.findByPropostaId(id); }
    @Transactional public Documento vincular(Documento doc, int propostaId) {
        Proposta p = propRepo.findById(propostaId).orElseThrow(() -> new IllegalArgumentException("Proposta não encontrada: id=" + propostaId));
        doc.setProposta(p);
        return docRepo.save(doc);
    }
    @Transactional public void deletar(int id) { if (!docRepo.existsById(id)) throw new IllegalArgumentException("Documento não encontrado: id=" + id); docRepo.deleteById(id); }
}
