package com.esteira.command;
import com.esteira.model.Proposta;
public interface ComandoEsteira {
    void executar(Proposta proposta);
    String getDescricao();
    String getAcao();
}
