package com.gestaopgr.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class FinanceiroDashboardPanel extends JPanel {
    public FinanceiroDashboardPanel(MainFrame main) {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(248, 250, 252));

        JLabel title = new JLabel("Dashboard Financeiro");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(30, 41, 59));
        add(title, BorderLayout.NORTH);

        // KPI cards
        JPanel kpiPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        kpiPanel.setOpaque(false);
        kpiPanel.setPreferredSize(new Dimension(0, 110));

        kpiPanel.add(createKpi("Receita Total", "R$ 15.420,00", new Color(34, 197, 94), "💰"));
        kpiPanel.add(createKpi("A Receber", "R$ 4.850,00", new Color(249, 115, 22), "📥"));
        kpiPanel.add(createKpi("Vencido", "R$ 1.200,00", new Color(239, 68, 68), "⚠️"));
        kpiPanel.add(createKpi("Contratos Ativos", "8", new Color(59, 130, 246), "📜"));

        add(kpiPanel, BorderLayout.NORTH);

        // Info
        JPanel info = new JPanel(new GridBagLayout());
        info.setOpaque(false);
        JLabel lbl = new JLabel("Módulo financeiro em desenvolvimento. Use as telas de Propostas, Contratos, Faturas e Pagamentos.");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setForeground(new Color(100, 116, 139));
        info.add(lbl);
        add(info, BorderLayout.CENTER);
    }

    private JPanel createKpi(String title, String value, Color color, String icon) {
        JPanel card = new JPanel(new BorderLayout(8, 4));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            new EmptyBorder(12, 15, 12, 15)
        ));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        top.setOpaque(false);
        JLabel il = new JLabel(icon); il.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        top.add(il);
        JLabel tl = new JLabel(title); tl.setFont(new Font("Segoe UI", Font.PLAIN, 12)); tl.setForeground(new Color(100, 116, 139));
        top.add(tl);
        card.add(top, BorderLayout.NORTH);
        JLabel vl = new JLabel(value); vl.setFont(new Font("Segoe UI", Font.BOLD, 24)); vl.setForeground(color);
        card.add(vl, BorderLayout.CENTER);
        return card;
    }
}
