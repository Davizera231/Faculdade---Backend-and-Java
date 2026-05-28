package com.esteira.controller;
import com.esteira.model.Documento;
import com.esteira.service.DocumentoService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/documentos")
public class DocumentoController {
    private final DocumentoService s;
    public DocumentoController(DocumentoService s) { this.s=s; }
    @GetMapping("/proposta/{id}") public List<Documento> listar(@PathVariable int id) { return s.listarPorProposta(id); }
    @PostMapping("/proposta/{id}") public ResponseEntity<Documento> vincular(@PathVariable int id, @Valid @RequestBody Documento d) { return ResponseEntity.status(HttpStatus.CREATED).body(s.vincular(d, id)); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> deletar(@PathVariable int id) { s.deletar(id); return ResponseEntity.noContent().build(); }
}
