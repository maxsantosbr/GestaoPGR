package com.gestaopgr.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PlanoAcao {
    public static final String[] TIPOS = {"ELIMINACAO", "SUBSTITUICAO", "CONTROLE_ENGENHARIA", "CONTROLE_ADMINISTRATIVO", "EPI", "EPC"};
    public static final String[] STATUS = {"PENDENTE", "EM_ANDAMENTO", "CONCLUIDO", "CANCELADO"};

    private Long id;
    private Long riscoId;
    private Long empresaId;
    private String acao;
    private String tipo;
    private String responsavel;
    private LocalDate prazo;
    private Double custoEstimado;
    private String status;
    private LocalDate dataConclusao;
    private String observacao;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // auxiliares
    private String riscoDescricao;
    private String empresaNome;

    public PlanoAcao() { this.status = "PENDENTE"; }

    public Long getId() { return id; }
    public void setId(Long v) { this.id = v; }
    public Long getRiscoId() { return riscoId; }
    public void setRiscoId(Long v) { this.riscoId = v; }
    public Long getEmpresaId() { return empresaId; }
    public void setEmpresaId(Long v) { this.empresaId = v; }
    public String getAcao() { return acao; }
    public void setAcao(String v) { this.acao = v; }
    public String getTipo() { return tipo; }
    public void setTipo(String v) { this.tipo = v; }
    public String getResponsavel() { return responsavel; }
    public void setResponsavel(String v) { this.responsavel = v; }
    public LocalDate getPrazo() { return prazo; }
    public void setPrazo(LocalDate v) { this.prazo = v; }
    public Double getCustoEstimado() { return custoEstimado; }
    public void setCustoEstimado(Double v) { this.custoEstimado = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }
    public LocalDate getDataConclusao() { return dataConclusao; }
    public void setDataConclusao(LocalDate v) { this.dataConclusao = v; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String v) { this.observacao = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime v) { this.updatedAt = v; }
    public String getRiscoDescricao() { return riscoDescricao; }
    public void setRiscoDescricao(String v) { this.riscoDescricao = v; }
    public String getEmpresaNome() { return empresaNome; }
    public void setEmpresaNome(String v) { this.empresaNome = v; }
}
