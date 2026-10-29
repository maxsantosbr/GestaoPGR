package com.gestaopgr.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PropostaItem {
    private Long id;
    private Long propostaId;
    private Long tipoServicoId;
    private String descricao;
    private double quantidade;
    private double valorUnitario;
    private double valorTotal;
    private String tipoServicoNome;

    public PropostaItem() { this.quantidade = 1; }
    public Long getId() { return id; }
    public void setId(Long v) { this.id = v; }
    public Long getPropostaId() { return propostaId; }
    public void setPropostaId(Long v) { this.propostaId = v; }
    public Long getTipoServicoId() { return tipoServicoId; }
    public void setTipoServicoId(Long v) { this.tipoServicoId = v; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String v) { this.descricao = v; }
    public double getQuantidade() { return quantidade; }
    public void setQuantidade(double v) { this.quantidade = v; calcularTotal(); }
    public double getValorUnitario() { return valorUnitario; }
    public void setValorUnitario(double v) { this.valorUnitario = v; calcularTotal(); }
    public double getValorTotal() { return valorTotal; }
    public void setValorTotal(double v) { this.valorTotal = v; }
    public String getTipoServicoNome() { return tipoServicoNome; }
    public void setTipoServicoNome(String v) { this.tipoServicoNome = v; }
    private void calcularTotal() { this.valorTotal = this.quantidade * this.valorUnitario; }
}
