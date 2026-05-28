package com.esteira.notification;
import com.esteira.model.Proposta;
/** Decorator abstrato — "Notifica_decorator" no diagrama UML. */
public abstract class NotificacaoDecorator extends Notificavel {
    protected final Notificavel notificavel;
    protected NotificacaoDecorator(Notificavel n) { this.notificavel = n; }
    @Override public void enviar(Proposta p, String acao) { notificavel.enviar(p, acao); }
}
