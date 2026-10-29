package com.gestaopgr.dao;

import com.gestaopgr.model.Perigo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PerigoDao extends BaseDao {
    private Perigo map(ResultSet rs) throws SQLException {
        Perigo p = new Perigo();
        p.setId(rs.getLong("id"));
        p.setCodigo(rs.getString("codigo"));
        p.setNome(rs.getString("nome"));
        p.setCategoria(rs.getString("categoria"));
        p.setDescricao(rs.getString("descricao"));
        p.setFonte(rs.getString("fonte"));
        p.setMedidasControle(rs.getString("medidas_controle"));
        p.setEpiRecomendado(rs.getString("epi_recomendado"));
        p.setNrReferencia(rs.getString("nr_referencia"));
        p.setAtivo(rs.getInt("ativo") == 1);
        return p;
    }
    public List<Perigo> findAll() throws SQLException { return query("SELECT * FROM perigo WHERE ativo=1 ORDER BY categoria, nome", this::map); }
    public List<Perigo> findByCategoria(String categoria) throws SQLException { return query("SELECT * FROM perigo WHERE ativo=1 AND categoria=? ORDER BY nome", this::map, categoria); }
    public java.util.Optional<Perigo> findById(Long id) throws SQLException { return queryOne("SELECT * FROM perigo WHERE id=?", this::map, id); }
}
