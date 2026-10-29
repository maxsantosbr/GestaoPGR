package com.gestaopgr.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardPanel extends JPanel {
    private final MainFrame main;

    public DashboardPanel(MainFrame main) {
        this.main = main;
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(248, 250, 252));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        JLabel title = new JLabel("Dashboard - Visao Geral");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(30, 41, 59));
        titlePanel.add(title, BorderLayout.WEST);
        JLabel subtitle = new JLabel("Visao geral da conformidade dos PGRs e servicos");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(new Color(100, 116, 139));
        titlePanel.add(subtitle, BorderLayout.SOUTH);
        add(titlePanel, BorderLayout.NORTH);

        // KPI cards
        JPanel kpiPanel = new JPanel(new GridLayout(1, 5, 15, 0));
        kpiPanel.setOpaque(false);
        kpiPanel.setPreferredSize(new Dimension(0, 110));

        int numEmpresas = 0, numPgrs = 0, numVencendo = 0, numVencidos = 0, numRiscosCriticos = 0;
        try {
            numEmpresas = main.empresaDao.findAtivas().size();
            numPgrs = main.pgrDao.findAll().size();
            numVencendo = main.pgrDao.findVencendo(30).size();
            numVencidos = main.pgrDao.findVencidos().size();
            for (com.gestaopgr.model.Risco r : main.riscoDao.findAll()) {
                if ("CRITICO".equals(r.getClassificacao())) numRiscosCriticos++;
            }
        } catch (Exception ignored) {}

        kpiPanel.add(createKpi("Empresas", String.valueOf(numEmpresas), "cadastradas", new Color(59, 130, 246), "E"));
        kpiPanel.add(createKpi("PGRs", String.valueOf(numPgrs), "elaborados", new Color(34, 197, 94), "P"));
        kpiPanel.add(createKpi("Vencendo", String.valueOf(numVencendo), "em 30 dias", new Color(249, 115, 22), "V"));
        kpiPanel.add(createKpi("Vencidos", String.valueOf(numVencidos), "atrasados", new Color(239, 68, 68), "X"));
        kpiPanel.add(createKpi("Riscos Criticos", String.valueOf(numRiscosCriticos), "identificados", new Color(168, 85, 247), "!"));

        JPanel topPanel = new JPanel(new BorderLayout(0, 15));
        topPanel.setOpaque(false);
        topPanel.add(kpiPanel, BorderLayout.NORTH);

        // Tabela de empresas
        JLabel lblEmpresas = new JLabel("Empresas e Status do PGR");
        lblEmpresas.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblEmpresas.setForeground(new Color(30, 41, 59));
        topPanel.add(lblEmpresas, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        String[] cols = {"Empresa", "CNAE", "Empregados", "PGR", "Validade", "Status"};
        Object[][] data = new Object[0][0];
        try {
            java.util.List<com.gestaopgr.model.Empresa> empresas = main.empresaDao.findAtivas();
            data = new Object[empresas.size()][6];
            for (int i = 0; i < empresas.size(); i++) {
                com.gestaopgr.model.Empresa e = empresas.get(i);
                java.util.List<com.gestaopgr.model.Pgr> pgrs = main.pgrDao.findByEmpresa(e.getId());
                com.gestaopgr.model.Pgr pgr = pgrs.isEmpty() ? null : pgrs.get(0);
                data[i][0] = e.getRazaoSocial();
                data[i][1] = e.getCnae();
                data[i][2] = e.getNumEmpregados();
                data[i][3] = pgr != null ? pgr.getCodigo() : "Sem PGR";
                data[i][4] = pgr != null ? pgr.getDataValidade().toString() : "-";
                data[i][5] = pgr != null ? pgr.getStatus() : "PENDENTE";
            }
        } catch (Exception ignored) {}

        JTable table = new JTable(data, cols);
        table.setRowHeight(26);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Ações rápidas
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionsPanel.setOpaque(false);
        JButton btnNovoPGR = new JButton("Novo PGR");
        btnNovoPGR.addActionListener(e -> main.showCard("PGR"));
        JButton btnNovaEmpresa = new JButton("Nova Empresa");
        btnNovaEmpresa.addActionListener(e -> main.showCard("EMPRESAS"));
        JButton btnProposta = new JButton("Nova Proposta");
        btnProposta.addActionListener(e -> main.showCard("PROPOSTAS"));
        actionsPanel.add(btnNovoPGR);
        actionsPanel.add(btnNovaEmpresa);
        actionsPanel.add(btnProposta);
        add(actionsPanel, BorderLayout.SOUTH);
    }

    private JPanel createKpi(String title, String value, String subtitle, Color color, String icon) {
        JPanel card = new JPanel(new BorderLayout(8, 4));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            new EmptyBorder(12, 15, 12, 15)
        ));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        top.setOpaque(false);
        JLabel il = new JLabel(icon); il.setFont(new Font("Segoe UI", Font.BOLD, 18)); il.setForeground(color);
        top.add(il);
        JLabel tl = new JLabel(title); tl.setFont(new Font("Segoe UI", Font.PLAIN, 12)); tl.setForeground(new Color(100, 116, 139));
        top.add(tl);
        card.add(top, BorderLayout.NORTH);
        JLabel vl = new JLabel(value); vl.setFont(new Font("Segoe UI", Font.BOLD, 32)); vl.setForeground(color);
        card.add(vl, BorderLayout.CENTER);
        JLabel sl = new JLabel(subtitle); sl.setFont(new Font("Segoe UI", Font.PLAIN, 11)); sl.setForeground(new Color(100, 116, 139));
        card.add(sl, BorderLayout.SOUTH);
        return card;
    }
}

