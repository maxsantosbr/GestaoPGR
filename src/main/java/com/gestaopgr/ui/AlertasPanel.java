package com.gestaopgr.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AlertasPanel extends JPanel {
    private final MainFrame main;
    private JTable table;
    private DefaultTableModel tableModel;

    public AlertasPanel(MainFrame main) {
        this.main = main;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Alertas");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        topPanel.add(title, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnGerar = new JButton("Gerar Alertas");
        btnGerar.addActionListener(e -> gerarAlertas());
        JButton btnMarcar = new JButton("Marcar Todos como Lidos");
        btnMarcar.addActionListener(e -> marcarTodosLidos());
        btnPanel.add(btnGerar); btnPanel.add(btnMarcar);
        topPanel.add(btnPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        String[] cols = {"ID", "Tipo", "Nível", "Mensagem", "Data", "Lido"};
        tableModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        table = new JTable(tableModel);
        table.setRowHeight(26);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        add(new JScrollPane(table), BorderLayout.CENTER);

        carregarDados();
    }

    private void carregarDados() {
        tableModel.setRowCount(0);
        try {
            java.sql.Connection conn = com.gestaopgr.config.DatabaseConfig.getInstance().getConnection();
            java.sql.ResultSet rs = conn.createStatement().executeQuery(
                "SELECT * FROM alerta ORDER BY data_criacao DESC");
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getLong("id"), rs.getString("tipo"), rs.getString("nivel"),
                    rs.getString("mensagem"), rs.getString("data_criacao"),
                    rs.getInt("lido") == 1 ? "Sim" : "Não"
                });
            }
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage()); }
    }

    private void gerarAlertas() {
        try {
            int count = 0;
            java.sql.Connection conn = com.gestaopgr.config.DatabaseConfig.getInstance().getConnection();

            // PGRs vencendo em 30 dias
            for (com.gestaopgr.model.Pgr p : main.pgrDao.findVencendo(30)) {
                String msg = "PGR vencendo em breve: " + p.getEmpresaNome() + " - " + p.getTitulo() + " (vence em " + p.getDataValidade() + ")";
                try (java.sql.PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO alerta (empresa_id, tipo, mensagem, nivel) VALUES (?,?,?,?)")) {
                    ps.setLong(1, p.getEmpresaId()); ps.setString(2, "PGR_VENCENDO");
                    ps.setString(3, msg); ps.setString(4, "ATENCAO");
                    ps.executeUpdate();
                }
                count++;
            }

            // PGRs vencidos
            for (com.gestaopgr.model.Pgr p : main.pgrDao.findVencidos()) {
                String msg = "PGR VENCIDO: " + p.getEmpresaNome() + " - " + p.getTitulo() + " (venceu em " + p.getDataValidade() + ")";
                try (java.sql.PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO alerta (empresa_id, tipo, mensagem, nivel) VALUES (?,?,?,?)")) {
                    ps.setLong(1, p.getEmpresaId()); ps.setString(2, "PGR_VENCIDO");
                    ps.setString(3, msg); ps.setString(4, "CRITICO");
                    ps.executeUpdate();
                }
                count++;
            }

            // Ações atrasadas
            for (com.gestaopgr.model.PlanoAcao pa : main.planoAcaoDao.findAtrasados()) {
                String msg = "Acao atrasada: " + pa.getAcao() + " (prazo: " + pa.getPrazo() + ")";
                try (java.sql.PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO alerta (empresa_id, tipo, mensagem, nivel) VALUES (?,?,?,?)")) {
                    ps.setLong(1, pa.getEmpresaId()); ps.setString(2, "ACAO_ATRASADA");
                    ps.setString(3, msg); ps.setString(4, "CRITICO");
                    ps.executeUpdate();
                }
                count++;
            }

            // Riscos críticos
            for (com.gestaopgr.model.Risco r : main.riscoDao.findAll()) {
                if ("CRITICO".equals(r.getClassificacao())) {
                    String msg = "Risco critico identificado: " + r.getPerigoDescricao() + " (" + r.getEmpresaNome() + ")";
                    try (java.sql.PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO alerta (empresa_id, tipo, mensagem, nivel) VALUES (?,?,?,?)")) {
                        ps.setLong(1, r.getEmpresaId()); ps.setString(2, "RISCO_CRITICO");
                        ps.setString(3, msg); ps.setString(4, "CRITICO");
                        ps.executeUpdate();
                    }
                    count++;
                }
            }

            JOptionPane.showMessageDialog(this, count + " alertas gerados.");
            carregarDados();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void marcarTodosLidos() {
        try {
            java.sql.Connection conn = com.gestaopgr.config.DatabaseConfig.getInstance().getConnection();
            conn.createStatement().executeUpdate("UPDATE alerta SET lido=1");
            carregarDados();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage()); }
    }
}
