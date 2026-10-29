package com.gestaopgr.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Contrato {
    public static final String[] TIPOS = {"PONTUAL", "RECORRENTE", "MANUTENCAO"};
    public static final String[] STATUS = {"ATIVO", "SUSPENSO", "ENCERRADO", "CANCELADO"};
    public static final String[] PERIODICIDADES = {"MENSAL", "TRIMESTRAL", "SEMESTRAL", "ANUAL"};

    private Long id;
    private Long empresaId;
    private Long propostaId;
    private String codigo;
    private String titulo;
    private String descricao;
    private String tipo;
    private String status;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private double valorTotal;
    private double valorRecorrente;
    private String periodicidade;
    private Integer diaVencimento;
    private String observacoes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String empresaNome;

    public Contrato() {
        this.tipo = "PONTUAL";
        this.status = "ATIVO";
        this.dataInicio = LocalDate.now();
    }

    public Long getId() { return id; }
    public void setId(Long v) { this.id = v; }
    public Long getEmpresaId() { return empresaId; }
    public void setEmpresaId(Long v) { this.empresaId = v; }
    public Long getPropostaId() { return propostaId; }
    public void setPropostaId(Long v) { this.propostaId = v; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String v) { this.codigo = v; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String v) { this.titulo = v; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String v) { this.descricao = v; }
    public String getTipo() { return tipo; }
    public void setTipo(String v) { this.tipo = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }
    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate v) { this.dataInicio = v; }
    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate v) { this.dataFim = v; }
    public double getValorTotal() { return valorTotal; }
    public void setValorTotal(double v) { this.valorTotal = v; }
    public double getValorRecorrente() { return valorRecorrente; }
    public void setValorRecorrente(double v) { this.valorRecorrente = v; }
    public String getPeriodicidade() { return periodicidade; }
    public void setPeriodicidade(String v) { this.periodicidade = v; }
    public Integer getDiaVencimento() { return diaVencimento; }
    public void setDiaVencimento(Integer v) { this.diaVencimento = v; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String v) { this.observacoes = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime v) { this.updatedAt = v; }
    public String getEmpresaNome() { return empresaNome; }
    public void setEmpresaNome(String v) { this.empresaNome = v; }
}

