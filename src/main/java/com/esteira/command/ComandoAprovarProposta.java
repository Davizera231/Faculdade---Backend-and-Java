package com.esteira.command;
import com.esteira.model.Proposta;
public class ComandoAprovarProposta implements ComandoEsteira {
    @Override public void executar(Proposta p) { p.avancar(); }
    @Override public String getDescricao() { return "Proposta aprovada com sucesso."; }
    @Override public String getAcao()      { return "APROVAR"; }
}
