package com.gestaopgr.dao;

import com.gestaopgr.model.TipoServico;
import com.gestaopgr.model.Proposta;
import com.gestaopgr.model.PropostaItem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class TipoServicoDao extends BaseDao {
    private TipoServico map(ResultSet rs) throws SQLException {
        TipoServico t = new TipoServico();
        t.setId(rs.getLong("id"));
        t.setNome(rs.getString("nome"));
        t.setDescricao(rs.getString("descricao"));
        t.setPrecoBase(rs.getDouble("preco_base"));
        t.setUnidade(rs.getString("unidade"));
        t.setAtivo(rs.getInt("ativo") == 1);
        t.setCreatedAt(parseDateTime(rs.getString("created_at")));
        return t;
    }
    public TipoServico save(TipoServico t) throws SQLException {
        if (t.getId() == null) {
            long id = executeInsert("INSERT INTO tipo_servico (nome, descricao, preco_base, unidade, ativo) VALUES (?,?,?,?,?)",
                t.getNome(), t.getDescricao(), t.getPrecoBase(), t.getUnidade(), t.isAtivo());
            t.setId(id);
        } else {
            executeUpdate("UPDATE tipo_servico SET nome=?, descricao=?, preco_base=?, unidade=?, ativo=? WHERE id=?",
                t.getNome(), t.getDescricao(), t.getPrecoBase(), t.getUnidade(), t.isAtivo(), t.getId());
        }
        return t;
    }
    public List<TipoServico> findAll() throws SQLException { return query("SELECT * FROM tipo_servico WHERE ativo=1 ORDER BY nome", this::map); }
    public Optional<TipoServico> findById(Long id) throws SQLException { return queryOne("SELECT * FROM tipo_servico WHERE id=?", this::map, id); }
    public void delete(Long id) throws SQLException { executeUpdate("DELETE FROM tipo_servico WHERE id=?", id); }
    private LocalDateTime parseDateTime(String s) { if (s == null) return null; return LocalDateTime.parse(s.replace(' ', 'T')); }
}

