package com.gestaopgr.ui;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;

public class RelatoriosPanel extends JPanel {
    private final MainFrame main;

    public RelatoriosPanel(MainFrame main) {
        this.main = main;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Relatórios");
        title.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));

        JLabel icon = new JLabel("📊");
        icon.setFont(new java.awt.Font("Segoe UI Emoji", java.awt.Font.PLAIN, 48));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(icon);
        card.add(Box.createVerticalStrut(10));

        JLabel desc = new JLabel("Selecione o relatório para gerar em PDF:");
        desc.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
        desc.setForeground(new Color(100, 116, 139));
        desc.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(desc);
        card.add(Box.createVerticalStrut(20));

        String[] relatorios = {
            "Inventário de Riscos por Empresa",
            "Plano de Ação (Status)",
            "Status de Conformidade (PGR)",
            "Riscos Críticos (ALTO/CRÍTICO)",
            "Faturamento por Período"
        };

        for (String r : relatorios) {
            JButton btn = new JButton(r);
            btn.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(350, 36));
            btn.setPreferredSize(new Dimension(350, 36));
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.addActionListener(e -> gerarRelatorio(r));
            card.add(btn);
            card.add(Box.createVerticalStrut(8));
        }

        center.add(card);
        add(center, BorderLayout.CENTER);
    }

    private void gerarRelatorio(String nome) {
        try {
            String dir = System.getProperty("user.home") + File.separator + "GestaoPGR" + File.separator + "relatorios";
            new File(dir).mkdirs();
            String fileName = nome.replace(" ", "_").replace("/", "") + "_" + LocalDate.now() + ".pdf";
            String path = dir + File.separator + fileName;

            Document doc = new Document(PageSize.A4, 50, 50, 60, 60);
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(path));

            writer.setPageEvent(new PdfPageEventHelper() {
                public void onEndPage(PdfWriter w, Document d) {
                    try {
                        PdfContentByte cb = w.getDirectContent();
                        cb.beginText();
                        BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, false);
                        cb.setFontAndSize(bf, 8);
                        cb.setTextMatrix(50, 25);
                        cb.showText("Relatorio - " + nome + "  |  Pagina " + w.getPageNumber());
                        cb.endText();
                    } catch (Exception ignored) {}
                }
            });

            doc.open();
            com.lowagie.text.Font titleFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 16, java.awt.Font.BOLD);
            com.lowagie.text.Font h1 = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 12, java.awt.Font.BOLD);
            com.lowagie.text.Font body = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 10, com.lowagie.text.Font.NORMAL);
            com.lowagie.text.Font small = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 8, com.lowagie.text.Font.NORMAL);

            Paragraph t = new Paragraph("RELATORIO: " + nome.toUpperCase(), titleFont);
            t.setAlignment(Element.ALIGN_CENTER);
            t.setSpacingAfter(5);
            doc.add(t);

            Paragraph sub = new Paragraph("Gerado em: " + LocalDate.now().toString(), body);
            sub.setAlignment(Element.ALIGN_CENTER);
            sub.setSpacingAfter(15);
            doc.add(sub);

            if (nome.equals("Inventário de Riscos por Empresa")) {
                gerarRelatorioRiscos(doc, h1, body, small);
            } else if (nome.equals("Plano de Ação (Status)")) {
                gerarRelatorioPlanoAcao(doc, h1, body, small);
            } else if (nome.equals("Status de Conformidade (PGR)")) {
                gerarRelatorioConformidade(doc, h1, body, small);
            } else if (nome.equals("Riscos Críticos (ALTO/CRÍTICO)")) {
                gerarRelatorioRiscosCriticos(doc, h1, body, small);
            } else if (nome.equals("Faturamento por Período")) {
                gerarRelatorioFaturamento(doc, h1, body, small);
            }

            doc.close();

            JOptionPane.showMessageDialog(this, "Relatório gerado!\n\nLocal: " + path, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            if (Desktop.isDesktopSupported()) Desktop.getDesktop().open(new File(path));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void gerarRelatorioRiscos(Document doc, com.lowagie.text.Font h1, com.lowagie.text.Font body, com.lowagie.text.Font small) throws Exception {
        for (com.gestaopgr.model.Empresa emp : main.empresaDao.findAtivas()) {
            doc.add(new Paragraph(emp.getRazaoSocial(), h1));
            doc.add(new Paragraph("CNPJ: " + emp.getCnpj() + "  |  Empregados: " + emp.getNumEmpregados(), body));
            doc.add(new Paragraph(" "));

            java.util.List<com.gestaopgr.model.Risco> riscos = main.riscoDao.findByEmpresa(emp.getId());
            if (riscos.isEmpty()) {
                doc.add(new Paragraph("Nenhum risco cadastrado.", body));
            } else {
                PdfPTable tab = new PdfPTable(5);
                tab.setWidthPercentage(100);
                tab.setWidths(new float[]{3, 1.2f, 0.6f, 0.6f, 0.8f});
                for (String h : new String[]{"Risco", "Categoria", "Prob.", "Sev.", "Classif."}) {
                    PdfPCell c = new PdfPCell(new Phrase(h, h1));
                    c.setBackgroundColor(new Color(200, 200, 210));
                    c.setPadding(3);
                    c.setHorizontalAlignment(Element.ALIGN_CENTER);
                    tab.addCell(c);
                }
                for (com.gestaopgr.model.Risco r : riscos) {
                    tab.addCell(new PdfPCell(new Phrase(r.getPerigoDescricao(), small)));
                    tab.addCell(new PdfPCell(new Phrase(r.getCategoria(), small)));
                    PdfPCell pc = new PdfPCell(new Phrase(String.valueOf(r.getProbabilidade()), small));
                    pc.setHorizontalAlignment(Element.ALIGN_CENTER);
                    tab.addCell(pc);
                    PdfPCell sc = new PdfPCell(new Phrase(String.valueOf(r.getSeveridade()), small));
                    sc.setHorizontalAlignment(Element.ALIGN_CENTER);
                    tab.addCell(sc);
                    PdfPCell nc = new PdfPCell(new Phrase(r.getClassificacao(), small));
                    nc.setHorizontalAlignment(Element.ALIGN_CENTER);
                    tab.addCell(nc);
                }
                doc.add(tab);
            }
            doc.add(new Paragraph("Total de riscos: " + riscos.size(), body));
            doc.add(new Paragraph(" "));
        }
    }

    private void gerarRelatorioPlanoAcao(Document doc, com.lowagie.text.Font h1, com.lowagie.text.Font body, com.lowagie.text.Font small) throws Exception {
        java.util.List<com.gestaopgr.model.PlanoAcao> acoes = main.planoAcaoDao.findAll();
        doc.add(new Paragraph("Total de acoes: " + acoes.size(), body));
        doc.add(new Paragraph(" "));

        PdfPTable tab = new PdfPTable(5);
        tab.setWidthPercentage(100);
        tab.setWidths(new float[]{3, 1.5f, 1.5f, 1, 1});
        for (String h : new String[]{"Acao", "Tipo", "Responsavel", "Prazo", "Status"}) {
            PdfPCell c = new PdfPCell(new Phrase(h, h1));
            c.setBackgroundColor(new Color(200, 200, 210));
            c.setPadding(3);
            c.setHorizontalAlignment(Element.ALIGN_CENTER);
            tab.addCell(c);
        }
        for (com.gestaopgr.model.PlanoAcao a : acoes) {
            tab.addCell(new PdfPCell(new Phrase(a.getAcao(), small)));
            tab.addCell(new PdfPCell(new Phrase(a.getTipo(), small)));
            tab.addCell(new PdfPCell(new Phrase(a.getResponsavel() != null ? a.getResponsavel() : "-", small)));
            tab.addCell(new PdfPCell(new Phrase(a.getPrazo() != null ? a.getPrazo().toString() : "-", small)));
            tab.addCell(new PdfPCell(new Phrase(a.getStatus(), small)));
        }
        doc.add(tab);
    }

    private void gerarRelatorioConformidade(Document doc, com.lowagie.text.Font h1, com.lowagie.text.Font body, com.lowagie.text.Font small) throws Exception {
        java.util.List<com.gestaopgr.model.Empresa> empresas = main.empresaDao.findAtivas();
        doc.add(new Paragraph("Total de empresas: " + empresas.size(), body));
        doc.add(new Paragraph(" "));

        PdfPTable tab = new PdfPTable(4);
        tab.setWidthPercentage(100);
        tab.setWidths(new float[]{3, 1.5f, 1.5f, 1.5f});
        for (String h : new String[]{"Empresa", "PGR", "Validade", "Status"}) {
            PdfPCell c = new PdfPCell(new Phrase(h, h1));
            c.setBackgroundColor(new Color(200, 200, 210));
            c.setPadding(3);
            c.setHorizontalAlignment(Element.ALIGN_CENTER);
            tab.addCell(c);
        }
        int atualizados = 0, vencendo = 0, vencidos = 0, semPgr = 0;
        for (com.gestaopgr.model.Empresa e : empresas) {
            java.util.List<com.gestaopgr.model.Pgr> pgrs = main.pgrDao.findByEmpresa(e.getId());
            com.gestaopgr.model.Pgr pgr = pgrs.isEmpty() ? null : pgrs.get(0);
            tab.addCell(new PdfPCell(new Phrase(e.getRazaoSocial(), small)));
            tab.addCell(new PdfPCell(new Phrase(pgr != null ? pgr.getCodigo() : "Sem PGR", small)));
            tab.addCell(new PdfPCell(new Phrase(pgr != null ? pgr.getDataValidade().toString() : "-", small)));
            String status = pgr != null ? pgr.getStatus() : "PENDENTE";
            if (pgr == null) { status = "SEM PGR"; semPgr++; }
            else if (pgr.getDataValidade().isBefore(LocalDate.now())) { status = "VENCIDO"; vencidos++; }
            else if (pgr.getDataValidade().isBefore(LocalDate.now().plusDays(30))) { status = "VENCENDO"; vencendo++; }
            else { status = "ATUALIZADO"; atualizados++; }
            tab.addCell(new PdfPCell(new Phrase(status, small)));
        }
        doc.add(tab);
        doc.add(new Paragraph(" "));
        doc.add(new Paragraph("Resumo: Atualizados=" + atualizados + " | Vencendo=" + vencendo + " | Vencidos=" + vencidos + " | Sem PGR=" + semPgr, body));
    }

    private void gerarRelatorioRiscosCriticos(Document doc, com.lowagie.text.Font h1, com.lowagie.text.Font body, com.lowagie.text.Font small) throws Exception {
        java.util.List<com.gestaopgr.model.Risco> riscos = main.riscoDao.findAll();
        int count = 0;
        PdfPTable tab = new PdfPTable(4);
        tab.setWidthPercentage(100);
        tab.setWidths(new float[]{3, 2, 1, 1});
        for (String h : new String[]{"Risco", "Empresa", "Nivel", "Classif."}) {
            PdfPCell c = new PdfPCell(new Phrase(h, h1));
            c.setBackgroundColor(new Color(200, 200, 210));
            c.setPadding(3);
            c.setHorizontalAlignment(Element.ALIGN_CENTER);
            tab.addCell(c);
        }
        for (com.gestaopgr.model.Risco r : riscos) {
            if ("ALTO".equals(r.getClassificacao()) || "CRITICO".equals(r.getClassificacao())) {
                tab.addCell(new PdfPCell(new Phrase(r.getPerigoDescricao(), small)));
                tab.addCell(new PdfPCell(new Phrase(r.getEmpresaNome(), small)));
                PdfPCell nc = new PdfPCell(new Phrase(String.valueOf(r.getNivelRisco()), small));
                nc.setHorizontalAlignment(Element.ALIGN_CENTER);
                tab.addCell(nc);
                tab.addCell(new PdfPCell(new Phrase(r.getClassificacao(), small)));
                count++;
            }
        }
        doc.add(tab);
        doc.add(new Paragraph("Total de riscos criticos: " + count, body));
    }

    private void gerarRelatorioFaturamento(Document doc, com.lowagie.text.Font h1, com.lowagie.text.Font body, com.lowagie.text.Font small) throws Exception {
        java.util.List<com.gestaopgr.model.Fatura> faturas = main.faturaDao.findAll();
        double total = 0, pago = 0, pendente = 0, vencido = 0;
        for (com.gestaopgr.model.Fatura f : faturas) {
            total += f.getValorFinal();
            if ("PAGA".equals(f.getStatus())) pago += f.getValorFinal();
            else if ("VENCIDA".equals(f.getStatus())) vencido += f.getValorFinal();
            else pendente += f.getValorFinal();
        }

        doc.add(new Paragraph("Total faturado: R$ " + String.format("%.2f", total), body));
        doc.add(new Paragraph("Pago: R$ " + String.format("%.2f", pago), body));
        doc.add(new Paragraph("Pendente: R$ " + String.format("%.2f", pendente), body));
        doc.add(new Paragraph("Vencido: R$ " + String.format("%.2f", vencido), body));
        doc.add(new Paragraph(" "));
        doc.add(new Paragraph("Total de faturas: " + faturas.size(), body));
    }
}


