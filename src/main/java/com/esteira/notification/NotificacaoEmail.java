package com.esteira.notification;
import com.esteira.model.Proposta;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
/** Componente concreto — e-mail real via Spring Mail. "Notifica_Email" no UML. */
public class NotificacaoEmail extends Notificavel {
    private static final Logger log = LoggerFactory.getLogger(NotificacaoEmail.class);
    private final JavaMailSender mailSender;
    private final String from;
    public NotificacaoEmail(JavaMailSender mailSender, String from) { this.mailSender=mailSender; this.from=from; }
    @Override public void enviar(Proposta p, String acao) {
        try {
            String dest = p.getCliente().getEmail();
            if (dest==null||dest.isBlank()) { log.warn("[EMAIL] Cliente sem e-mail — proposta {}", p.getCodigo()); return; }
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(from); msg.setTo(dest);
            msg.setSubject("Atualização na Proposta: " + p.getCodigo());
            msg.setText(String.format("Olá, %s!\n\nProposta '%s' (%s) atualizada.\nAção: %s\nStatus: %s\nEtapa: %s\n%sAtenciosamente,\nEsteira de Propostas",
                p.getCliente().getNome(), p.getTitulo(), p.getCodigo(), acao, p.getStatus(), p.getEtapaAtual(),
                (p.getObservacoes()!=null&&!p.getObservacoes().isBlank()) ? "Obs: "+p.getObservacoes()+"\n\n" : "\n"));
            mailSender.send(msg);
            log.info("[EMAIL] ✔ {} | {} | {}", dest, p.getCodigo(), acao);
        } catch (Exception e) { log.error("[EMAIL] ✖ Falha na proposta {}: {}", p.getCodigo(), e.getMessage()); }
    }
}
