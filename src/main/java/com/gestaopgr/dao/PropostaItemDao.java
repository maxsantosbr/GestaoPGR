package com.gestaopgr.dao;

import com.gestaopgr.model.PropostaItem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
public class PropostaItemDao extends BaseDao {
    private PropostaItem map(ResultSet rs) throws SQLException {
        PropostaItem pi = new PropostaItem();
        pi.setId(rs.getLong("id"));
        pi.setPropostaId(rs.getLong("proposta_id"));
        pi.setTipoServicoId(getLongOrNull(rs, "tipo_servico_id"));
        pi.setDescricao(rs.getString("descricao"));
        pi.setQuantidade(rs.getDouble("quantidade"));
        pi.setValorUnitario(rs.getDouble("valor_unitario"));
        pi.setValorTotal(rs.getDouble("valor_total"));
        try { pi.setTipoServicoNome(rs.getString("ts_nome")); } catch (SQLException ignored) {}
        return pi;
    }
    public PropostaItem save(PropostaItem pi) throws SQLException {
        if (pi.getId() == null) {
            long id = executeInsert("INSERT INTO proposta_item (proposta_id, tipo_servico_id, descricao, quantidade, valor_unitario, valor_total) VALUES (?,?,?,?,?,?)",
                pi.getPropostaId(), pi.getTipoServicoId(), pi.getDescricao(), pi.getQuantidade(), pi.getValorUnitario(), pi.getValorTotal());
            pi.setId(id);
        } else {
            executeUpdate("UPDATE proposta_item SET descricao=?, quantidade=?, valor_unitario=?, valor_total=? WHERE id=?",
                pi.getDescricao(), pi.getQuantidade(), pi.getValorUnitario(), pi.getValorTotal(), pi.getId());
        }
        return pi;
    }
    public List<PropostaItem> findByProposta(Long propostaId) throws SQLException {
        String sql = "SELECT pi.*, ts.nome as ts_nome FROM proposta_item pi LEFT JOIN tipo_servico ts ON pi.tipo_servico_id=ts.id WHERE pi.proposta_id=? ORDER BY pi.id";
        return query(sql, this::map, propostaId);
    }
    public void delete(Long id) throws SQLException { executeUpdate("DELETE FROM proposta_item WHERE id=?", id); }
    public void deleteByProposta(Long propostaId) throws SQLException { executeUpdate("DELETE FROM proposta_item WHERE proposta_id=?", propostaId); }
    private Long getLongOrNull(ResultSet rs, String col) throws SQLException { long v = rs.getLong(col); return rs.wasNull() ? null : v; }
}

