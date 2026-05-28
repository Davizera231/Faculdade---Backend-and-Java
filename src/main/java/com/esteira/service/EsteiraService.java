package com.esteira.service;
import com.esteira.command.ComandoEsteira;
import com.esteira.factory.FabricaComandoEsteira;
import com.esteira.model.Proposta;
import com.esteira.notification.Notificavel;
import com.esteira.repository.PropostaRepository;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class EsteiraService {
    private static final Logger log = LoggerFactory.getLogger(EsteiraService.class);
    private final PropostaRepository repo;
    private final Notificavel notificacaoChain;
    public EsteiraService(PropostaRepository repo, Notificavel notificacaoChain) { this.repo=repo; this.notificacaoChain=notificacaoChain; }
    @Transactional
    public Proposta executarAcao(int propostaId, String acao, String observacao) {
        Proposta p = repo.findById(propostaId).orElseThrow(() -> new IllegalArgumentException("Proposta não encontrada: id=" + propostaId));
        if (observacao!=null&&!observacao.isBlank()) p.setObservacoes(observacao);
        ComandoEsteira cmd = FabricaComandoEsteira.criar(acao);
        cmd.executar(p);
        Proposta salva = repo.save(p);
        try { notificacaoChain.enviar(salva, acao); } catch (Exception e) { log.error("Erro nas notificações: {}", e.getMessage()); }
        log.info("Esteira | {} | {} → {}", salva.getCodigo(), acao, salva.getStatus());
        return salva;
    }
    public String[] acoesDisponiveisPara(int propostaId) {
        Proposta p = repo.findById(propostaId).orElseThrow(() -> new IllegalArgumentException("Proposta não encontrada: id=" + propostaId));
        return FabricaComandoEsteira.acoesDisponiveisPara(p.getStatus());
    }
}
