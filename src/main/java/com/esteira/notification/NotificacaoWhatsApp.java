package com.esteira.notification;
import com.esteira.model.Proposta;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
public class NotificacaoWhatsApp extends NotificacaoDecorator {
    private static final Logger log = LoggerFactory.getLogger(NotificacaoWhatsApp.class);
    public NotificacaoWhatsApp(Notificavel n) { super(n); }
    @Override public void enviar(Proposta p, String acao) {
        super.enviar(p, acao);
        log.info("[WHATSAPP] {} | {} | {} | {}", p.getCliente().getNome(), p.getCodigo(), p.getStatus(), acao);
    }
}
