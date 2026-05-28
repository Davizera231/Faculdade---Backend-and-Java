package com.esteira.service;

import com.esteira.command.ComandoEsteira;
import com.esteira.factory.FabricaComandoEsteira;
import com.esteira.model.Proposta;
import com.esteira.notification.*;
import com.esteira.repository.PropostaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EsteiraService {

    private static final Logger log = LoggerFactory.getLogger(EsteiraService.class);

    private final PropostaRepository repo;
    private final NotificacaoEmail emailBase; // base sempre obrigatória

    public EsteiraService(PropostaRepository repo, NotificacaoEmail emailBase) {
        this.repo = repo;
        this.emailBase = emailBase;
    }

    @Transactional
    public Proposta executarAcao(int propostaId, String acao, String observacao, List<String> canais) {
        Proposta p = repo.findById(propostaId)
                .orElseThrow(() -> new IllegalArgumentException("Proposta não encontrada: id=" + propostaId));

        if (observacao != null && !observacao.isBlank()) p.setObservacoes(observacao);

        ComandoEsteira cmd = FabricaComandoEsteira.criar(acao);
        cmd.executar(p);
        Proposta salva = repo.save(p);

        try {
            Notificavel chain = montarCadeia(canais);
            chain.enviar(salva, acao);
        } catch (Exception e) {
            log.error("Erro nas notificações: {}", e.getMessage());
        }

        log.info("Esteira | {} | {} → {}", salva.getCodigo(), acao, salva.getStatus());
        return salva;
    }

    public String[] acoesDisponiveisPara(int propostaId) {
        Proposta p = repo.findById(propostaId)
                .orElseThrow(() -> new IllegalArgumentException("Proposta não encontrada: id=" + propostaId));
        return FabricaComandoEsteira.acoesDisponiveisPara(p.getStatus());
    }

    /**
     * Monta a cadeia Decorator dinamicamente.
     * E-mail é sempre a base (obrigatório).
     * SMS, WHATSAPP e FACEBOOK são adicionados conforme a lista de canais.
     */
    private Notificavel montarCadeia(List<String> canais) {
        Notificavel chain = emailBase;

        if (canais == null) return chain;

        List<String> upper = canais.stream().map(String::toUpperCase).toList();

        if (upper.contains("SMS"))      chain = new NotificacaoSMS(chain);
        if (upper.contains("WHATSAPP")) chain = new NotificacaoWhatsApp(chain);
        if (upper.contains("FACEBOOK")) chain = new NotificacaoFacebook(chain);

        return chain;
    }
}
