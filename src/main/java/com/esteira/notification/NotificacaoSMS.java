package com.esteira.notification;
import com.esteira.model.Proposta;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
public class NotificacaoSMS extends NotificacaoDecorator {
    private static final Logger log = LoggerFactory.getLogger(NotificacaoSMS.class);
    public NotificacaoSMS(Notificavel n) { super(n); }
    @Override public void enviar(Proposta p, String acao) {
        super.enviar(p, acao);
        log.info("[SMS] {} | {} | {} | {}", p.getCliente().getTelefone()!=null?p.getCliente().getTelefone():"N/A", p.getCodigo(), p.getStatus(), acao);
    }
}
