package com.esteira.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "documento")
public class Documento implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 200)
    private String nome;          // nome original do arquivo

    @Column(length = 50)
    private String tipo;          // sempre "application/pdf"

    @Column(columnDefinition = "TEXT")
    private String descricao;     // observação opcional sobre o doc

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_upload", nullable = false)
    private Date dataUpload = new Date();

    @Lob
    @Column(name = "conteudo", nullable = false)
    @JsonIgnore                   // não serializar bytes no JSON de listagem
    private byte[] conteudo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposta_id", nullable = false)
    @JsonBackReference
    private Proposta proposta;

    public Documento() {}

    public int getId()            { return id; }
    public String getNome()       { return nome; }
    public String getTipo()       { return tipo; }
    public String getDescricao()  { return descricao; }
    public Date getDataUpload()   { return dataUpload; }
    public byte[] getConteudo()   { return conteudo; }
    public Proposta getProposta() { return proposta; }

    public void setId(int v)            { this.id = v; }
    public void setNome(String v)       { this.nome = v; }
    public void setTipo(String v)       { this.tipo = v; }
    public void setDescricao(String v)  { this.descricao = v; }
    public void setDataUpload(Date v)   { this.dataUpload = v; }
    public void setConteudo(byte[] v)   { this.conteudo = v; }
    public void setProposta(Proposta v) { this.proposta = v; }
}
