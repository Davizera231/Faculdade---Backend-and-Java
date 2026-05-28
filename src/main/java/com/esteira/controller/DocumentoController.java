package com.esteira.controller;

import com.esteira.model.Documento;
import com.esteira.service.DocumentoService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/documentos")
public class DocumentoController {

    private final DocumentoService s;

    public DocumentoController(DocumentoService s) { this.s = s; }

    /** Lista metadados dos documentos de uma proposta (sem os bytes) */
    @GetMapping("/proposta/{id}")
    public List<Documento> listar(@PathVariable int id) {
        return s.listarPorProposta(id);
    }

    /**
     * Upload de PDF vinculado a uma proposta.
     * Content-Type: multipart/form-data
     *   - arquivo   (obrigatório) — o arquivo PDF
     *   - descricao (opcional)   — observação sobre o documento
     */
    @PostMapping(value = "/proposta/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Documento> upload(
            @PathVariable int id,
            @RequestPart("arquivo") MultipartFile arquivo,
            @RequestPart(value = "descricao", required = false) String descricao) throws IOException {

        Documento doc = s.upload(id, arquivo, descricao);
        return ResponseEntity.status(HttpStatus.CREATED).body(doc);
    }

    /** Download do PDF pelo id do documento */
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> download(@PathVariable int id) {
        Documento doc = s.buscarParaDownload(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + doc.getNome() + "\"")
                .body(doc.getConteudo());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable int id) {
        s.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
