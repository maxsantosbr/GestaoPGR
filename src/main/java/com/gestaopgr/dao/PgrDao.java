package com.gestaopgr.dao;

import com.gestaopgr.model.Pgr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class PgrDao extends BaseDao {
    private Pgr map(ResultSet rs) throws SQLException {
        Pgr p = new Pgr();
        p.setId(rs.getLong("id"));
        p.setEmpresaId(rs.getLong("empresa_id"));
        p.setCodigo(rs.getString("codigo"));
        p.setTitulo(rs.getString("titulo"));
        p.setVersao(rs.getInt("versao"));
        String de = rs.getString("data_elaboracao");
        if (de != null && !de.isEmpty()) p.setDataElaboracao(LocalDate.parse(de.substring(0, 10)));
        String dv = rs.getString("data_validade");
        if (dv != null && !dv.isEmpty()) p.setDataValidade(LocalDate.parse(dv.substring(0, 10)));
        p.setElaborador(rs.getString("elaborador"));
        p.setElaboradorRegistro(rs.getString("elaborador_registro"));
        p.setStatus(rs.getString("status"));
        p.setObservacoes(rs.getString("observacoes"));
        p.setPdfPath(rs.getString("pdf_path"));
        p.setCreatedAt(parseDateTime(rs.getString("created_at")));
        p.setUpdatedAt(parseDateTime(rs.getString("updated_at")));
        try { p.setEmpresaNome(rs.getString("empresa_nome")); } catch (SQLException ignored) {}
        return p;
    }
    public Pgr save(Pgr p) throws SQLException {
        if (p.getId() == null) {
            long id = executeInsert(
                "INSERT INTO pgr (empresa_id, codigo, titulo, versao, data_elaboracao, data_validade, elaborador, elaborador_registro, status, observacoes, pdf_path) VALUES (?,?,?,?,?,?,?,?,?,?,?)",
                p.getEmpresaId(), p.getCodigo(), p.getTitulo(), p.getVersao(), p.getDataElaboracao(),
                p.getDataValidade(), p.getElaborador(), p.getElaboradorRegistro(), p.getStatus(), p.getObservacoes(), p.getPdfPath()
            );
            p.setId(id);
        } else {
            executeUpdate(
                "UPDATE pgr SET codigo=?, titulo=?, versao=?, data_elaboracao=?, data_validade=?, elaborador=?, elaborador_registro=?, status=?, observacoes=?, pdf_path=?, updated_at=datetime('now') WHERE id=?",
                p.getCodigo(), p.getTitulo(), p.getVersao(), p.getDataElaboracao(), p.getDataValidade(),
                p.getElaborador(), p.getElaboradorRegistro(), p.getStatus(), p.getObservacoes(), p.getPdfPath(), p.getId()
            );
        }
        return p;
    }
    public Optional<Pgr> findById(Long id) throws SQLException {
        return queryOne("SELECT p.*, e.razao_social as empresa_nome FROM pgr p LEFT JOIN empresa e ON p.empresa_id=e.id WHERE p.id=?", this::map, id);
    }
    public List<Pgr> findByEmpresa(Long empresaId) throws SQLException {
        return query("SELECT p.*, e.razao_social as empresa_nome FROM pgr p LEFT JOIN empresa e ON p.empresa_id=e.id WHERE p.empresa_id=? ORDER BY p.created_at DESC", this::map, empresaId);
    }
    public List<Pgr> findAll() throws SQLException {
        return query("SELECT p.*, e.razao_social as empresa_nome FROM pgr p LEFT JOIN empresa e ON p.empresa_id=e.id ORDER BY p.created_at DESC", this::map);
    }
    public List<Pgr> findVencendo(int dias) throws SQLException {
        return query("SELECT p.*, e.razao_social as empresa_nome FROM pgr p LEFT JOIN empresa e ON p.empresa_id=e.id WHERE p.data_validade BETWEEN date('now') AND date('now', ?) ORDER BY p.data_validade", this::map, "+" + dias + " days");
    }
    public List<Pgr> findVencidos() throws SQLException {
        return query("SELECT p.*, e.razao_social as empresa_nome FROM pgr p LEFT JOIN empresa e ON p.empresa_id=e.id WHERE p.data_validade < date('now') AND p.status != 'VENCIDO' ORDER BY p.data_validade", this::map);
    }
    public void delete(Long id) throws SQLException { executeUpdate("DELETE FROM pgr WHERE id=?", id); }
    private LocalDateTime parseDateTime(String s) { if (s == null) return null; return LocalDateTime.parse(s.replace(' ', 'T')); }
}
