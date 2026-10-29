package com.gestaopgr.ui;

import com.gestaopgr.model.TipoServico;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TipoServicoPanel extends JPanel {
    private final MainFrame main;
    private JTable table;
    private DefaultTableModel tableModel;

    public TipoServicoPanel(MainFrame main) {
        this.main = main;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Tipos de Serviço e Preços");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        topPanel.add(title, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnNovo = new JButton("Novo Serviço");
        btnNovo.addActionListener(e -> abrirFormulario(null));
        JButton btnEditar = new JButton("Editar");
        btnEditar.addActionListener(e -> editarSelecionado());
        JButton btnExcluir = new JButton("Excluir");
        btnExcluir.addActionListener(e -> excluirSelecionado());
        btnPanel.add(btnNovo); btnPanel.add(btnEditar); btnPanel.add(btnExcluir);
        topPanel.add(btnPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        String[] cols = {"ID", "Serviço", "Descrição", "Preço Base", "Unidade", "Ativo"};
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
            for (TipoServico t : main.tipoServicoDao.findAll()) {
                tableModel.addRow(new Object[]{
                    t.getId(), t.getNome(), t.getDescricao(),
                    String.format("R$ %.2f", t.getPrecoBase()), t.getUnidade(), t.isAtivo() ? "Sim" : "Não"
                });
            }
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage()); }
    }

    private void abrirFormulario(TipoServico ts) {
        TipoServicoFormDialog d = new TipoServicoFormDialog(main, ts);
        d.setVisible(true);
        if (d.isSaved()) carregarDados();
    }

    private void editarSelecionado() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione um serviço."); return; }
        Long id = (Long) tableModel.getValueAt(row, 0);
        try {
            TipoServico t = main.tipoServicoDao.findById(id).orElse(null);
            if (t != null) abrirFormulario(t);
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage()); }
    }

    private void excluirSelecionado() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione um serviço."); return; }
        if (JOptionPane.showConfirmDialog(this, "Excluir serviço?", "Confirmar", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        Long id = (Long) tableModel.getValueAt(row, 0);
        try { main.tipoServicoDao.delete(id); carregarDados(); }
        catch (Exception e) { JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage()); }
    }
}

class TipoServicoFormDialog extends JDialog {
    private boolean saved = false;
    private final MainFrame main;
    private TipoServico ts;
    private JTextField txtNome, txtDescricao, txtPreco, txtUnidade;

    public TipoServicoFormDialog(MainFrame main, TipoServico ts) {
        super(main, "Tipo de Serviço", true);
        this.main = main; this.ts = ts;
        setSize(420, 280); setLocationRelativeTo(main);
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtNome = new JTextField(20); txtDescricao = new JTextField(20);
        txtPreco = new JTextField(20); txtUnidade = new JTextField(20);

        int row = 0;
        addField(form, gbc, row++, "Nome:*", txtNome);
        addField(form, gbc, row++, "Descrição:", txtDescricao);
        addField(form, gbc, row++, "Preço Base (R$):*", txtPreco);
        addField(form, gbc, row++, "Unidade:", txtUnidade);

        if (ts != null) {
            txtNome.setText(ts.getNome());
            txtDescricao.setText(ts.getDescricao());
            txtPreco.setText(String.valueOf(ts.getPrecoBase()));
            txtUnidade.setText(ts.getUnidade());
        } else {
            txtUnidade.setText("EMPRESA");
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
            if (txtNome.getText().trim().isEmpty() || txtPreco.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nome e Preço são obrigatórios."); return;
            }
            TipoServico t = ts != null ? ts : new TipoServico();
            t.setNome(txtNome.getText().trim());
            t.setDescricao(txtDescricao.getText().trim());
            t.setPrecoBase(Double.parseDouble(txtPreco.getText().trim().replace(",", ".")));
            t.setUnidade(txtUnidade.getText().trim().isEmpty() ? "UN" : txtUnidade.getText().trim());
            t.setAtivo(true);

            main.tipoServicoDao.save(t);
            saved = true;
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() { return saved; }
}

