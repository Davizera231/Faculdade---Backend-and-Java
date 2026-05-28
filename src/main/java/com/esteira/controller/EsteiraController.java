package com.esteira.controller;

import com.esteira.model.Proposta;
import com.esteira.service.EsteiraService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/esteira")
public class EsteiraController {

    private final EsteiraService s;

    public EsteiraController(EsteiraService s) { this.s = s; }

    /**
     * Body esperado:
     * {
     *   "observacao": "...",          // opcional
     *   "canais": ["SMS","WHATSAPP"]  // opcional — EMAIL sempre incluído pelo service
     * }
     */
    @PostMapping("/{propostaId}/{acao}")
    public ResponseEntity<Map<String, Object>> executar(
            @PathVariable int propostaId,
            @PathVariable String acao,
            @RequestBody(required = false) Map<String, Object> body) {

        String obs = body != null ? (String) body.get("observacao") : null;

        @SuppressWarnings("unchecked")
        List<String> canais = body != null ? (List<String>) body.get("canais") : List.of();

        Proposta p = s.executarAcao(propostaId, acao.toUpperCase(), obs, canais);

        return ResponseEntity.ok(Map.of(
                "mensagem",   "Ação executada com sucesso.",
                "propostaId", p.getId(),
                "codigo",     p.getCodigo(),
                "status",     p.getStatus(),
                "etapa",      p.getEtapaAtual()
        ));
    }

    @GetMapping("/{propostaId}/acoes")
    public ResponseEntity<Map<String, Object>> acoes(@PathVariable int propostaId) {
        return ResponseEntity.ok(Map.of(
                "propostaId",       propostaId,
                "acoesDisponiveis", s.acoesDisponiveisPara(propostaId)
        ));
    }
}
