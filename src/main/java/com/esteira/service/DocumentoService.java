package com.esteira.service;

import com.esteira.model.Documento;
import com.esteira.model.Proposta;
import com.esteira.repository.DocumentoRepository;
import com.esteira.repository.PropostaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class DocumentoService {

    // Magic bytes do PDF: %PDF (0x25 0x50 0x44 0x46)
    private static final byte[] PDF_MAGIC = { 0x25, 0x50, 0x44, 0x46 };

    private final DocumentoRepository docRepo;
    private final PropostaRepository  propRepo;

    public DocumentoService(DocumentoRepository d, PropostaRepository p) {
        docRepo = d;
        propRepo = p;
    }

    public List<Documento> listarPorProposta(int id) {
        return docRepo.findByPropostaId(id);
    }

    @Transactional
    public Documento upload(int propostaId, MultipartFile arquivo, String descricao) throws IOException {
        validarPDF(arquivo);

        Proposta p = propRepo.findById(propostaId)
                .orElseThrow(() -> new IllegalArgumentException("Proposta não encontrada: id=" + propostaId));

        Documento doc = new Documento();
        doc.setNome(arquivo.getOriginalFilename());
        doc.setTipo("application/pdf");
        doc.setDescricao(descricao);
        doc.setConteudo(arquivo.getBytes());
        doc.setProposta(p);

        return docRepo.save(doc);
    }

    public Documento buscarParaDownload(int id) {
        return docRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Documento não encontrado: id=" + id));
    }

    @Transactional
    public void deletar(int id) {
        if (!docRepo.existsById(id))
            throw new IllegalArgumentException("Documento não encontrado: id=" + id);
        docRepo.deleteById(id);
    }

    // ---------------------------------------------------------------
    // Validação PDF: Content-Type + magic bytes
    // ---------------------------------------------------------------
    private void validarPDF(MultipartFile arquivo) throws IOException {
        if (arquivo == null || arquivo.isEmpty())
            throw new IllegalArgumentException("Nenhum arquivo enviado.");

        String contentType = arquivo.getContentType();
        if (contentType == null || !contentType.equalsIgnoreCase("application/pdf"))
            throw new IllegalArgumentException(
                "Apenas arquivos PDF são aceitos. Tipo recebido: " + contentType);

        byte[] primeiros4 = Arrays.copyOf(arquivo.getBytes(), 4);
        if (!Arrays.equals(primeiros4, PDF_MAGIC))
            throw new IllegalArgumentException(
                "O arquivo não é um PDF válido (assinatura de arquivo inválida).");
    }
}
