package com.esteira.factory;
import com.esteira.command.*;
public class FabricaComandoEsteira {
    public static final String ACAO_ENVIAR_ANALISE="ENVIAR_ANALISE", ACAO_APROVAR="APROVAR", ACAO_REPROVAR="REPROVAR", ACAO_REABRIR="REABRIR";
    private FabricaComandoEsteira() {}
    public static ComandoEsteira criar(String acao) {
        if (acao == null) throw new IllegalArgumentException("Ação não pode ser nula.");
        return switch (acao.toUpperCase()) {
            case "ENVIAR_ANALISE" -> new ComandoEnviarParaAnalise();
            case "APROVAR"        -> new ComandoAprovarProposta();
            case "REPROVAR"       -> new ComandoReprovarProposta();
            case "REABRIR"        -> new ComandoReabrirProposta();
            default -> throw new IllegalArgumentException("Ação desconhecida: " + acao);
        };
    }
    public static String[] acoesDisponiveisPara(String status) {
        return switch (status.toUpperCase()) {
            case "RASCUNHO"  -> new String[]{ACAO_ENVIAR_ANALISE};
            case "ANALISE"   -> new String[]{ACAO_APROVAR, ACAO_REPROVAR};
            case "REPROVADA" -> new String[]{ACAO_REABRIR};
            default          -> new String[]{};
        };
    }
}
