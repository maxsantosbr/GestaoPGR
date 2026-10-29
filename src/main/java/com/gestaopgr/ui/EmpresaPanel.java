package com.gestaopgr.ui;

import com.gestaopgr.model.Empresa;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EmpresaPanel extends JPanel {
    private final MainFrame main;
    private JTable table;
    private DefaultTableModel tableModel;

    public EmpresaPanel(MainFrame main) {
        this.main = main;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Empresas");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        topPanel.add(title, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnNova = new JButton("Nova Empresa");
        btnNova.addActionListener(e -> abrirFormulario(null));
        JButton btnEditar = new JButton("Editar");
        btnEditar.addActionListener(e -> editarSelecionado());
        JButton btnExcluir = new JButton("Excluir");
        btnExcluir.addActionListener(e -> excluirSelecionado());
        btnPanel.add(btnNova); btnPanel.add(btnEditar); btnPanel.add(btnExcluir);
        topPanel.add(btnPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        String[] cols = {"ID", "Razão Social", "CNPJ", "CNAE", "Cidade/UF", "Empregados", "Status"};
        tableModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        table = new JTable(tableModel);
        table.setRowHeight(26);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        carregarDados();
    }

    private void carregarDados() {
        tableModel.setRowCount(0);
        try {
            for (Empresa e : main.empresaDao.findAll()) {
                tableModel.addRow(new Object[]{
                    e.getId(), e.getRazaoSocial(), e.getCnpj(), e.getCnae(),
                    (e.getCidade() != null ? e.getCidade() : "") + "/" + (e.getUf() != null ? e.getUf() : ""),
                    e.getNumEmpregados(), e.isAtivo() ? "Ativo" : "Inativo"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }

    private void abrirFormulario(Empresa empresa) {
        EmpresaFormDialog d = new EmpresaFormDialog(main, empresa);
        d.setVisible(true);
        if (d.isSaved()) carregarDados();
    }

    private void editarSelecionado() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione uma empresa."); return; }
        Long id = (Long) tableModel.getValueAt(row, 0);
        try {
            Empresa e = main.empresaDao.findById(id).orElse(null);
            if (e != null) abrirFormulario(e);
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage()); }
    }

    private void excluirSelecionado() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione uma empresa."); return; }
        if (JOptionPane.showConfirmDialog(this, "Excluir empresa?", "Confirmar", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        Long id = (Long) tableModel.getValueAt(row, 0);
        try { main.empresaDao.delete(id); carregarDados(); }
        catch (Exception e) { JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage()); }
    }
}

class EmpresaFormDialog extends JDialog {
    private boolean saved = false;
    private final MainFrame main;
    private Empresa empresa;
    private JTextField txtRazao, txtFantasia, txtCnpj, txtCnae, txtCnaeDesc, txtEndereco, txtCidade, txtUf, txtCep, txtTelefone, txtEmail, txtResponsavel, txtNumEmp;

    public EmpresaFormDialog(MainFrame main, Empresa empresa) {
        super(main, "Empresa", true);
        this.main = main; this.empresa = empresa;
        setSize(520, 520); setLocationRelativeTo(main);
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtRazao = new JTextField(25); txtFantasia = new JTextField(25); txtCnpj = new JTextField(25);
        txtCnae = new JTextField(25); txtCnaeDesc = new JTextField(25); txtEndereco = new JTextField(25);
        txtCidade = new JTextField(25); txtUf = new JTextField(25); txtCep = new JTextField(25);
        txtTelefone = new JTextField(25); txtEmail = new JTextField(25); txtResponsavel = new JTextField(25);
        txtNumEmp = new JTextField(25);

        int row = 0;
        addField(form, gbc, row++, "Razão Social:*", txtRazao);
        addField(form, gbc, row++, "Nome Fantasia:", txtFantasia);
        addField(form, gbc, row++, "CNPJ:", txtCnpj);
        addField(form, gbc, row++, "CNAE:", txtCnae);
        addField(form, gbc, row++, "Descrição CNAE:", txtCnaeDesc);
        addField(form, gbc, row++, "Endereço:", txtEndereco);
        addField(form, gbc, row++, "Cidade:", txtCidade);
        addField(form, gbc, row++, "UF:", txtUf);
        addField(form, gbc, row++, "CEP:", txtCep);
        addField(form, gbc, row++, "Telefone:", txtTelefone);
        addField(form, gbc, row++, "E-mail:", txtEmail);
        addField(form, gbc, row++, "Responsável:", txtResponsavel);
        addField(form, gbc, row++, "Nº Empregados:", txtNumEmp);

        if (empresa != null) {
            txtRazao.setText(empresa.getRazaoSocial());
            txtFantasia.setText(empresa.getNomeFantasia());
            txtCnpj.setText(empresa.getCnpj());
            txtCnae.setText(empresa.getCnae());
            txtCnaeDesc.setText(empresa.getCnaeDescricao());
            txtEndereco.setText(empresa.getEndereco());
            txtCidade.setText(empresa.getCidade());
            txtUf.setText(empresa.getUf());
            txtCep.setText(empresa.getCep());
            txtTelefone.setText(empresa.getTelefone());
            txtEmail.setText(empresa.getEmail());
            txtResponsavel.setText(empresa.getResponsavel());
            txtNumEmp.setText(String.valueOf(empresa.getNumEmpregados()));
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
            if (txtRazao.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Razão Social é obrigatória."); return;
            }
            Empresa e = empresa != null ? empresa : new Empresa();
            e.setRazaoSocial(txtRazao.getText().trim());
            e.setNomeFantasia(txtFantasia.getText().trim());
            e.setCnpj(txtCnpj.getText().trim());
            e.setCnae(txtCnae.getText().trim());
            e.setCnaeDescricao(txtCnaeDesc.getText().trim());
            e.setEndereco(txtEndereco.getText().trim());
            e.setCidade(txtCidade.getText().trim());
            e.setUf(txtUf.getText().trim());
            e.setCep(txtCep.getText().trim());
            e.setTelefone(txtTelefone.getText().trim());
            e.setEmail(txtEmail.getText().trim());
            e.setResponsavel(txtResponsavel.getText().trim());
            if (!txtNumEmp.getText().trim().isEmpty())
                e.setNumEmpregados(Integer.parseInt(txtNumEmp.getText().trim()));
            e.setAtivo(true);

            main.empresaDao.save(e);
            saved = true;
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() { return saved; }
}
