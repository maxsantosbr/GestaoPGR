package com.gestaopgr.dao;

import com.gestaopgr.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class BaseDao {
    protected Connection getConnection() { return DatabaseConfig.getInstance().getConnection(); }

    protected void close(AutoCloseable... closeables) {
        for (AutoCloseable c : closeables) {
            if (c != null) try { c.close(); } catch (Exception ignored) {}
        }
    }

    protected long executeInsert(String sql, Object... params) throws SQLException {
        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setParams(ps, params);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        return -1;
    }

    protected int executeUpdate(String sql, Object... params) throws SQLException {
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            setParams(ps, params);
            return ps.executeUpdate();
        }
    }

    protected <T> List<T> query(String sql, RowMapper<T> mapper, Object... params) throws SQLException {
        List<T> result = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            setParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapper.map(rs));
            }
        }
        return result;
    }

    protected <T> Optional<T> queryOne(String sql, RowMapper<T> mapper, Object... params) throws SQLException {
        List<T> list = query(sql, mapper, params);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    protected void setParams(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            Object p = params[i];
            if (p == null) ps.setObject(i + 1, null);
            else if (p instanceof java.time.LocalDate ld) ps.setString(i + 1, ld.toString());
            else if (p instanceof java.time.LocalDateTime ldt) ps.setString(i + 1, ldt.toString().replace('T', ' '));
            else if (p instanceof Boolean b) ps.setInt(i + 1, b ? 1 : 0);
            else if (p instanceof Enum<?> e) ps.setString(i + 1, e.name());
            else ps.setObject(i + 1, p);
        }
    }

    @FunctionalInterface
    public interface RowMapper<T> { T map(ResultSet rs) throws SQLException; }
}
