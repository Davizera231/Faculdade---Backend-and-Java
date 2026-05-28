package com.esteira.notification;
import com.esteira.model.Proposta;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
public class NotificacaoFacebook extends NotificacaoDecorator {
    private static final Logger log = LoggerFactory.getLogger(NotificacaoFacebook.class);
    public NotificacaoFacebook(Notificavel n) { super(n); }
    @Override public void enviar(Proposta p, String acao) {
        super.enviar(p, acao);
        log.info("[FACEBOOK] {} | {} | {} | {}", p.getCliente().getNome(), p.getCodigo(), p.getStatus(), acao);
    }
}
