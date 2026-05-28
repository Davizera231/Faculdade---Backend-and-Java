package com.esteira.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
@Entity @Table(name = "cliente")
public class Cliente implements Serializable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private int id;
    @NotBlank @Column(nullable=false, length=150) private String nome;
    @NotBlank @Column(name="cpf_cnpj", nullable=false, unique=true, length=20) private String cpfCnpj;
    @Column(length=100) private String email;
    @Column(length=20)  private String telefone;
    @Column(length=200) private String endereco;
    @Column(length=100) private String cidade;
    @Column(length=2)   private String estado;
    @Column(length=10)  private String cep;
    @Column(nullable=false, length=20) private String tipo = "PESSOA_FISICA";
    @Column(nullable=false) private boolean ativo = true;
    public Cliente() {}
    public int getId()          { return id; }
    public String getNome()     { return nome; }
    public String getCpfCnpj()  { return cpfCnpj; }
    public String getEmail()    { return email; }
    public String getTelefone() { return telefone; }
    public String getEndereco() { return endereco; }
    public String getCidade()   { return cidade; }
    public String getEstado()   { return estado; }
    public String getCep()      { return cep; }
    public String getTipo()     { return tipo; }
    public boolean isAtivo()    { return ativo; }
    public void setId(int v)            { this.id=v; }
    public void setNome(String v)       { this.nome=v; }
    public void setCpfCnpj(String v)    { this.cpfCnpj=v; }
    public void setEmail(String v)      { this.email=v; }
    public void setTelefone(String v)   { this.telefone=v; }
    public void setEndereco(String v)   { this.endereco=v; }
    public void setCidade(String v)     { this.cidade=v; }
    public void setEstado(String v)     { this.estado=v; }
    public void setCep(String v)        { this.cep=v; }
    public void setTipo(String v)       { this.tipo=v; }
    public void setAtivo(boolean v)     { this.ativo=v; }
    public static class Builder {
        private final Cliente c = new Cliente();
        public Builder id(int v)          { c.id=v; return this; }
        public Builder nome(String v)     { c.nome=v; return this; }
        public Builder cpfCnpj(String v)  { c.cpfCnpj=v; return this; }
        public Builder email(String v)    { c.email=v; return this; }
        public Builder telefone(String v) { c.telefone=v; return this; }
        public Builder endereco(String v) { c.endereco=v; return this; }
        public Builder cidade(String v)   { c.cidade=v; return this; }
        public Builder estado(String v)   { c.estado=v; return this; }
        public Builder cep(String v)      { c.cep=v; return this; }
        public Builder tipo(String v)     { c.tipo=v; return this; }
        public Builder ativo(boolean v)   { c.ativo=v; return this; }
        public Cliente build() {
            if (c.nome==null||c.nome.isBlank()) throw new IllegalStateException("Nome obrigatório.");
            if (c.cpfCnpj==null||c.cpfCnpj.isBlank()) throw new IllegalStateException("CPF/CNPJ obrigatório.");
            return c;
        }
    }
}
