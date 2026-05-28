package com.esteira.state;
import com.esteira.model.Proposta;
public class EstadoAprovada implements EstadoProposta {
    @Override public void avancar(Proposta p)  { throw new IllegalStateException("Proposta já aprovada. Nenhuma transição disponível."); }
    @Override public void reprovar(Proposta p) { throw new IllegalStateException("Não é possível reprovar uma proposta já aprovada."); }
    @Override public void reabrir(Proposta p)  { throw new IllegalStateException("Não é possível reabrir uma proposta aprovada."); }
    @Override public String getNome()  { return "APROVADA"; }
    @Override public String getEtapa() { return "CONCLUIDA"; }
}
