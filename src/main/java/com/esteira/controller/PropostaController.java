package com.esteira.controller;
import com.esteira.model.Proposta;
import com.esteira.service.PropostaService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/propostas")
public class PropostaController {
    private final PropostaService s;
    public PropostaController(PropostaService s) { this.s=s; }
    @GetMapping public List<Proposta> listar(@RequestParam(required=false) String status) { return (status!=null&&!status.isBlank()) ? s.listarPorStatus(status.toUpperCase()) : s.listarTodas(); }
    @GetMapping("/{id}") public Proposta buscar(@PathVariable int id) { return s.buscarPorId(id); }
    @PostMapping public ResponseEntity<Proposta> criar(@Valid @RequestBody Proposta p) { return ResponseEntity.status(HttpStatus.CREATED).body(s.salvar(p)); }
    @PutMapping("/{id}") public Proposta atualizar(@PathVariable int id, @Valid @RequestBody Proposta p) {
        Proposta ex = s.buscarPorId(id);
        if (!ex.getStatus().equals("RASCUNHO")) throw new IllegalStateException("Edição permitida apenas em RASCUNHO.");
        p.setId(id); p.setDataCriacao(ex.getDataCriacao()); return s.salvar(p);
    }
    @DeleteMapping("/{id}") public ResponseEntity<Void> deletar(@PathVariable int id) { s.deletar(id); return ResponseEntity.noContent().build(); }
}
