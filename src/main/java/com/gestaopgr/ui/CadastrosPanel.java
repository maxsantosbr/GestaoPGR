package com.gestaopgr.ui;

import javax.swing.*;
import java.awt.*;

public class CadastrosPanel extends JPanel {
    private final MainFrame main;

    public CadastrosPanel(MainFrame main) {
        this.main = main;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Cadastros Auxiliares");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        JPanel btnPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        String[] cadastros = {"Cargos", "Perigos (Biblioteca)", "Formas de Pagamento"};
        for (String c : cadastros) {
            JButton btn = new JButton(c);
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            btn.addActionListener(e -> abrirCadastro(c));
            btnPanel.add(btn);
        }

        add(btnPanel, BorderLayout.CENTER);
    }

    private void abrirCadastro(String tipo) {
        try {
            if (tipo.equals("Cargos")) {
                gerenciarCargo();
            } else if (tipo.equals("Perigos (Biblioteca)")) {
                gerenciarPerigo();
            } else if (tipo.equals("Formas de Pagamento")) {
                JOptionPane.showMessageDialog(this, "Formas de pagamento já cadastradas no banco de dados.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void gerenciarCargo() {
        try {
            StringBuilder sb = new StringBuilder("Cargos cadastrados:\n");
            for (com.gestaopgr.model.Cargo c : main.cargoDao.findAll()) {
                sb.append(c.getId()).append(" - ").append(c.getNome()).append("\n");
            }
            String nome = JOptionPane.showInputDialog(this, sb + "\nNovo cargo (deixe vazio para cancelar):");
            if (nome != null && !nome.trim().isEmpty()) {
                com.gestaopgr.model.Cargo c = new com.gestaopgr.model.Cargo();
                c.setNome(nome.trim());
                c.setAtivo(true);
                main.cargoDao.save(c);
                JOptionPane.showMessageDialog(this, "Cargo cadastrado.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void gerenciarPerigo() {
        try {
            StringBuilder sb = new StringBuilder("Perigos cadastrados:\n");
            for (com.gestaopgr.model.Perigo p : main.perigoDao.findAll()) {
                sb.append(p.getId()).append(" - ").append(p.getNome()).append(" (").append(p.getCategoria()).append(")\n");
            }
            JOptionPane.showMessageDialog(this, sb.toString(), "Biblioteca de Perigos", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
