package com.gestaopgr.model;

import java.time.LocalDateTime;

public class Risco {
    public static final String[] CLASSIFICACOES = {"BAIXO", "MEDIO", "ALTO", "CRITICO"};
    public static final String[] STATUS = {"IDENTIFICADO", "EM_TRATAMENTO", "CONTROLADO", "ACEITO"};

    private Long id;
    private Long empresaId;
    private Long setorId;
    private Long cargoId;
    private Long perigoId;
    private String perigoDescricao;
    private String categoria;
    private String fonte;
    private String atividade;
    private int numTrabalhadoresExpostos;
    private int probabilidade;
    private int severidade;
    private int nivelRisco;
    private String classificacao;
    private String medidasExistentes;
    private String medidasPropostas;
    private String epiRecomendado;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // auxiliares para exibição
    private String setorNome;
    private String cargoNome;
    private String empresaNome;

    public Risco() {
        this.probabilidade = 1;
        this.severidade = 1;
        this.status = "IDENTIFICADO";
    }

    public void calcularNivel() {
        this.nivelRisco = this.probabilidade * this.severidade;
        if (nivelRisco <= 4) classificacao = "BAIXO";
        else if (nivelRisco <= 9) classificacao = "MEDIO";
        else if (nivelRisco <= 16) classificacao = "ALTO";
        else classificacao = "CRITICO";
    }

    public Long getId() { return id; }
    public void setId(Long v) { this.id = v; }
    public Long getEmpresaId() { return empresaId; }
    public void setEmpresaId(Long v) { this.empresaId = v; }
    public Long getSetorId() { return setorId; }
    public void setSetorId(Long v) { this.setorId = v; }
    public Long getCargoId() { return cargoId; }
    public void setCargoId(Long v) { this.cargoId = v; }
    public Long getPerigoId() { return perigoId; }
    public void setPerigoId(Long v) { this.perigoId = v; }
    public String getPerigoDescricao() { return perigoDescricao; }
    public void setPerigoDescricao(String v) { this.perigoDescricao = v; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String v) { this.categoria = v; }
    public String getFonte() { return fonte; }
    public void setFonte(String v) { this.fonte = v; }
    public String getAtividade() { return atividade; }
    public void setAtividade(String v) { this.atividade = v; }
    public int getNumTrabalhadoresExpostos() { return numTrabalhadoresExpostos; }
    public void setNumTrabalhadoresExpostos(int v) { this.numTrabalhadoresExpostos = v; }
    public int getProbabilidade() { return probabilidade; }
    public void setProbabilidade(int v) { this.probabilidade = v; calcularNivel(); }
    public int getSeveridade() { return severidade; }
    public void setSeveridade(int v) { this.severidade = v; calcularNivel(); }
    public int getNivelRisco() { return nivelRisco; }
    public void setNivelRisco(int v) { this.nivelRisco = v; }
    public String getClassificacao() { return classificacao; }
    public void setClassificacao(String v) { this.classificacao = v; }
    public String getMedidasExistentes() { return medidasExistentes; }
    public void setMedidasExistentes(String v) { this.medidasExistentes = v; }
    public String getMedidasPropostas() { return medidasPropostas; }
    public void setMedidasPropostas(String v) { this.medidasPropostas = v; }
    public String getEpiRecomendado() { return epiRecomendado; }
    public void setEpiRecomendado(String v) { this.epiRecomendado = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime v) { this.updatedAt = v; }
    public String getSetorNome() { return setorNome; }
    public void setSetorNome(String v) { this.setorNome = v; }
    public String getCargoNome() { return cargoNome; }
    public void setCargoNome(String v) { this.cargoNome = v; }
    public String getEmpresaNome() { return empresaNome; }
    public void setEmpresaNome(String v) { this.empresaNome = v; }

    public java.awt.Color getClassificacaoColor() {
        switch (classificacao != null ? classificacao : "") {
            case "BAIXO": return new java.awt.Color(34, 197, 94);
            case "MEDIO": return new java.awt.Color(249, 115, 22);
            case "ALTO": return new java.awt.Color(239, 68, 68);
            case "CRITICO": return new java.awt.Color(127, 29, 29);
            default: return new java.awt.Color(148, 163, 184);
        }
    }
}
