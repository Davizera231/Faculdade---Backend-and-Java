package com.esteira.state;
import com.esteira.model.Proposta;
public class EstadoAnalise implements EstadoProposta {
    @Override public void avancar(Proposta p)  { p.setEstado(new EstadoAprovada()); }
    @Override public void reprovar(Proposta p) { p.setEstado(new EstadoReprovada()); }
    @Override public void reabrir(Proposta p)  { throw new IllegalStateException("Não é possível reabrir uma proposta em análise."); }
    @Override public String getNome()  { return "ANALISE"; }
    @Override public String getEtapa() { return "ANALISE"; }
}
