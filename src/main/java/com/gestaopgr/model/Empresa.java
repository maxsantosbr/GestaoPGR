package com.gestaopgr.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Empresa {
    private Long id;
    private String razaoSocial;
    private String nomeFantasia;
    private String cnpj;
    private String cnae;
    private String cnaeDescricao;
    private String endereco;
    private String cidade;
    private String uf;
    private String cep;
    private String telefone;
    private String email;
    private String responsavel;
    private int numEmpregados;
    private String logoPath;
    private boolean ativo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Empresa() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRazaoSocial() { return razaoSocial; }
    public void setRazaoSocial(String v) { this.razaoSocial = v; }
    public String getNomeFantasia() { return nomeFantasia; }
    public void setNomeFantasia(String v) { this.nomeFantasia = v; }
    public String getCnpj() { return cnpj; }
    public void setCnpj(String v) { this.cnpj = v; }
    public String getCnae() { return cnae; }
    public void setCnae(String v) { this.cnae = v; }
    public String getCnaeDescricao() { return cnaeDescricao; }
    public void setCnaeDescricao(String v) { this.cnaeDescricao = v; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String v) { this.endereco = v; }
    public String getCidade() { return cidade; }
    public void setCidade(String v) { this.cidade = v; }
    public String getUf() { return uf; }
    public void setUf(String v) { this.uf = v; }
    public String getCep() { return cep; }
    public void setCep(String v) { this.cep = v; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String v) { this.telefone = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { this.email = v; }
    public String getResponsavel() { return responsavel; }
    public void setResponsavel(String v) { this.responsavel = v; }
    public int getNumEmpregados() { return numEmpregados; }
    public void setNumEmpregados(int v) { this.numEmpregados = v; }
    public String getLogoPath() { return logoPath; }
    public void setLogoPath(String v) { this.logoPath = v; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean v) { this.ativo = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime v) { this.updatedAt = v; }

    @Override
    public String toString() { return razaoSocial != null ? razaoSocial : ""; }
}
