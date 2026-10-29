package com.gestaopgr.ui;

import com.gestaopgr.model.PlanoAcao;
import com.gestaopgr.model.Risco;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class PlanoAcaoPanel extends JPanel {
    private final MainFrame main;
    private JTable table;
    private DefaultTableModel tableModel;

    public PlanoAcaoPanel(MainFrame main) {
        this.main = main;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Plano de Ação");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        topPanel.add(title, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnNovo = new JButton("Nova Ação");
        btnNovo.addActionListener(e -> abrirFormulario(null));
        JButton btnEditar = new JButton("Editar");
        btnEditar.addActionListener(e -> editarSelecionado());
        JButton btnExcluir = new JButton("Excluir");
        btnExcluir.addActionListener(e -> excluirSelecionado());
        btnPanel.add(btnNovo); btnPanel.add(btnEditar); btnPanel.add(btnExcluir);
        topPanel.add(btnPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        String[] cols = {"ID", "Risco", "Ação", "Tipo", "Responsável", "Prazo", "Custo", "Status"};
        tableModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        table = new JTable(tableModel);
        table.setRowHeight(26);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        add(new JScrollPane(table), BorderLayout.CENTER);

        carregarDados();
    }

    private void carregarDados() {
        tableModel.setRowCount(0);
        try {
            for (PlanoAcao pa : main.planoAcaoDao.findAll()) {
                tableModel.addRow(new Object[]{
                    pa.getId(), pa.getRiscoDescricao(), pa.getAcao(), pa.getTipo(),
                    pa.getResponsavel(), pa.getPrazo(), pa.getCustoEstimado(), pa.getStatus()
                });
            }
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage()); }
    }

    private void abrirFormulario(PlanoAcao pa) {
        PlanoAcaoFormDialog d = new PlanoAcaoFormDialog(main, pa);
        d.setVisible(true);
        if (d.isSaved()) carregarDados();
    }

    private void editarSelecionado() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione uma ação."); return; }
        Long id = (Long) tableModel.getValueAt(row, 0);
        // Simplified: just reopen
        JOptionPane.showMessageDialog(this, "Edição em desenvolvimento.");
    }

    private void excluirSelecionado() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione uma ação."); return; }
        if (JOptionPane.showConfirmDialog(this, "Excluir ação?", "Confirmar", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        Long id = (Long) tableModel.getValueAt(row, 0);
        try { main.planoAcaoDao.delete(id); carregarDados(); }
        catch (Exception e) { JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage()); }
    }
}

class PlanoAcaoFormDialog extends JDialog {
    private boolean saved = false;
    private final MainFrame main;
    private PlanoAcao pa;
    private JComboBox<String> cbRisco, cbTipo, cbStatus;
    private JTextField txtAcao, txtResponsavel, txtPrazo, txtCusto, txtObs;

    public PlanoAcaoFormDialog(MainFrame main, PlanoAcao pa) {
        super(main, "Plano de Ação", true);
        this.main = main; this.pa = pa;
        setSize(500, 400); setLocationRelativeTo(main);
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cbRisco = new JComboBox<>(); cbTipo = new JComboBox<>(PlanoAcao.TIPOS); cbStatus = new JComboBox<>(PlanoAcao.STATUS);
        txtAcao = new JTextField(25); txtResponsavel = new JTextField(25);
        txtPrazo = new JTextField(25); txtCusto = new JTextField(25); txtObs = new JTextField(25);

        try {
            for (com.gestaopgr.model.Risco r : main.riscoDao.findAll())
                cbRisco.addItem(r.getId() + " - " + r.getPerigoDescricao());
        } catch (Exception ignored) {}

        int row = 0;
        addField(form, gbc, row++, "Risco:*", cbRisco);
        addField(form, gbc, row++, "Ação:*", txtAcao);
        addField(form, gbc, row++, "Tipo:", cbTipo);
        addField(form, gbc, row++, "Responsável:", txtResponsavel);
        addField(form, gbc, row++, "Prazo (yyyy-MM-dd):", txtPrazo);
        addField(form, gbc, row++, "Custo (R$):", txtCusto);
        addField(form, gbc, row++, "Status:", cbStatus);
        addField(form, gbc, row++, "Observação:", txtObs);

        if (pa != null) {
            txtAcao.setText(pa.getAcao());
            if (pa.getTipo() != null) cbTipo.setSelectedItem(pa.getTipo());
            txtResponsavel.setText(pa.getResponsavel());
            if (pa.getPrazo() != null) txtPrazo.setText(pa.getPrazo().toString());
            if (pa.getCustoEstimado() != null) txtCusto.setText(String.valueOf(pa.getCustoEstimado()));
            if (pa.getStatus() != null) cbStatus.setSelectedItem(pa.getStatus());
            txtObs.setText(pa.getObservacao());
        }

        add(form, BorderLayout.CENTER);
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.addActionListener(e -> salvar());
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dispose());
        btns.add(btnSalvar); btns.add(btnCancelar);
        add(btns, BorderLayout.SOUTH);
    }

    private void addField(JPanel form, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        form.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        form.add(field, gbc);
    }

    private void salvar() {
        try {
            if (cbRisco.getSelectedItem() == null || txtAcao.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Risco e Ação são obrigatórios."); return;
            }
            Long riscoId = Long.parseLong(((String) cbRisco.getSelectedItem()).split(" - ")[0]);
            PlanoAcao pa = this.pa != null ? this.pa : new PlanoAcao();
            pa.setRiscoId(riscoId);
            pa.setAcao(txtAcao.getText().trim());
            pa.setTipo((String) cbTipo.getSelectedItem());
            pa.setResponsavel(txtResponsavel.getText().trim());
            if (!txtPrazo.getText().trim().isEmpty()) pa.setPrazo(LocalDate.parse(txtPrazo.getText().trim()));
            if (!txtCusto.getText().trim().isEmpty()) pa.setCustoEstimado(Double.parseDouble(txtCusto.getText().trim().replace(",", ".")));
            pa.setStatus((String) cbStatus.getSelectedItem());
            pa.setObservacao(txtObs.getText().trim());

            main.planoAcaoDao.save(pa);
            saved = true;
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() { return saved; }
}

