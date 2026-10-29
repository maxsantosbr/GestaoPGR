package com.gestaopgr.ui;

import com.gestaopgr.model.Empresa;
import com.gestaopgr.model.Proposta;
import com.gestaopgr.model.PropostaItem;
import com.gestaopgr.model.TipoServico;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class PropostaPanel extends JPanel {
    private final MainFrame main;
    private JTable table;
    private DefaultTableModel tableModel;

    public PropostaPanel(MainFrame main) {
        this.main = main;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Propostas / Orçamentos");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        topPanel.add(title, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnNova = new JButton("Nova Proposta");
        btnNova.addActionListener(e -> abrirFormulario(null));
        JButton btnEditar = new JButton("Editar");
        btnEditar.addActionListener(e -> editarSelecionado());
        JButton btnExcluir = new JButton("Excluir");
        btnExcluir.addActionListener(e -> excluirSelecionado());
        btnPanel.add(btnNova); btnPanel.add(btnEditar); btnPanel.add(btnExcluir);
        topPanel.add(btnPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        String[] cols = {"ID", "Código", "Empresa", "Título", "Valor Final", "Status", "Validade"};
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
            for (Proposta p : main.propostaDao.findAll()) {
                tableModel.addRow(new Object[]{
                    p.getId(), p.getCodigo(), p.getEmpresaNome(), p.getTitulo(),
                    String.format("R$ %.2f", p.getValorFinal()), p.getStatus(),
                    p.getDataValidade() != null ? p.getDataValidade().toString() : ""
                });
            }
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage()); }
    }

    private void abrirFormulario(Proposta proposta) {
        PropostaFormDialog d = new PropostaFormDialog(main, proposta);
        d.setVisible(true);
        if (d.isSaved()) carregarDados();
    }

    private void editarSelecionado() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione uma proposta."); return; }
        Long id = (Long) tableModel.getValueAt(row, 0);
        try {
            Proposta p = main.propostaDao.findById(id).orElse(null);
            if (p != null) abrirFormulario(p);
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage()); }
    }

    private void excluirSelecionado() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione uma proposta."); return; }
        if (JOptionPane.showConfirmDialog(this, "Excluir proposta?", "Confirmar", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        Long id = (Long) tableModel.getValueAt(row, 0);
        try { main.propostaDao.delete(id); carregarDados(); }
        catch (Exception e) { JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage()); }
    }
}

class PropostaFormDialog extends JDialog {
    private boolean saved = false;
    private final MainFrame main;
    private Proposta proposta;
    private JComboBox<String> cbEmpresa;
    private JTextField txtTitulo, txtDescricao, txtDesconto, txtCondicoes, txtValidade;
    private JLabel lblTotal, lblFinal;
    private DefaultTableModel itensModel;
    private JTable itensTable;
    private List<TipoServico> tiposServico;

    public PropostaFormDialog(MainFrame main, Proposta proposta) {
        super(main, "Proposta / Orçamento", true);
        this.main = main; this.proposta = proposta;
        setSize(750, 600); setLocationRelativeTo(main);
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cbEmpresa = new JComboBox<>();
        txtTitulo = new JTextField(30); txtDescricao = new JTextField(30);
        txtDesconto = new JTextField(10); txtCondicoes = new JTextField(30);
        txtValidade = new JTextField(10);
        lblTotal = new JLabel("R$ 0.00"); lblFinal = new JLabel("R$ 0.00");

        try {
            for (Empresa e : main.empresaDao.findAtivas())
                cbEmpresa.addItem(e.getId() + " - " + e.getRazaoSocial());
            tiposServico = main.tipoServicoDao.findAll();
        } catch (Exception ignored) {}

        int row = 0;
        addField(form, gbc, row++, "Empresa:*", cbEmpresa);
        addField(form, gbc, row++, "Título:*", txtTitulo);
        addField(form, gbc, row++, "Descrição:", txtDescricao);
        addField(form, gbc, row++, "Desconto (R$):", txtDesconto);
        addField(form, gbc, row++, "Validade (dias):", txtValidade);
        addField(form, gbc, row++, "Condições Pgto:", txtCondicoes);

        if (proposta != null) {
            txtTitulo.setText(proposta.getTitulo());
            txtDescricao.setText(proposta.getDescricao());
            txtDesconto.setText(String.valueOf(proposta.getDesconto()));
            txtCondicoes.setText(proposta.getCondicoesPagamento());
            if (proposta.getDataValidade() != null) txtValidade.setText(String.valueOf(java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), proposta.getDataValidade())));
        } else {
            txtDesconto.setText("0");
            txtValidade.setText("30");
        }

        add(form, BorderLayout.NORTH);

        // Tabela de itens
        JPanel itensPanel = new JPanel(new BorderLayout(5, 5));
        itensPanel.setBorder(BorderFactory.createTitledBorder("Itens da Proposta"));

        String[] cols = {"#", "Serviço", "Descrição", "Qtd", "Valor Unit.", "Valor Total"};
        itensModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        itensTable = new JTable(itensModel);
        itensTable.setRowHeight(24);
        itensTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        itensPanel.add(new JScrollPane(itensTable), BorderLayout.CENTER);

        JPanel itemBtns = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAddItem = new JButton("Adicionar Item");
        btnAddItem.addActionListener(e -> adicionarItem());
        JButton btnRemItem = new JButton("Remover Item");
        btnRemItem.addActionListener(e -> removerItem());
        itemBtns.add(btnAddItem); itemBtns.add(btnRemItem);
        itensPanel.add(itemBtns, BorderLayout.SOUTH);

        add(itensPanel, BorderLayout.CENTER);

        // Totais
        JPanel totaisPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        totaisPanel.add(new JLabel("Total:"));
        totaisPanel.add(lblTotal);
        totaisPanel.add(new JLabel("  Desconto:"));
        totaisPanel.add(new JLabel(txtDesconto.getText()));
        totaisPanel.add(new JLabel("  Valor Final:"));
        lblFinal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totaisPanel.add(lblFinal);
        add(totaisPanel, BorderLayout.SOUTH);

        // Carregar itens existentes
        if (proposta != null) {
            try {
                for (PropostaItem pi : main.propostaItemDao.findByProposta(proposta.getId())) {
                    itensModel.addRow(new Object[]{
                        itensModel.getRowCount() + 1, pi.getTipoServicoNome(), pi.getDescricao(),
                        pi.getQuantidade(), String.format("R$ %.2f", pi.getValorUnitario()), String.format("R$ %.2f", pi.getValorTotal())
                    });
                }
                calcularTotais();
            } catch (Exception ignored) {}
        }

        // Botões salvar
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalvar = new JButton("Salvar Proposta");
        btnSalvar.addActionListener(e -> salvar());
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dispose());
        btns.add(btnSalvar); btns.add(btnCancelar);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(totaisPanel, BorderLayout.CENTER);
        bottomPanel.add(btns, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void addField(JPanel form, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.2;
        form.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        form.add(field, gbc);
    }

    private void adicionarItem() {
        if (tiposServico == null || tiposServico.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cadastre tipos de serviço primeiro."); return;
        }
        JComboBox<String> cbServico = new JComboBox<>();
        for (TipoServico ts : tiposServico)
            cbServico.addItem(ts.getId() + " - " + ts.getNome() + " (R$ " + String.format("%.2f", ts.getPrecoBase()) + ")");
        JTextField txtDesc = new JTextField(20);
        JTextField txtQtd = new JTextField("1");
        JTextField txtValor = new JTextField(10);

        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.add(new JLabel("Serviço:")); form.add(cbServico);
        form.add(new JLabel("Descrição:")); form.add(txtDesc);
        form.add(new JLabel("Quantidade:")); form.add(txtQtd);
        form.add(new JLabel("Valor Unit.:")); form.add(txtValor);

        cbServico.addActionListener(e -> {
            String sel = (String) cbServico.getSelectedItem();
            if (sel != null) {
                Long id = Long.parseLong(sel.split(" - ")[0]);
                for (TipoServico ts : tiposServico) {
                    if (ts.getId().equals(id)) {
                        txtValor.setText(String.valueOf(ts.getPrecoBase()));
                        if (txtDesc.getText().trim().isEmpty()) txtDesc.setText(ts.getNome());
                    }
                }
            }
        });

        int res = JOptionPane.showConfirmDialog(this, form, "Adicionar Item", JOptionPane.OK_CANCEL_OPTION);
        if (res != JOptionPane.OK_OPTION) return;

        String sel = (String) cbServico.getSelectedItem();
        Long tsId = Long.parseLong(sel.split(" - ")[0]);
        String tsNome = sel.split(" - ")[1].split(" \\(")[0];
        double qtd = Double.parseDouble(txtQtd.getText().trim().replace(",", "."));
        double valor = Double.parseDouble(txtValor.getText().trim().replace(",", "."));
        double total = qtd * valor;

        itensModel.addRow(new Object[]{
            itensModel.getRowCount() + 1, tsNome, txtDesc.getText().trim(),
            qtd, String.format("R$ %.2f", valor), String.format("R$ %.2f", total)
        });

        // Armazenar dados do item (usando a última linha)
        // Os dados serão salvos ao salvar a proposta
        calcularTotais();
    }

    private void removerItem() {
        int row = itensTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione um item."); return; }
        itensModel.removeRow(row);
        calcularTotais();
    }

    private void calcularTotais() {
        double total = 0;
        for (int i = 0; i < itensModel.getRowCount(); i++) {
            String valStr = (String) itensModel.getValueAt(i, 5);
            valStr = valStr.replace("R$ ", "").replace(",", ".");
            total += Double.parseDouble(valStr);
        }
        double desconto = 0;
        try { desconto = Double.parseDouble(txtDesconto.getText().trim().replace(",", ".")); } catch (Exception ignored) {}
        double final_ = total - desconto;
        lblTotal.setText(String.format("R$ %.2f", total));
        lblFinal.setText(String.format("R$ %.2f", final_));
    }

    private void salvar() {
        try {
            if (cbEmpresa.getSelectedItem() == null || txtTitulo.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Empresa e Título são obrigatórios."); return;
            }
            Long empresaId = Long.parseLong(((String) cbEmpresa.getSelectedItem()).split(" - ")[0]);

            Proposta p = proposta != null ? proposta : new Proposta();
            p.setEmpresaId(empresaId);
            p.setTitulo(txtTitulo.getText().trim());
            p.setDescricao(txtDescricao.getText().trim());
            p.setCondicoesPagamento(txtCondicoes.getText().trim());
            double desconto = 0;
            try { desconto = Double.parseDouble(txtDesconto.getText().trim().replace(",", ".")); } catch (Exception ignored) {}
            p.setDesconto(desconto);
            int dias = 30;
            try { dias = Integer.parseInt(txtValidade.getText().trim()); } catch (Exception ignored) {}
            p.setDataValidade(LocalDate.now().plusDays(dias));

            // Calcular totais
            double total = 0;
            for (int i = 0; i < itensModel.getRowCount(); i++) {
                String valStr = (String) itensModel.getValueAt(i, 5);
                valStr = valStr.replace("R$ ", "").replace(",", ".");
                total += Double.parseDouble(valStr);
            }
            p.setValorTotal(total);
            p.setValorFinal(total - desconto);

            if (proposta == null) {
                p.setCodigo("PROP-" + System.currentTimeMillis() % 10000);
            }

            main.propostaDao.save(p);

            // Salvar itens
            if (proposta != null) main.propostaItemDao.deleteByProposta(p.getId());
            for (int i = 0; i < itensModel.getRowCount(); i++) {
                PropostaItem pi = new PropostaItem();
                pi.setPropostaId(p.getId());
                pi.setDescricao((String) itensModel.getValueAt(i, 2));
                pi.setQuantidade(Double.parseDouble(itensModel.getValueAt(i, 3).toString().replace(",", ".")));
                String vu = (String) itensModel.getValueAt(i, 4);
                vu = vu.replace("R$ ", "").replace(",", ".");
                pi.setValorUnitario(Double.parseDouble(vu));
                String vt = (String) itensModel.getValueAt(i, 5);
                vt = vt.replace("R$ ", "").replace(",", ".");
                pi.setValorTotal(Double.parseDouble(vt));
                main.propostaItemDao.save(pi);
            }

            saved = true;
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() { return saved; }
}

