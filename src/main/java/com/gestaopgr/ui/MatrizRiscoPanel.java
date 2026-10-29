package com.gestaopgr.ui;

import com.gestaopgr.model.Risco;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public class MatrizRiscoPanel extends JPanel {
    private final MainFrame main;
    private JPanel matrizGrid;
    private JTable riscosTable;
    private DefaultTableModel riscosModel;

    public MatrizRiscoPanel(MainFrame main) {
        this.main = main;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Matriz de Risco (Probabilidade × Severidade)");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        // Painel superior: matriz 5x5
        matrizGrid = new JPanel(new GridLayout(6, 6, 2, 2));
        matrizGrid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header row
        matrizGrid.add(new JLabel("")); // top-left corner
        for (int s = 1; s <= 5; s++) {
            JLabel lbl = new JLabel("Sev " + s, SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            matrizGrid.add(lbl);
        }

        // Rows
        for (int p = 5; p >= 1; p--) {
            JLabel rowLbl = new JLabel("Prob " + p, SwingConstants.CENTER);
            rowLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            matrizGrid.add(rowLbl);

            for (int s = 1; s <= 5; s++) {
                int nivel = p * s;
                String cls;
                Color bg;
                if (nivel <= 4) { cls = "B"; bg = new Color(34, 197, 94); }
                else if (nivel <= 9) { cls = "M"; bg = new Color(249, 115, 22); }
                else if (nivel <= 16) { cls = "A"; bg = new Color(239, 68, 68); }
                else { cls = "C"; bg = new Color(127, 29, 29); }

                JLabel cell = new JLabel(cls + " (" + nivel + ")", SwingConstants.CENTER);
                cell.setOpaque(true);
                cell.setBackground(bg);
                cell.setForeground(Color.WHITE);
                cell.setFont(new Font("Segoe UI", Font.BOLD, 11));
                cell.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
                matrizGrid.add(cell);
            }
        }

        JPanel matrizCard = new JPanel(new BorderLayout());
        matrizCard.setBackground(Color.WHITE);
        matrizCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        matrizCard.add(matrizGrid, BorderLayout.CENTER);

        // Legenda
        JPanel legenda = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        legenda.setOpaque(false);
        addLegenda(legenda, "B = Baixo (1-4)", new Color(34, 197, 94));
        addLegenda(legenda, "M = Médio (5-9)", new Color(249, 115, 22));
        addLegenda(legenda, "A = Alto (10-16)", new Color(239, 68, 68));
        addLegenda(legenda, "C = Crítico (17-25)", new Color(127, 29, 29));
        matrizCard.add(legenda, BorderLayout.SOUTH);

        add(matrizCard, BorderLayout.NORTH);

        // Tabela de riscos
        String[] cols = {"ID", "Empresa", "Setor", "Risco", "Categoria", "Prob.", "Sev.", "Nível", "Classificação"};
        riscosModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        riscosTable = new JTable(riscosModel);
        riscosTable.setRowHeight(24);
        riscosTable.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        riscosTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));

        riscosTable.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (v != null) {
                    switch (v.toString()) {
                        case "BAIXO": comp.setBackground(new Color(220, 252, 231)); break;
                        case "MEDIO": comp.setBackground(new Color(254, 243, 199)); break;
                        case "ALTO": comp.setBackground(new Color(254, 226, 226)); break;
                        case "CRITICO": comp.setBackground(new Color(252, 165, 165)); break;
                        default: comp.setBackground(Color.WHITE);
                    }
                    comp.setForeground(Color.BLACK);
                }
                return comp;
            }
        });

        add(new JScrollPane(riscosTable), BorderLayout.CENTER);

        carregarDados();
    }

    private void addLegenda(JPanel panel, String text, Color color) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        item.setOpaque(false);
        JLabel box = new JLabel("  ");
        box.setOpaque(true);
        box.setBackground(color);
        box.setPreferredSize(new Dimension(16, 16));
        item.add(box);
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        item.add(lbl);
        panel.add(item);
    }

    private void carregarDados() {
        riscosModel.setRowCount(0);
        try {
            for (Risco r : main.riscoDao.findAll()) {
                riscosModel.addRow(new Object[]{
                    r.getId(), r.getEmpresaNome(), r.getSetorNome(), r.getPerigoDescricao(),
                    r.getCategoria(), r.getProbabilidade(), r.getSeveridade(),
                    r.getNivelRisco(), r.getClassificacao()
                });
            }
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage()); }
    }
}
