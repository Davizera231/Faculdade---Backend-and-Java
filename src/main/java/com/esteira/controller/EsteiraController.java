package com.esteira.controller;
import com.esteira.model.Proposta;
import com.esteira.service.EsteiraService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController @RequestMapping("/esteira")
public class EsteiraController {
    private final EsteiraService s;
    public EsteiraController(EsteiraService s) { this.s=s; }
    @PostMapping("/{propostaId}/{acao}")
    public ResponseEntity<Map<String,Object>> executar(@PathVariable int propostaId, @PathVariable String acao, @RequestBody(required=false) Map<String,String> body) {
        String obs = (body!=null) ? body.get("observacao") : null;
        Proposta p = s.executarAcao(propostaId, acao.toUpperCase(), obs);
        return ResponseEntity.ok(Map.of("mensagem","Ação executada com sucesso.","propostaId",p.getId(),"codigo",p.getCodigo(),"status",p.getStatus(),"etapa",p.getEtapaAtual()));
    }
    @GetMapping("/{propostaId}/acoes")
    public ResponseEntity<Map<String,Object>> acoes(@PathVariable int propostaId) {
        return ResponseEntity.ok(Map.of("propostaId",propostaId,"acoesDisponiveis",s.acoesDisponiveisPara(propostaId)));
    }
}
