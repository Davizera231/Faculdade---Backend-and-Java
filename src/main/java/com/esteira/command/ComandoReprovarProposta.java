package com.esteira.command;
import com.esteira.model.Proposta;
public class ComandoReprovarProposta implements ComandoEsteira {
    @Override public void executar(Proposta p) { p.reprovar(); }
    @Override public String getDescricao() { return "Proposta reprovada."; }
    @Override public String getAcao()      { return "REPROVAR"; }
}
