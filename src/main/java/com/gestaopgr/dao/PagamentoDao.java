package com.gestaopgr.dao;

import com.gestaopgr.model.Pagamento;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
public class PagamentoDao extends BaseDao {
    private Pagamento map(ResultSet rs) throws SQLException {
        Pagamento p = new Pagamento();
        p.setId(rs.getLong("id"));
        p.setFaturaId(rs.getLong("fatura_id"));
        p.setEmpresaId(rs.getLong("empresa_id"));
        String dp = rs.getString("data_pagamento");
        if (dp != null && !dp.isEmpty()) p.setDataPagamento(LocalDate.parse(dp.substring(0, 10)));
        p.setValor(rs.getDouble("valor"));
        p.setFormaPagamento(rs.getString("forma_pagamento"));
        p.setStatus(rs.getString("status"));
        p.setComprovantePath(rs.getString("comprovante_path"));
        p.setObservacao(rs.getString("observacao"));
        p.setCreatedAt(parseDateTime(rs.getString("created_at")));
        return p;
    }
    public Pagamento save(Pagamento p) throws SQLException {
        if (p.getId() == null) {
            long id = executeInsert(
                "INSERT INTO pagamento (fatura_id, empresa_id, data_pagamento, valor, forma_pagamento, status, comprovante_path, observacao) VALUES (?,?,?,?,?,?,?,?)",
                p.getFaturaId(), p.getEmpresaId(), p.getDataPagamento(), p.getValor(), p.getFormaPagamento(), p.getStatus(), p.getComprovantePath(), p.getObservacao()
            );
            p.setId(id);
        } else {
            executeUpdate("UPDATE pagamento SET status=?, observacao=? WHERE id=?", p.getStatus(), p.getObservacao(), p.getId());
        }
        return p;
    }
    public List<Pagamento> findByFatura(Long faturaId) throws SQLException {
        return query("SELECT * FROM pagamento WHERE fatura_id=? ORDER BY data_pagamento DESC", this::map, faturaId);
    }
    public List<Pagamento> findByEmpresa(Long empresaId) throws SQLException {
        return query("SELECT * FROM pagamento WHERE empresa_id=? ORDER BY data_pagamento DESC", this::map, empresaId);
    }
    public List<Pagamento> findAll() throws SQLException {
        return query("SELECT * FROM pagamento ORDER BY data_pagamento DESC", this::map);
    }
    public double getTotalRecebido(Long empresaId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(valor),0) as total FROM pagamento WHERE empresa_id=? AND status='CONFIRMADO'";
        List<Double> r = query(sql, rs -> rs.getDouble("total"), empresaId);
        return r.isEmpty() ? 0 : r.get(0);
    }
    public void delete(Long id) throws SQLException { executeUpdate("DELETE FROM pagamento WHERE id=?", id); }
    private LocalDateTime parseDateTime(String s) { if (s == null) return null; return LocalDateTime.parse(s.replace(' ', 'T')); }
}

