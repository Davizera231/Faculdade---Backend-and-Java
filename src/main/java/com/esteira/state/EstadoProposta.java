package com.esteira.state;
import com.esteira.model.Proposta;
public interface EstadoProposta {
    void avancar(Proposta proposta);
    void reprovar(Proposta proposta);
    void reabrir(Proposta proposta);
    String getNome();
    String getEtapa();
}
