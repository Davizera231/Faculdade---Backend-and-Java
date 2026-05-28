package com.esteira.state;
public class EstadoFactory {
    private EstadoFactory() {}
    public static EstadoProposta criar(String status) {
        if (status == null) throw new IllegalArgumentException("Status não pode ser nulo.");
        return switch (status.toUpperCase()) {
            case "RASCUNHO"  -> new EstadoRascunho();
            case "ANALISE"   -> new EstadoAnalise();
            case "APROVADA"  -> new EstadoAprovada();
            case "REPROVADA" -> new EstadoReprovada();
            default -> throw new IllegalArgumentException("Estado desconhecido: " + status);
        };
    }
}
