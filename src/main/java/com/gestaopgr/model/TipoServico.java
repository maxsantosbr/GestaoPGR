package com.gestaopgr.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TipoServico {
    private Long id;
    private String nome;
    private String descricao;
    private double precoBase;
    private String unidade;
    private boolean ativo;
    private LocalDateTime createdAt;

    public TipoServico() {}
    public Long getId() { return id; }
    public void setId(Long v) { this.id = v; }
    public String getNome() { return nome; }
    public void setNome(String v) { this.nome = v; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String v) { this.descricao = v; }
    public double getPrecoBase() { return precoBase; }
    public void setPrecoBase(double v) { this.precoBase = v; }
    public String getUnidade() { return unidade; }
    public void setUnidade(String v) { this.unidade = v; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean v) { this.ativo = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
    @Override public String toString() { return nome != null ? nome : ""; }
}
