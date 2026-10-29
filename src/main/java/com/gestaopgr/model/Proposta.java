package com.gestaopgr.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Proposta {
    public static final String[] STATUS = {"RASCUNHO", "ENVIADA", "APROVADA", "REJEITADA", "EXPIRADA"};

    private Long id;
    private Long empresaId;
    private String codigo;
    private String titulo;
    private String descricao;
    private String status;
    private LocalDateTime dataCriacao;
    private LocalDate dataValidade;
    private double valorTotal;
    private double desconto;
    private double valorFinal;
    private String condicoesPagamento;
    private String observacoes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String empresaNome;

    public Proposta() {
        this.status = "RASCUNHO";
        this.dataCriacao = LocalDateTime.now();
        this.dataValidade = LocalDate.now().plusDays(30);
    }

    public Long getId() { return id; }
    public void setId(Long v) { this.id = v; }
    public Long getEmpresaId() { return empresaId; }
    public void setEmpresaId(Long v) { this.empresaId = v; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String v) { this.codigo = v; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String v) { this.titulo = v; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String v) { this.descricao = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime v) { this.dataCriacao = v; }
    public LocalDate getDataValidade() { return dataValidade; }
    public void setDataValidade(LocalDate v) { this.dataValidade = v; }
    public double getValorTotal() { return valorTotal; }
    public void setValorTotal(double v) { this.valorTotal = v; }
    public double getDesconto() { return desconto; }
    public void setDesconto(double v) { this.desconto = v; }
    public double getValorFinal() { return valorFinal; }
    public void setValorFinal(double v) { this.valorFinal = v; }
    public String getCondicoesPagamento() { return condicoesPagamento; }
    public void setCondicoesPagamento(String v) { this.condicoesPagamento = v; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String v) { this.observacoes = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime v) { this.updatedAt = v; }
    public String getEmpresaNome() { return empresaNome; }
    public void setEmpresaNome(String v) { this.empresaNome = v; }
}
