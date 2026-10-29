package com.gestaopgr.ui;

import com.gestaopgr.model.Empresa;
import com.gestaopgr.model.Pgr;
import com.gestaopgr.model.Risco;
import com.gestaopgr.model.PlanoAcao;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.List;

public class PgrPanel extends JPanel {
    private final MainFrame main;
    private JTable table;
    private DefaultTableModel tableModel;

    public PgrPanel(MainFrame main) {
        this.main = main;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("PGR - Programa de Gerenciamento de Riscos");
        title.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 22));
        topPanel.add(title, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnNovo = new JButton("Novo PGR");
        btnNovo.addActionListener(e -> abrirFormulario(null));
        JButton btnGerarPDF = new JButton("Gerar Laudo PDF");
        btnGerarPDF.addActionListener(e -> gerarLaudo());
        JButton btnExcluir = new JButton("Excluir");
        btnExcluir.addActionListener(e -> excluirSelecionado());
        btnPanel.add(btnNovo); btnPanel.add(btnGerarPDF); btnPanel.add(btnExcluir);
        topPanel.add(btnPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        String[] cols = {"ID", "Código", "Empresa", "Título", "Versão", "Elaboração", "Validade", "Status"};
        tableModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        table = new JTable(tableModel);
        table.setRowHeight(26);
        table.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        table.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        add(new JScrollPane(table), BorderLayout.CENTER);

        carregarDados();
    }

    private void carregarDados() {
        tableModel.setRowCount(0);
        try {
            for (Pgr p : main.pgrDao.findAll()) {
                tableModel.addRow(new Object[]{
                    p.getId(), p.getCodigo(), p.getEmpresaNome(), p.getTitulo(),
                    p.getVersao(), p.getDataElaboracao(), p.getDataValidade(), p.getStatus()
                });
            }
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage()); }
    }

    private void abrirFormulario(Pgr pgr) {
        PgrFormDialog d = new PgrFormDialog(main, pgr);
        d.setVisible(true);
        if (d.isSaved()) carregarDados();
    }

    private void gerarLaudo() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione um PGR."); return; }
        Long id = (Long) tableModel.getValueAt(row, 0);
        try {
            Pgr pgr = main.pgrDao.findById(id).orElse(null);
            if (pgr == null) return;
            String path = gerarLaudoPDF(pgr);
            JOptionPane.showMessageDialog(this, "Laudo gerado: " + path);
            if (Desktop.isDesktopSupported()) Desktop.getDesktop().open(new File(path));
            carregarDados();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private String gerarLaudoPDF(Pgr pgr) throws Exception {
        Empresa empresa = main.empresaDao.findById(pgr.getEmpresaId()).orElse(null);
        List<Risco> riscos = main.riscoDao.findByEmpresa(pgr.getEmpresaId());
        List<PlanoAcao> acoes = main.planoAcaoDao.findByEmpresa(pgr.getEmpresaId());

        String dir = System.getProperty("user.home") + File.separator + "GestaoPGR" + File.separator + "pgr";
        new File(dir).mkdirs();
        String path = dir + File.separator + "PGR_" + pgr.getCodigo() + "_v" + pgr.getVersao() + ".pdf";

        Document doc = new Document(PageSize.A4, 50, 50, 60, 60);
        PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(path));

        writer.setPageEvent(new PdfPageEventHelper() {
            public void onEndPage(PdfWriter w, Document d) {
                try {
                    PdfContentByte cb = w.getDirectContent();
                    cb.beginText();
                    BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, false);
                    cb.setFontAndSize(bf, 9);
                    cb.setTextMatrix(50, 30);
                    cb.showText("PGR - " + (empresa != null ? empresa.getRazaoSocial() : "") + "  |  Pagina " + w.getPageNumber());
                    cb.endText();
                } catch (Exception ignored) {}
            }
        });

        doc.open();

        com.lowagie.text.Font titleFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 20, com.lowagie.text.Font.BOLD);
        com.lowagie.text.Font h1 = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 14, com.lowagie.text.Font.BOLD);
        com.lowagie.text.Font h2 = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 12, com.lowagie.text.Font.BOLD);
        com.lowagie.text.Font body = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 10, com.lowagie.text.Font.NORMAL);
        com.lowagie.text.Font small = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 9, com.lowagie.text.Font.NORMAL);

        // ===== CAPA =====
        Paragraph t = new Paragraph("PROGRAMA DE GERENCIAMENTO DE RISCOS", titleFont);
        t.setAlignment(Element.ALIGN_CENTER);
        t.setSpacingAfter(5);
        doc.add(t);

        Paragraph sub = new Paragraph("(PGR)", new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 16, com.lowagie.text.Font.BOLD));
        sub.setAlignment(Element.ALIGN_CENTER);
        sub.setSpacingAfter(30);
        doc.add(sub);

        Paragraph emp = new Paragraph(empresa != null ? empresa.getRazaoSocial() : "Empresa", new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 14, com.lowagie.text.Font.BOLD));
        emp.setAlignment(Element.ALIGN_CENTER);
        doc.add(emp);

        Paragraph cnpj = new Paragraph("CNPJ: " + (empresa != null ? empresa.getCnpj() : ""), body);
        cnpj.setAlignment(Element.ALIGN_CENTER);
        doc.add(cnpj);

        doc.add(new Paragraph(" "));
        doc.add(new Paragraph(" "));

        Paragraph info = new Paragraph(
            "Codigo: " + pgr.getCodigo() + "\n" +
            "Versao: " + pgr.getVersao() + "\n" +
            "Data de Elaboracao: " + pgr.getDataElaboracao() + "\n" +
            "Data de Validade: " + pgr.getDataValidade() + "\n" +
            "Elaborador: " + pgr.getElaborador() + "\n" +
            "Registro: " + pgr.getElaboradorRegistro() + "\n" +
            "Status: " + pgr.getStatus(),
            body
        );
        info.setAlignment(Element.ALIGN_CENTER);
        doc.add(info);

        doc.add(new Paragraph(" "));
        doc.add(new Paragraph(" "));

        Paragraph nr = new Paragraph("Conforme NR-01 (Disposicoes Gerais e Gerenciamento de Riscos Ocupacionais)", small);
        nr.setAlignment(Element.ALIGN_CENTER);
        doc.add(nr);

        doc.newPage();

        // ===== 1. INTRODUCAO =====
        doc.add(new Paragraph("1. INTRODUCAO", h1));
        doc.add(new Paragraph(" "));
        doc.add(new Paragraph("Este Programa de Gerenciamento de Riscos (PGR) foi elaborado conforme a Norma Regulamentadora NR-01, " +
            "que estabelece as disposicoes gerais e o gerenciamento de riscos ocupacionais. O PGR e composto pelo inventario " +
            "de riscos ocupacionais e pelo plano de acao, visando a prevencao de acidentes e doencas ocupacionais.", body));
        doc.add(new Paragraph(" "));

        // ===== 2. DADOS DA EMPRESA =====
        doc.add(new Paragraph("2. DADOS DA EMPRESA", h1));
        doc.add(new Paragraph(" "));

        PdfPTable empTable = new PdfPTable(2);
        empTable.setWidthPercentage(100);
        empTable.setWidths(new float[]{1, 2});

        String[][] empData = {
            {"Razao Social", empresa != null ? empresa.getRazaoSocial() : ""},
            {"CNPJ", empresa != null ? empresa.getCnpj() : ""},
            {"CNAE", (empresa != null ? empresa.getCnae() : "") + " - " + (empresa != null ? empresa.getCnaeDescricao() : "")},
            {"Endereco", empresa != null ? empresa.getEndereco() : ""},
            {"Cidade/UF", (empresa != null ? empresa.getCidade() : "") + "/" + (empresa != null ? empresa.getUf() : "")},
            {"CEP", empresa != null ? empresa.getCep() : ""},
            {"Telefone", empresa != null ? empresa.getTelefone() : ""},
            {"E-mail", empresa != null ? empresa.getEmail() : ""},
            {"Responsavel", empresa != null ? empresa.getResponsavel() : ""},
            {"N. Empregados", String.valueOf(empresa != null ? empresa.getNumEmpregados() : 0)}
        };

        for (String[] row : empData) {
            PdfPCell c1 = new PdfPCell(new Phrase(row[0], h2));
            c1.setPadding(4);
            c1.setBackgroundColor(new Color(240, 240, 245));
            empTable.addCell(c1);
            PdfPCell c2 = new PdfPCell(new Phrase(row[1], body));
            c2.setPadding(4);
            empTable.addCell(c2);
        }
        doc.add(empTable);
        doc.add(new Paragraph(" "));

        // ===== 3. INVENTARIO DE RISCOS =====
        doc.add(new Paragraph("3. INVENTARIO DE RISCOS OCUPACIONAIS", h1));
        doc.add(new Paragraph(" "));

        if (riscos.isEmpty()) {
            doc.add(new Paragraph("Nenhum risco cadastrado.", body));
        } else {
            PdfPTable riscoTable = new PdfPTable(6);
            riscoTable.setWidthPercentage(100);
            riscoTable.setWidths(new float[]{2.5f, 1, 1, 0.7f, 0.7f, 0.7f});

            String[] riscoHeaders = {"Risco", "Categoria", "Setor", "Prob.", "Sev.", "Nivel"};
            for (String h : riscoHeaders) {
                PdfPCell hc = new PdfPCell(new Phrase(h, h2));
                hc.setBackgroundColor(new Color(200, 200, 210));
                hc.setPadding(4);
                hc.setHorizontalAlignment(Element.ALIGN_CENTER);
                riscoTable.addCell(hc);
            }

            for (Risco r : riscos) {
                riscoTable.addCell(new PdfPCell(new Phrase(r.getPerigoDescricao(), small)));
                riscoTable.addCell(new PdfPCell(new Phrase(r.getCategoria(), small)));
                riscoTable.addCell(new PdfPCell(new Phrase(r.getSetorNome() != null ? r.getSetorNome() : "-", small)));
                PdfPCell pc = new PdfPCell(new Phrase(String.valueOf(r.getProbabilidade()), small));
                pc.setHorizontalAlignment(Element.ALIGN_CENTER);
                riscoTable.addCell(pc);
                PdfPCell sc = new PdfPCell(new Phrase(String.valueOf(r.getSeveridade()), small));
                sc.setHorizontalAlignment(Element.ALIGN_CENTER);
                riscoTable.addCell(sc);
                PdfPCell nc = new PdfPCell(new Phrase(r.getNivelRisco() + " (" + r.getClassificacao() + ")", small));
                nc.setHorizontalAlignment(Element.ALIGN_CENTER);
                riscoTable.addCell(nc);
            }
            doc.add(riscoTable);
        }

        doc.add(new Paragraph(" "));
        doc.add(new Paragraph("Total de riscos identificados: " + riscos.size(), body));
        doc.add(new Paragraph(" "));

        // ===== 4. PLANO DE ACAO =====
        doc.add(new Paragraph("4. PLANO DE ACAO", h1));
        doc.add(new Paragraph(" "));

        if (acoes.isEmpty()) {
            doc.add(new Paragraph("Nenhuma acao cadastrada.", body));
        } else {
            PdfPTable acaoTable = new PdfPTable(5);
            acaoTable.setWidthPercentage(100);
            acaoTable.setWidths(new float[]{2.5f, 1.5f, 1.5f, 1, 1});

            String[] acaoHeaders = {"Acao", "Tipo", "Responsavel", "Prazo", "Status"};
            for (String h : acaoHeaders) {
                PdfPCell hc = new PdfPCell(new Phrase(h, h2));
                hc.setBackgroundColor(new Color(200, 200, 210));
                hc.setPadding(4);
                hc.setHorizontalAlignment(Element.ALIGN_CENTER);
                acaoTable.addCell(hc);
            }

            for (PlanoAcao a : acoes) {
                acaoTable.addCell(new PdfPCell(new Phrase(a.getAcao(), small)));
                acaoTable.addCell(new PdfPCell(new Phrase(a.getTipo(), small)));
                acaoTable.addCell(new PdfPCell(new Phrase(a.getResponsavel() != null ? a.getResponsavel() : "-", small)));
                acaoTable.addCell(new PdfPCell(new Phrase(a.getPrazo() != null ? a.getPrazo().toString() : "-", small)));
                acaoTable.addCell(new PdfPCell(new Phrase(a.getStatus(), small)));
            }
            doc.add(acaoTable);
        }

        doc.add(new Paragraph(" "));

        // ===== 5. CONCLUSAO =====
        doc.add(new Paragraph("5. CONCLUSAO", h1));
        doc.add(new Paragraph(" "));
        doc.add(new Paragraph("Este PGR foi elaborado para atender as exigencias da NR-01 e deve ser revisado " +
            "periodicamente (a cada 2 anos ou quando houver mudancas no ambiente de trabalho, acidentes, " +
            "doencas ocupacionais ou alteracoes legais). A empresa e responsavel por implementar as medidas " +
            "de controle propostas no plano de acao e por manter o inventario de riscos atualizado.", body));

        if (pgr.getObservacoes() != null && !pgr.getObservacoes().isEmpty()) {
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph("Observacoes: " + pgr.getObservacoes(), body));
        }

        doc.add(new Paragraph(" "));
        doc.add(new Paragraph(" "));
        doc.add(new Paragraph(" "));
        doc.add(new Paragraph("_______________________________________", body));
        doc.add(new Paragraph("Elaborador: " + pgr.getElaborador(), body));
        doc.add(new Paragraph("Registro: " + pgr.getElaboradorRegistro(), body));
        doc.add(new Paragraph("Data: " + LocalDate.now().toString(), body));

        doc.close();

        // Atualizar path
        pgr.setPdfPath(path);
        main.pgrDao.save(pgr);

        return path;
    }

    private void excluirSelecionado() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione um PGR."); return; }
        if (JOptionPane.showConfirmDialog(this, "Excluir PGR?", "Confirmar", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        Long id = (Long) tableModel.getValueAt(row, 0);
        try { main.pgrDao.delete(id); carregarDados(); }
        catch (Exception e) { JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage()); }
    }
}

class PgrFormDialog extends JDialog {
    private boolean saved = false;
    private final MainFrame main;
    private Pgr pgr;
    private JComboBox<String> cbEmpresa;
    private JTextField txtTitulo, txtCodigo, txtElaborador, txtRegistro, txtObs, txtValidade;

    public PgrFormDialog(MainFrame main, Pgr pgr) {
        super(main, "PGR", true);
        this.main = main; this.pgr = pgr;
        setSize(480, 400); setLocationRelativeTo(main);
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cbEmpresa = new JComboBox<>();
        txtTitulo = new JTextField(25); txtCodigo = new JTextField(25);
        txtElaborador = new JTextField(25); txtRegistro = new JTextField(25);
        txtObs = new JTextField(25); txtValidade = new JTextField(25);

        try {
            for (Empresa e : main.empresaDao.findAtivas())
                cbEmpresa.addItem(e.getId() + " - " + e.getRazaoSocial());
        } catch (Exception ignored) {}

        int row = 0;
        addField(form, gbc, row++, "Empresa:*", cbEmpresa);
        addField(form, gbc, row++, "Código:", txtCodigo);
        addField(form, gbc, row++, "Título:*", txtTitulo);
        addField(form, gbc, row++, "Elaborador:*", txtElaborador);
        addField(form, gbc, row++, "Registro (CREA/CFT):", txtRegistro);
        addField(form, gbc, row++, "Validade (dias):", txtValidade);
        addField(form, gbc, row++, "Observações:", txtObs);

        if (pgr != null) {
            txtTitulo.setText(pgr.getTitulo());
            txtCodigo.setText(pgr.getCodigo());
            txtElaborador.setText(pgr.getElaborador());
            txtRegistro.setText(pgr.getElaboradorRegistro());
            if (pgr.getDataValidade() != null) txtValidade.setText(String.valueOf(java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), pgr.getDataValidade())));
            txtObs.setText(pgr.getObservacoes());
        } else {
            txtValidade.setText("365");
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
            if (cbEmpresa.getSelectedItem() == null || txtTitulo.getText().trim().isEmpty() || txtElaborador.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha os campos obrigatórios."); return;
            }
            Long empresaId = Long.parseLong(((String) cbEmpresa.getSelectedItem()).split(" - ")[0]);
            Pgr p = pgr != null ? pgr : new Pgr();
            p.setEmpresaId(empresaId);
            p.setTitulo(txtTitulo.getText().trim());
            p.setCodigo(txtCodigo.getText().trim().isEmpty() ? "PGR-" + System.currentTimeMillis() % 10000 : txtCodigo.getText().trim());
            p.setElaborador(txtElaborador.getText().trim());
            p.setElaboradorRegistro(txtRegistro.getText().trim());
            p.setObservacoes(txtObs.getText().trim());
            int dias = 365;
            try { dias = Integer.parseInt(txtValidade.getText().trim()); } catch (Exception ignored) {}
            p.setDataValidade(LocalDate.now().plusDays(dias));

            main.pgrDao.save(p);
            saved = true;
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() { return saved; }
}



