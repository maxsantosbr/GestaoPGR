package com.gestaopgr.model;

import java.time.LocalDateTime;

public class Setor {
    private Long id;
    private Long empresaId;
    private String nome;
    private String descricao;
    private int numTrabalhadores;
    private boolean ativo;
    private LocalDateTime createdAt;
    private String empresaNome;

    public Setor() {}
    public Long getId() { return id; }
    public void setId(Long v) { this.id = v; }
    public Long getEmpresaId() { return empresaId; }
    public void setEmpresaId(Long v) { this.empresaId = v; }
    public String getNome() { return nome; }
    public void setNome(String v) { this.nome = v; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String v) { this.descricao = v; }
    public int getNumTrabalhadores() { return numTrabalhadores; }
    public void setNumTrabalhadores(int v) { this.numTrabalhadores = v; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean v) { this.ativo = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
    public String getEmpresaNome() { return empresaNome; }
    public void setEmpresaNome(String v) { this.empresaNome = v; }
    @Override public String toString() { return nome != null ? nome : ""; }
}
