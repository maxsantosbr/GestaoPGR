package com.gestaopgr.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConfig {
    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);
    private static final String DB_DIR = System.getProperty("user.home") + File.separator + "GestaoPGR";
    private static final String DB_FILE = DB_DIR + File.separator + "gestaopgr.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_FILE;

    private static DatabaseConfig instance;
    private Connection connection;

    private DatabaseConfig() {
        try {
            File dir = new File(DB_DIR);
            if (!dir.exists()) dir.mkdirs();

            // Verificar se o banco existente tem todas as tabelas necessárias
            File dbFile = new File(DB_FILE);
            if (dbFile.exists()) {
                if (!isSchemaComplete()) {
                    log.warn("Banco de dados incompleto. Recriando...");
                    dbFile.delete();
                }
            }

            connection = DriverManager.getConnection(DB_URL);
            connection.setAutoCommit(true);
            log.info("Banco de dados conectado: {}", DB_FILE);
            initSchema();
        } catch (SQLException e) {
            log.error("Erro ao conectar ao banco de dados", e);
            throw new RuntimeException("Não foi possível conectar ao banco de dados", e);
        }
    }

    private boolean isSchemaComplete() {
        String[] requiredTables = {"empresa", "setor", "cargo", "perigo", "risco", "plano_acao", "pgr", "alerta", "usuario", "tipo_servico", "proposta", "contrato", "fatura", "pagamento", "ppp", "configuracao"};
        try (Statement stmt = DriverManager.getConnection(DB_URL).createStatement()) {
            for (String table : requiredTables) {
                try (ResultSet rs = stmt.executeQuery("SELECT 1 FROM " + table + " LIMIT 1")) {
                    // Table exists
                } catch (SQLException e) {
                    return false; // Table doesn't exist
                }
            }
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public static synchronized DatabaseConfig getInstance() {
        if (instance == null) instance = new DatabaseConfig();
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            log.error("Erro ao obter conexão", e);
            throw new RuntimeException(e);
        }
        return connection;
    }

    private void initSchema() {
        try (Statement stmt = connection.createStatement()) {
            String schema = new String(
                getClass().getClassLoader().getResourceAsStream("schema.sql").readAllBytes(),
                java.nio.charset.StandardCharsets.UTF_8
            );
            StringBuilder currentStmt = new StringBuilder();
            for (String line : schema.split("\\n")) {
                String trimmed = line.trim();
                if (trimmed.startsWith("--") || trimmed.isEmpty()) {
                    continue;
                }
                currentStmt.append(line).append("\n");
                if (trimmed.endsWith(";")) {
                    String sql = currentStmt.toString().trim();
                    if (!sql.isEmpty()) {
                        try {
                            stmt.execute(sql);
                        } catch (SQLException e) {
                            if (!e.getMessage().contains("already exists")) {
                                log.warn("SQL warning: {} | SQL: {}", e.getMessage(), sql.substring(0, Math.min(80, sql.length())));
                            }
                        }
                    }
                    currentStmt.setLength(0);
                }
            }
            // Execute any remaining statement
            String remaining = currentStmt.toString().trim();
            if (!remaining.isEmpty()) {
                try {
                    stmt.execute(remaining);
                } catch (SQLException e) {
                    if (!e.getMessage().contains("already exists")) {
                        log.warn("SQL warning: {}", e.getMessage());
                    }
                }
            }
            log.info("Schema inicializado com sucesso");
        } catch (Exception e) {
            log.error("Erro ao inicializar schema", e);
            throw new RuntimeException("Erro ao inicializar banco de dados", e);
        }
    }

    public static String getDbPath() { return DB_FILE; }
}
