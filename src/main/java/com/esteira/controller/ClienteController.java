package com.esteira.controller;
import com.esteira.model.Cliente;
import com.esteira.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/clientes")
public class ClienteController {
    private final ClienteService s;
    public ClienteController(ClienteService s) { this.s=s; }
    @GetMapping public List<Cliente> listar() { return s.listarTodos(); }
    @GetMapping("/{id}") public Cliente buscar(@PathVariable int id) { return s.buscarPorId(id); }
    @PostMapping public ResponseEntity<Cliente> criar(@Valid @RequestBody Cliente c) { return ResponseEntity.status(HttpStatus.CREATED).body(s.salvar(c)); }
    @PutMapping("/{id}") public Cliente atualizar(@PathVariable int id, @Valid @RequestBody Cliente c) { c.setId(id); return s.salvar(c); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> deletar(@PathVariable int id) { s.deletar(id); return ResponseEntity.noContent().build(); }
}
