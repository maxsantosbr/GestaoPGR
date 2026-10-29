package com.gestaopgr.ui;

import com.gestaopgr.model.Empresa;
import com.gestaopgr.model.Setor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SetorPanel extends JPanel {
    private final MainFrame main;
    private JTable table;
    private DefaultTableModel tableModel;

    public SetorPanel(MainFrame main) {
        this.main = main;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Setores / Ambientes");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        topPanel.add(title, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnNovo = new JButton("Novo Setor");
        btnNovo.addActionListener(e -> abrirFormulario(null));
        JButton btnEditar = new JButton("Editar");
        btnEditar.addActionListener(e -> editarSelecionado());
        JButton btnExcluir = new JButton("Excluir");
        btnExcluir.addActionListener(e -> excluirSelecionado());
        btnPanel.add(btnNovo); btnPanel.add(btnEditar); btnPanel.add(btnExcluir);
        topPanel.add(btnPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        String[] cols = {"ID", "Empresa", "Setor", "Descrição", "Nº Trabalhadores", "Ativo"};
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
            for (Setor s : main.setorDao.findAll()) {
                tableModel.addRow(new Object[]{
                    s.getId(), s.getEmpresaNome(), s.getNome(), s.getDescricao(),
                    s.getNumTrabalhadores(), s.isAtivo() ? "Sim" : "Não"
                });
            }
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage()); }
    }

    private void abrirFormulario(Setor setor) {
        SetorFormDialog d = new SetorFormDialog(main, setor);
        d.setVisible(true);
        if (d.isSaved()) carregarDados();
    }

    private void editarSelecionado() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione um setor."); return; }
        Long id = (Long) tableModel.getValueAt(row, 0);
        try {
            Setor s = main.setorDao.findById(id).orElse(null);
            if (s != null) abrirFormulario(s);
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage()); }
    }

    private void excluirSelecionado() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione um setor."); return; }
        if (JOptionPane.showConfirmDialog(this, "Excluir setor?", "Confirmar", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        Long id = (Long) tableModel.getValueAt(row, 0);
        try { main.setorDao.delete(id); carregarDados(); }
        catch (Exception e) { JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage()); }
    }
}

class SetorFormDialog extends JDialog {
    private boolean saved = false;
    private final MainFrame main;
    private Setor setor;
    private JComboBox<String> cbEmpresa;
    private JTextField txtNome, txtDescricao, txtNumTrab;

    public SetorFormDialog(MainFrame main, Setor setor) {
        super(main, "Setor / Ambiente", true);
        this.main = main; this.setor = setor;
        setSize(420, 280); setLocationRelativeTo(main);
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cbEmpresa = new JComboBox<>();
        txtNome = new JTextField(20); txtDescricao = new JTextField(20); txtNumTrab = new JTextField(20);

        try {
            for (Empresa e : main.empresaDao.findAtivas())
                cbEmpresa.addItem(e.getId() + " - " + e.getRazaoSocial());
        } catch (Exception ignored) {}

        int row = 0;
        addField(form, gbc, row++, "Empresa:*", cbEmpresa);
        addField(form, gbc, row++, "Nome:*", txtNome);
        addField(form, gbc, row++, "Descrição:", txtDescricao);
        addField(form, gbc, row++, "Nº Trabalhadores:", txtNumTrab);

        if (setor != null) {
            txtNome.setText(setor.getNome());
            txtDescricao.setText(setor.getDescricao());
            txtNumTrab.setText(String.valueOf(setor.getNumTrabalhadores()));
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
            if (cbEmpresa.getSelectedItem() == null || txtNome.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Empresa e Nome são obrigatórios."); return;
            }
            Long empresaId = Long.parseLong(((String) cbEmpresa.getSelectedItem()).split(" - ")[0]);
            Setor s = setor != null ? setor : new Setor();
            s.setEmpresaId(empresaId);
            s.setNome(txtNome.getText().trim());
            s.setDescricao(txtDescricao.getText().trim());
            if (!txtNumTrab.getText().trim().isEmpty())
                s.setNumTrabalhadores(Integer.parseInt(txtNumTrab.getText().trim()));
            s.setAtivo(true);

            main.setorDao.save(s);
            saved = true;
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() { return saved; }
}

