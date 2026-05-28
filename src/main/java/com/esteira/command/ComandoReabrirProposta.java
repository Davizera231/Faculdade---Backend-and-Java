package com.esteira.command;
import com.esteira.model.Proposta;
public class ComandoReabrirProposta implements ComandoEsteira {
    @Override public void executar(Proposta p) { p.reabrir(); }
    @Override public String getDescricao() { return "Proposta reaberta e retornada ao rascunho."; }
    @Override public String getAcao()      { return "REABRIR"; }
}
