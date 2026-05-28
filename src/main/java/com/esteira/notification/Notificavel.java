package com.esteira.notification;
import com.esteira.model.Proposta;
/** Componente base — "Notifica_abstract" no diagrama UML. */
public abstract class Notificavel {
    public abstract void enviar(Proposta proposta, String acao);
}
