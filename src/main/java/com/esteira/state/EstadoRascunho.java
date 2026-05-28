package com.esteira.state;
import com.esteira.model.Proposta;
public class EstadoRascunho implements EstadoProposta {
    @Override public void avancar(Proposta p)  { p.setEstado(new EstadoAnalise()); }
    @Override public void reprovar(Proposta p) { throw new IllegalStateException("Não é possível reprovar uma proposta em rascunho."); }
    @Override public void reabrir(Proposta p)  { throw new IllegalStateException("Não é possível reabrir uma proposta em rascunho."); }
    @Override public String getNome()  { return "RASCUNHO"; }
    @Override public String getEtapa() { return "CADASTRO"; }
}
