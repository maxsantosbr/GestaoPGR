package com.gestaopgr.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
public class Pagamento {
    public static final String[] FORMAS = {"PIX", "BOLETO", "CARTAO_CREDITO", "CARTAO_DEBITO", "TRANSFERENCIA", "DINHEIRO", "CHEQUE"};
    public static final String[] STATUS = {"CONFIRMADO", "PENDENTE", "ESTORNADO"};

    private Long id;
    private Long faturaId;
    private Long empresaId;
    private LocalDate dataPagamento;
    private double valor;
    private String formaPagamento;
    private String status;
    private String comprovantePath;
    private String observacao;
    private LocalDateTime createdAt;

    public Pagamento() {
        this.status = "CONFIRMADO";
        this.dataPagamento = LocalDate.now();
    }

    public Long getId() { return id; }
    public void setId(Long v) { this.id = v; }
    public Long getFaturaId() { return faturaId; }
    public void setFaturaId(Long v) { this.faturaId = v; }
    public Long getEmpresaId() { return empresaId; }
    public void setEmpresaId(Long v) { this.empresaId = v; }
    public LocalDate getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(LocalDate v) { this.dataPagamento = v; }
    public double getValor() { return valor; }
    public void setValor(double v) { this.valor = v; }
    public String getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(String v) { this.formaPagamento = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }
    public String getComprovantePath() { return comprovantePath; }
    public void setComprovantePath(String v) { this.comprovantePath = v; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String v) { this.observacao = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
}

