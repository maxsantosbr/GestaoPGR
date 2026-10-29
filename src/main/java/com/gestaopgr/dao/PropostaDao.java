package com.gestaopgr.dao;

import com.gestaopgr.model.Proposta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
public class PropostaDao extends BaseDao {
    private Proposta map(ResultSet rs) throws SQLException {
        Proposta p = new Proposta();
        p.setId(rs.getLong("id"));
        p.setEmpresaId(rs.getLong("empresa_id"));
        p.setCodigo(rs.getString("codigo"));
        p.setTitulo(rs.getString("titulo"));
        p.setDescricao(rs.getString("descricao"));
        p.setStatus(rs.getString("status"));
        String dc = rs.getString("data_criacao");
        if (dc != null && !dc.isEmpty()) p.setDataCriacao(LocalDateTime.parse(dc.replace(' ', 'T')));
        String dv = rs.getString("data_validade");
        if (dv != null && !dv.isEmpty()) p.setDataValidade(LocalDate.parse(dv.substring(0, 10)));
        p.setValorTotal(rs.getDouble("valor_total"));
        p.setDesconto(rs.getDouble("desconto"));
        p.setValorFinal(rs.getDouble("valor_final"));
        p.setCondicoesPagamento(rs.getString("condicoes_pagamento"));
        p.setObservacoes(rs.getString("observacoes"));
        p.setCreatedAt(parseDateTime(rs.getString("created_at")));
        p.setUpdatedAt(parseDateTime(rs.getString("updated_at")));
        try { p.setEmpresaNome(rs.getString("empresa_nome")); } catch (SQLException ignored) {}
        return p;
    }
    public Proposta save(Proposta p) throws SQLException {
        if (p.getId() == null) {
            long id = executeInsert(
                "INSERT INTO proposta (empresa_id, codigo, titulo, descricao, status, data_criacao, data_validade, valor_total, desconto, valor_final, condicoes_pagamento, observacoes) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)",
                p.getEmpresaId(), p.getCodigo(), p.getTitulo(), p.getDescricao(), p.getStatus(), p.getDataCriacao(), p.getDataValidade(), p.getValorTotal(), p.getDesconto(), p.getValorFinal(), p.getCondicoesPagamento(), p.getObservacoes()
            );
            p.setId(id);
        } else {
            executeUpdate(
                "UPDATE proposta SET codigo=?, titulo=?, descricao=?, status=?, data_validade=?, valor_total=?, desconto=?, valor_final=?, condicoes_pagamento=?, observacoes=?, updated_at=datetime('now') WHERE id=?",
                p.getCodigo(), p.getTitulo(), p.getDescricao(), p.getStatus(), p.getDataValidade(), p.getValorTotal(), p.getDesconto(), p.getValorFinal(), p.getCondicoesPagamento(), p.getObservacoes(), p.getId()
            );
        }
        return p;
    }
    public Optional<Proposta> findById(Long id) throws SQLException {
        return queryOne("SELECT p.*, e.razao_social as empresa_nome FROM proposta p LEFT JOIN empresa e ON p.empresa_id=e.id WHERE p.id=?", this::map, id);
    }
    public List<Proposta> findByEmpresa(Long empresaId) throws SQLException {
        return query("SELECT p.*, e.razao_social as empresa_nome FROM proposta p LEFT JOIN empresa e ON p.empresa_id=e.id WHERE p.empresa_id=? ORDER BY p.created_at DESC", this::map, empresaId);
    }
    public List<Proposta> findAll() throws SQLException {
        return query("SELECT p.*, e.razao_social as empresa_nome FROM proposta p LEFT JOIN empresa e ON p.empresa_id=e.id ORDER BY p.created_at DESC", this::map);
    }
    public void delete(Long id) throws SQLException { executeUpdate("DELETE FROM proposta WHERE id=?", id); }
    private LocalDateTime parseDateTime(String s) { if (s == null) return null; return LocalDateTime.parse(s.replace(' ', 'T')); }
}


