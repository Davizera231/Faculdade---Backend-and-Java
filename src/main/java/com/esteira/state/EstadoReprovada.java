package com.esteira.state;
import com.esteira.model.Proposta;
public class EstadoReprovada implements EstadoProposta {
    @Override public void avancar(Proposta p)  { throw new IllegalStateException("Use 'reabrir' para retornar ao rascunho."); }
    @Override public void reprovar(Proposta p) { throw new IllegalStateException("Proposta já está reprovada."); }
    @Override public void reabrir(Proposta p)  { p.setEstado(new EstadoRascunho()); }
    @Override public String getNome()  { return "REPROVADA"; }
    @Override public String getEtapa() { return "REPROVADA"; }
}
