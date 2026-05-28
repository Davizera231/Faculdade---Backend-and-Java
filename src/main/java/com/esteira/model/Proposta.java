package com.esteira.model;
import com.esteira.state.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.io.Serializable;
import java.util.*;
@Entity @Table(name = "proposta")
public class Proposta implements Serializable {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private int id;
    @Column(nullable=false, unique=true, length=20) private String codigo; // gerado automaticamente pelo service
    @NotBlank @Column(nullable=false, length=200) private String titulo;
    @Column(columnDefinition="TEXT") private String descricao;
    @Positive @Column(nullable=false) private double valor;
    @Column(nullable=false, length=20) private String status = "RASCUNHO";
    @Column(name="etapa_atual", nullable=false, length=50) private String etapaAtual = "CADASTRO";
    @Temporal(TemporalType.TIMESTAMP) @Column(name="data_criacao", nullable=false, updatable=false) private Date dataCriacao = new Date();
    @Temporal(TemporalType.TIMESTAMP) @Column(name="data_atualizacao", nullable=false) private Date dataAtualizacao = new Date();
    @Column(columnDefinition="TEXT") private String observacoes;
    @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="cliente_id", nullable=false) private Cliente cliente;
    @OneToMany(mappedBy="proposta", cascade=CascadeType.ALL, orphanRemoval=true) @JsonManagedReference private List<Documento> documentos = new ArrayList<>();
    @Transient private EstadoProposta estado;
    public static final String STATUS_RASCUNHO="RASCUNHO", STATUS_ANALISE="ANALISE", STATUS_APROVADA="APROVADA", STATUS_REPROVADA="REPROVADA";
    public Proposta() {}
    @PostLoad @PostPersist public void inicializarEstado() { this.estado = EstadoFactory.criar(this.status); }
    public int getId()                     { return id; }
    public String getCodigo()              { return codigo; }
    public String getTitulo()              { return titulo; }
    public String getDescricao()           { return descricao; }
    public double getValor()               { return valor; }
    public String getStatus()              { return status; }
    public String getEtapaAtual()          { return etapaAtual; }
    public Date getDataCriacao()           { return dataCriacao; }
    public Date getDataAtualizacao()       { return dataAtualizacao; }
    public String getObservacoes()         { return observacoes; }
    public Cliente getCliente()            { return cliente; }
    public List<Documento> getDocumentos() { return documentos; }
    public EstadoProposta getEstado()      { return estado; }
    public void setId(int v)                    { this.id=v; }
    public void setCodigo(String v)             { this.codigo=v; }
    public void setTitulo(String v)             { this.titulo=v; }
    public void setDescricao(String v)          { this.descricao=v; }
    public void setValor(double v)              { this.valor=v; }
    public void setStatus(String v)             { this.status=v; }
    public void setEtapaAtual(String v)         { this.etapaAtual=v; }
    public void setDataCriacao(Date v)          { this.dataCriacao=v; }
    public void setDataAtualizacao(Date v)      { this.dataAtualizacao=v; }
    public void setObservacoes(String v)        { this.observacoes=v; }
    public void setCliente(Cliente v)           { this.cliente=v; }
    public void setDocumentos(List<Documento> v){ this.documentos=v; }
    public void setEstado(EstadoProposta e) { this.estado=e; this.status=e.getNome(); this.etapaAtual=e.getEtapa(); this.dataAtualizacao=new Date(); }
    public void avancar()  { if(estado==null) estado=new EstadoRascunho(); estado.avancar(this); }
    public void reprovar() { if(estado==null) throw new IllegalStateException("Estado não definido."); estado.reprovar(this); }
    public void reabrir()  { if(estado==null) throw new IllegalStateException("Estado não definido."); estado.reabrir(this); }
    public static class Builder {
        private final Proposta p = new Proposta();
        public Builder() { p.estado = new EstadoRascunho(); }
        public Builder id(int v)               { p.id=v; return this; }
        public Builder codigo(String v)        { p.codigo=v; return this; }
        public Builder titulo(String v)        { p.titulo=v; return this; }
        public Builder descricao(String v)     { p.descricao=v; return this; }
        public Builder valor(double v)         { p.valor=v; return this; }
        public Builder status(String v)        { p.status=v; p.estado=EstadoFactory.criar(v); return this; }
        public Builder etapaAtual(String v)    { p.etapaAtual=v; return this; }
        public Builder dataCriacao(Date v)     { p.dataCriacao=v; return this; }
        public Builder dataAtualizacao(Date v) { p.dataAtualizacao=v; return this; }
        public Builder observacoes(String v)   { p.observacoes=v; return this; }
        public Builder cliente(Cliente v)      { p.cliente=v; return this; }
        public Proposta build() {
            if(p.titulo==null||p.titulo.isBlank()) throw new IllegalStateException("Título obrigatório.");
            if(p.valor<=0) throw new IllegalStateException("Valor deve ser maior que zero.");
            if(p.cliente==null) throw new IllegalStateException("Cliente obrigatório.");
            return p;
        }
    }
}
