package com.esteira.model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;
@Entity @Table(name = "documento")
public class Documento implements Serializable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private int id;
    @NotBlank @Column(nullable=false, length=200) private String nome;
    @Column(length=50) private String tipo;
    @Column(length=300) private String caminho;
    @Column(columnDefinition="TEXT") private String descricao;
    @Temporal(TemporalType.TIMESTAMP) @Column(name="data_upload", nullable=false) private Date dataUpload = new Date();
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="proposta_id", nullable=false) @JsonBackReference private Proposta proposta;
    public Documento() {}
    public int getId()           { return id; }
    public String getNome()      { return nome; }
    public String getTipo()      { return tipo; }
    public String getCaminho()   { return caminho; }
    public String getDescricao() { return descricao; }
    public Date getDataUpload()  { return dataUpload; }
    public Proposta getProposta(){ return proposta; }
    public void setId(int v)           { this.id=v; }
    public void setNome(String v)      { this.nome=v; }
    public void setTipo(String v)      { this.tipo=v; }
    public void setCaminho(String v)   { this.caminho=v; }
    public void setDescricao(String v) { this.descricao=v; }
    public void setDataUpload(Date v)  { this.dataUpload=v; }
    public void setProposta(Proposta v){ this.proposta=v; }
    public static class Builder {
        private final Documento d = new Documento();
        public Builder nome(String v)      { d.nome=v; return this; }
        public Builder tipo(String v)      { d.tipo=v; return this; }
        public Builder caminho(String v)   { d.caminho=v; return this; }
        public Builder descricao(String v) { d.descricao=v; return this; }
        public Builder proposta(Proposta v){ d.proposta=v; return this; }
        public Documento build() {
            if (d.nome==null||d.nome.isBlank()) throw new IllegalStateException("Nome obrigatório.");
            if (d.proposta==null) throw new IllegalStateException("Proposta obrigatória.");
            return d;
        }
    }
}
