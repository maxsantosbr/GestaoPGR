package com.gestaopgr.model;

public class Perigo {
    private Long id;
    private String codigo;
    private String nome;
    private String categoria;
    private String descricao;
    private String fonte;
    private String medidasControle;
    private String epiRecomendado;
    private String nrReferencia;
    private boolean ativo;

    public Perigo() {}
    public Long getId() { return id; }
    public void setId(Long v) { this.id = v; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String v) { this.codigo = v; }
    public String getNome() { return nome; }
    public void setNome(String v) { this.nome = v; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String v) { this.categoria = v; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String v) { this.descricao = v; }
    public String getFonte() { return fonte; }
    public void setFonte(String v) { this.fonte = v; }
    public String getMedidasControle() { return medidasControle; }
    public void setMedidasControle(String v) { this.medidasControle = v; }
    public String getEpiRecomendado() { return epiRecomendado; }
    public void setEpiRecomendado(String v) { this.epiRecomendado = v; }
    public String getNrReferencia() { return nrReferencia; }
    public void setNrReferencia(String v) { this.nrReferencia = v; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean v) { this.ativo = v; }
    @Override public String toString() { return nome != null ? nome : ""; }
}
