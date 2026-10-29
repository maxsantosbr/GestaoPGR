package com.gestaopgr.model;

import java.time.LocalDateTime;

public class Cargo {
    private Long id;
    private Long empresaId;
    private String nome;
    private String cbo;
    private String descricao;
    private boolean ativo;
    private LocalDateTime createdAt;

    public Cargo() {}
    public Long getId() { return id; }
    public void setId(Long v) { this.id = v; }
    public Long getEmpresaId() { return empresaId; }
    public void setEmpresaId(Long v) { this.empresaId = v; }
    public String getNome() { return nome; }
    public void setNome(String v) { this.nome = v; }
    public String getCbo() { return cbo; }
    public void setCbo(String v) { this.cbo = v; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String v) { this.descricao = v; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean v) { this.ativo = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
    @Override public String toString() { return nome != null ? nome : ""; }
}
