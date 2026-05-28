package com.esteira.command;
import com.esteira.model.Proposta;
public class ComandoEnviarParaAnalise implements ComandoEsteira {
    @Override public void executar(Proposta p) { p.avancar(); }
    @Override public String getDescricao() { return "Proposta enviada para análise com sucesso."; }
    @Override public String getAcao()      { return "ENVIAR_ANALISE"; }
}
