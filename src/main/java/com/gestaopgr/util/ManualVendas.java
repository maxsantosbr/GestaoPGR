package com.gestaopgr.util;

import org.apache.poi.xwpf.usermodel.*;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

public class ManualVendas {
    private static final String[] SUMARIO = {
        "1. Introdução",
        "2. Primeiros Passos",
        "3. Cadastro de Empresas",
        "4. Tipos de Serviço e Preços",
        "5. Propostas e Orçamentos",
        "6. Contratos",
        "7. Faturas e Pagamentos",
        "8. PGR - Programa de Gerenciamento de Riscos",
        "9. PPP - Perfil Profissiográfico",
        "10. Inventário de Riscos",
        "11. Matriz de Risco",
        "12. Plano de Ação",
        "13. Alertas",
        "14. Relatórios",
        "15. Manual do Usuário",
        "16. Proposta de Vendas",
        "17. Configurações",
        "18. Backup e Restauração",
        "19. Dicas de Vendas",
        "20. Precificação de Serviços",
        "21. Estratégias de Marketing",
        "22. Solução de Problemas",
        "23. Apêndice - Tabelas de Referência"
    };
    public static void main(String[] args) {
        try {
            String dir = System.getProperty("user.home") + File.separator + "GestaoPGR" + File.separator + "documentos";
            new File(dir).mkdirs();

            gerarWord(dir + File.separator + "Manual_Vendas_GestaoPGR.docx");
            gerarPdf(dir + File.separator + "Manual_Vendas_GestaoPGR.pdf");

            System.out.println("Manual gerado com sucesso em: " + dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==================== WORD (.docx) ====================
    private static void gerarWord(String path) throws Exception {
        XWPFDocument doc = new XWPFDocument();

        // Capa
        XWPFParagraph titulo = doc.createParagraph();
        titulo.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = titulo.createRun();
        run.setText("MANUAL DE VENDAS E USO");
        run.setBold(true);
        run.setFontSize(28);
        run.setFontFamily("Calibri");

        XWPFParagraph sub = doc.createParagraph();
        sub.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun runSub = sub.createRun();
        runSub.setText("Sistema Gestão PGR - Programa de Gerenciamento de Riscos");
        runSub.setFontSize(16);
        runSub.setFontFamily("Calibri");

        doc.createParagraph();
        XWPFParagraph info = doc.createParagraph();
        info.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun runInfo = info.createRun();
        runInfo.setText("Versão 1.0\nDesenvolvido por Maxwell Souza dos Santos\nCNPJ: 54.718.287/0001-19\n© 2026 - Todos os direitos reservados");
        runInfo.setFontSize(12);
        runInfo.setFontFamily("Calibri");

        doc.createParagraph();
        doc.createParagraph();
        XWPFParagraph confidencial = doc.createParagraph();
        confidencial.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun runConf = confidencial.createRun();
        runConf.setText("Documento confidencial - Uso interno");
        runConf.setItalic(true);
        runConf.setFontSize(10);

        doc.createParagraph();
        doc.createParagraph();

        // Sumário
        XWPFParagraph sumarioTitle = doc.createParagraph();
        XWPFRun runST = sumarioTitle.createRun();
        runST.setText("SUMÁRIO");
        runST.setBold(true);
        runST.setFontSize(18);
        for (String s : SUMARIO) {
            XWPFParagraph p = doc.createParagraph();
            XWPFRun r = p.createRun();
            r.setText(s);
            r.setFontSize(12);
        }

        // Capítulos
        addCapituloWord(doc, "1. Introdução",
            "O Sistema Gestão PGR é uma ferramenta desktop desenvolvida para técnicos e engenheiros de segurança do trabalho. " +
            "Com ele, você pode gerenciar empresas clientes, elaborar propostas comerciais, contratos, faturas e controlar pagamentos. " +
            "O software também auxilia na elaboração de PGR (Programa de Gerenciamento de Riscos) conforme a NR-01.\n\n" +
            "O sistema é multi-tenant, ou seja, você pode atender várias empresas clientes em um único software. " +
            "Todos os dados são armazenados localmente em um banco SQLite, sem necessidade de internet.\n\n" +
            "Principais funcionalidades:\n" +
            "• Cadastro de empresas e clientes\n" +
            "• Tipos de serviço com preços\n" +
            "• Propostas e orçamentos\n" +
            "• Contratos (pontual, recorrente, manutenção)\n" +
            "• Faturas e controle de vencimentos\n" +
            "• Registro de pagamentos (PIX, boleto, cartão, etc.)\n" +
            "• Inventário de riscos e PGR\n" +
            "• PPP - Perfil Profissiográfico Previdenciário\n" +
            "• Relatórios e alertas\n" +
            "• Backup e restauração\n" +
            "• Manual do usuário e proposta de vendas em PDF");

        addCapituloWord(doc, "2. Primeiros Passos",
            "2.1 Instalação\n" +
            "O software não requer instalação. Basta executar o arquivo gestao-pgr.jar ou o arquivo GestaoPGR.bat. " +
            "Na primeira execução, o banco de dados será criado automaticamente na pasta C:\\Users\\SeuUsuario\\GestaoPGR\\.\n\n" +
            "2.2 Primeiro Acesso\n" +
            "Ao abrir o software, você verá a tela principal com um menu lateral. " +
            "O sistema já vem com um usuário padrão (admin / admin123) e dados de exemplo.\n\n" +
            "2.3 Navegação\n" +
            "Use o menu lateral para navegar entre as telas. Clique em qualquer item para abrir o módulo correspondente. " +
            "A tela principal exibe um dashboard com indicadores gerais.\n\n" +
            "2.4 Configuração Inicial\n" +
            "Recomendamos começar por:\n" +
            "1. Cadastrar suas empresas clientes (menu Empresas)\n" +
            "2. Cadastrar os tipos de serviço que você oferece (menu Tipos de Serviço)\n" +
            "3. Criar propostas para seus clientes (menu Propostas)\n" +
            "4. Configurar seus dados (menu Configurações)");

        addCapituloWord(doc, "3. Cadastro de Empresas",
            "3.1 Como cadastrar uma empresa\n" +
            "1. Clique em 'Empresas / Clientes' no menu lateral.\n" +
            "2. Clique no botão 'Nova Empresa'.\n" +
            "3. Preencha os campos: Razão Social (obrigatório), CNPJ, CNAE, Endereço, Cidade, UF, CEP, Telefone, E-mail, Responsável e Número de Empregados.\n" +
            "4. Clique em 'Salvar'.\n\n" +
            "3.2 Editar ou excluir\n" +
            "Selecione uma empresa na tabela e clique em 'Editar' para alterar os dados, ou 'Excluir' para remover.\n\n" +
            "3.3 Dicas\n" +
            "• O CNPJ é importante para identificação fiscal.\n" +
            "• O CNAE ajuda na classificação de riscos por atividade.\n" +
            "• O número de empregados influencia no preço do PGR (pequena/média/grande empresa).");

        addCapituloWord(doc, "4. Tipos de Serviço e Preços",
            "4.1 O que são tipos de serviço\n" +
            "São os serviços que você oferece (PGR, PCMSO, LTCAT, treinamentos, consultorias, medições, etc.) " +
            "com seus respectivos preços base.\n\n" +
            "4.2 Como cadastrar\n" +
            "1. Clique em 'Tipos de Serviço' no menu lateral.\n" +
            "2. Clique em 'Novo Serviço'.\n" +
            "3. Preencha: Nome, Descrição, Preço Base (R$) e Unidade (EMPRESA, UN, HORA, M²).\n" +
            "4. Clique em 'Salvar'.\n\n" +
            "4.3 Preços sugeridos (já cadastrados)\n" +
            "• Elaboração de PGR (Empresa Pequena): R$ 800,00\n" +
            "• Elaboração de PGR (Empresa Média): R$ 1.500,00\n" +
            "• Elaboração de PGR (Empresa Grande): R$ 3.000,00\n" +
            "• Elaboração de PCMSO: R$ 800,00\n" +
            "• Elaboração de LTCAT: R$ 1.200,00\n" +
            "• Consultoria de EPI: R$ 500,00\n" +
            "• Inspeção de Segurança: R$ 300,00\n" +
            "• Medição de Ruído: R$ 200,00\n" +
            "• Treinamento NR-33: R$ 250,00/aluno\n" +
            "• Treinamento NR-35: R$ 150,00/aluno");

        addCapituloWord(doc, "5. Propostas e Orçamentos",
            "5.1 Como criar uma proposta\n" +
            "1. Clique em 'Propostas / Orçamentos' no menu lateral.\n" +
            "2. Clique em 'Nova Proposta'.\n" +
            "3. Selecione a empresa cliente.\n" +
            "4. Preencha o título, descrição, desconto e condições de pagamento.\n" +
            "5. Clique em 'Adicionar Item' para incluir serviços.\n" +
            "6. Selecione o serviço, quantidade e valor unitário.\n" +
            "7. Clique em 'Salvar Proposta'.\n\n" +
            "5.2 Status da proposta\n" +
            "• Rascunho: proposta em elaboração.\n" +
            "• Enviada: proposta enviada ao cliente.\n" +
            "• Aprovada: cliente aceitou a proposta.\n" +
            "• Rejeitada: cliente recusou.\n" +
            "• Expirada: prazo de validade vencido.\n\n" +
            "5.3 Dicas\n" +
            "• Defina um prazo de validade (ex: 30 dias).\n" +
            "• Inclua condições de pagamento (à vista, parcelado, etc.).\n" +
            "• O valor final é calculado automaticamente (total - desconto).");

        addCapituloWord(doc, "6. Contratos",
            "6.1 Como criar um contrato\n" +
            "1. Clique em 'Contratos' no menu lateral.\n" +
            "2. Clique em 'Novo Contrato'.\n" +
            "3. Selecione a empresa e preencha: tipo (pontual, recorrente, manutenção), título, valor total, data de início e fim.\n" +
            "4. Para contratos recorrentes, preencha o valor recorrente, periodicidade e dia de vencimento.\n" +
            "5. Clique em 'Salvar'.\n\n" +
            "6.2 Tipos de contrato\n" +
            "• Pontual: serviço único (ex: elaboração de PGR).\n" +
            "• Recorrente: serviço periódico (ex: manutenção mensal de PGR).\n" +
            "• Manutenção: contrato de manutenção contínua.\n\n" +
            "6.3 Status\n" +
            "• Ativo: contrato em vigor.\n" +
            "• Suspenso: temporariamente pausado.\n" +
            "• Encerrado: concluído.\n" +
            "• Cancelado: cancelado antes do prazo.");

        addCapituloWord(doc, "7. Faturas e Pagamentos",
            "7.1 Como criar uma fatura\n" +
            "1. Clique em 'Faturas' no menu lateral.\n" +
            "2. Clique em 'Nova Fatura'.\n" +
            "3. Selecione a empresa, preencha a descrição, valor, desconto e data de vencimento.\n" +
            "4. Clique em 'Salvar'.\n\n" +
            "7.2 Status da fatura\n" +
            "• Pendente: aguardando pagamento.\n" +
            "• Paga: pagamento confirmado.\n" +
            "• Vencida: passou da data de vencimento.\n" +
            "• Parcial: pagamento parcial recebido.\n" +
            "• Cancelada: fatura cancelada.\n\n" +
            "7.3 Registrar pagamento\n" +
            "1. Selecione a fatura na tabela.\n" +
            "2. Clique em 'Registrar Pagamento'.\n" +
            "3. Informe o valor e a forma de pagamento (PIX, boleto, cartão, etc.).\n" +
            "4. O status da fatura será atualizado automaticamente.\n\n" +
            "7.4 Formas de pagamento aceitas\n" +
            "PIX, Boleto, Cartão de Crédito, Cartão de Débito, Transferência Bancária, Dinheiro, Cheque");

        addCapituloWord(doc, "8. PGR - Programa de Gerenciamento de Riscos",
            "8.1 O que é o PGR\n" +
            "O PGR é um documento obrigatório conforme a NR-01, composto pelo inventário de riscos ocupacionais e pelo plano de ação. " +
            "Toda empresa com empregados deve ter um PGR atualizado.\n\n" +
            "8.2 Como criar um PGR\n" +
            "1. Clique em 'PGR / Laudo' no menu lateral.\n" +
            "2. Clique em 'Novo PGR'.\n" +
            "3. Selecione a empresa, preencha o título, elaborador, registro (CREA/CFT) e validade.\n" +
            "4. Clique em 'Salvar'.\n" +
            "5. Cadastre os riscos (menu Inventário de Riscos).\n" +
            "6. Crie o plano de ação (menu Plano de Ação).\n" +
            "7. Clique em 'Gerar Laudo PDF'.\n\n" +
            "8.3 Estrutura do PGR\n" +
            "• Capa com dados da empresa e do elaborador\n" +
            "• Introdução\n" +
            "• Dados da empresa\n" +
            "• Inventário de riscos (tabela com classificação)\n" +
            "• Plano de ação (tabela com status)\n" +
            "• Conclusão e assinatura\n\n" +
            "8.4 Riscos Psicossociais (obrigatório a partir de 2026)\n" +
            "A NR-01 atualizada exige a inclusão de riscos psicossociais (estresse, assédio, burnout, violência) no PGR. " +
            "O software já inclui 12 riscos psicossociais na biblioteca de perigos.");

        addCapituloWord(doc, "9. PPP - Perfil Profissiográfico Previdenciário",
            "9.1 O que é o PPP\n" +
            "O PPP é um documento que registra a exposição do trabalhador a agentes nocivos, usado para aposentadoria especial. " +
            "É gerado a partir do PGR e das medições ambientais.\n\n" +
            "9.2 Como criar um PPP\n" +
            "1. Clique em 'PPP' no menu lateral.\n" +
            "2. Clique em 'Novo PPP'.\n" +
            "3. Selecione a empresa e preencha os dados do trabalhador (nome, CPF, NIT, datas).\n" +
            "4. Preencha o cargo, setor, agente nocivo, intensidade, EPC, EPI.\n" +
            "5. Clique em 'Salvar'.\n" +
            "6. Clique em 'Gerar PPP (PDF)'.\n\n" +
            "9.3 Dados necessários\n" +
            "• Dados do trabalhador (nome, CPF, NIT, data de nascimento)\n" +
            "• Datas de admissão e demissão\n" +
            "• Cargo e setor\n" +
            "• Agente nocivo (ruído, calor, químicos, etc.)\n" +
            "• Intensidade/concentração da exposição\n" +
            "• Técnica de medição utilizada\n" +
            "• EPC (Engenharia de Controle) existente\n" +
            "• EPI (Equipamento de Proteção Individual) utilizado");

        addCapituloWord(doc, "10. Inventário de Riscos",
            "10.1 O que é o inventário de riscos\n" +
            "O inventário de riscos é a lista de todos os riscos identificados no ambiente de trabalho, " +
            "com sua classificação (probabilidade × severidade).\n\n" +
            "10.2 Como cadastrar um risco\n" +
            "1. Clique em 'Inventário de Riscos' no menu lateral.\n" +
            "2. Clique em 'Novo Risco'.\n" +
            "3. Selecione a empresa, setor e perigo (da biblioteca).\n" +
            "4. Preencha a descrição, categoria, probabilidade (1-5) e severidade (1-5).\n" +
            "5. O nível de risco e a classificação são calculados automaticamente.\n" +
            "6. Preencha as medidas existentes e propostas.\n" +
            "7. Clique em 'Salvar'.\n\n" +
            "10.3 Classificação de riscos\n" +
            "• BAIXO: nível 1-4 (verde)\n" +
            "• MÉDIO: nível 5-9 (amarelo)\n" +
            "• ALTO: nível 10-16 (laranja)\n" +
            "• CRÍTICO: nível 17-25 (vermelho)\n\n" +
            "10.4 Filtros\n" +
            "Use os filtros por empresa e classificação para encontrar riscos específicos.");

        addCapituloWord(doc, "11. Matriz de Risco",
            "A matriz de risco é uma visualização gráfica da distribuição dos riscos por probabilidade e severidade. " +
            "Ela exibe um grid 5×5 com cores indicando o nível de risco de cada célula.\n\n" +
            "Abaixo da matriz, há uma tabela com todos os riscos cadastrados, ordenados por classificação.");

        addCapituloWord(doc, "12. Plano de Ação",
            "12.1 O que é o plano de ação\n" +
            "O plano de ação define as medidas para controlar ou eliminar os riscos identificados no inventário.\n\n" +
            "12.2 Como criar uma ação\n" +
            "1. Clique em 'Plano de Ação' no menu lateral.\n" +
            "2. Clique em 'Nova Ação'.\n" +
            "3. Selecione o risco, preencha a ação, tipo, responsável, prazo e custo.\n" +
            "4. Clique em 'Salvar'.\n\n" +
            "12.3 Tipos de ação\n" +
            "• Eliminação: remover o perigo\n" +
            "• Substituição: trocar por algo menos perigoso\n" +
            "• Controle de Engenharia: modificar o ambiente\n" +
            "• Controle Administrativo: procedimentos, treinamentos\n" +
            "• EPI: Equipamento de Proteção Individual\n" +
            "• EPC: Equipamento de Proteção Coletiva\n\n" +
            "12.4 Status\n" +
            "• Pendente: não iniciado\n" +
            "• Em andamento: em execução\n" +
            "• Concluído: finalizado\n" +
            "• Cancelado: cancelado");

        addCapituloWord(doc, "13. Alertas",
            "O módulo de alertas gera notificações automáticas para:\n" +
            "• PGRs vencendo em 30 dias\n" +
            "• PGRs vencidos\n" +
            "• Ações do plano de ação atrasadas\n" +
            "• Riscos críticos identificados\n\n" +
            "Clique em 'Gerar Alertas' para criar alertas automaticamente. " +
            "Use 'Marcar Todos como Lidos' para limpar a lista.");

        addCapituloWord(doc, "14. Relatórios",
            "O módulo de relatórios gera PDFs com:\n" +
            "• Inventário de riscos por empresa\n" +
            "• Plano de ação (status)\n" +
            "• Status de conformidade (PGR)\n" +
            "• Riscos críticos (ALTO/CRÍTICO)\n" +
            "• Faturamento por período\n\n" +
            "Selecione o relatório desejado e clique no botão. O PDF será gerado e aberto automaticamente.");

        addCapituloWord(doc, "15. Manual do Usuário",
            "O software inclui um gerador de manual em PDF. Clique em 'Manual do Usuário' no menu lateral e depois em 'Gerar Manual em PDF'. " +
            "O manual inclui capa, sumário, capítulos com instruções passo a passo e dicas de boas práticas.");

        addCapituloWord(doc, "16. Proposta de Vendas",
            "O software inclui um gerador de proposta de vendas em PDF. Clique em 'Proposta de Vendas' no menu lateral e preencha os dados da empresa prospecto. " +
            "O PDF gerado inclui capa profissional, benefícios, tabela de preços, cronograma e chamada para ação. " +
            "Use este documento para apresentar seus serviços a empresas.");

        addCapituloWord(doc, "17. Configurações",
            "17.1 Como configurar\n" +
            "1. Clique em 'Configurações' no menu lateral.\n" +
            "2. Preencha seus dados (nome, registro CREA/CFT, e-mail, telefone).\n" +
            "3. Preencha o nome da empresa/consultoria.\n" +
            "4. Selecione o tema (Claro, Escuro, Nord, Dracula).\n" +
            "5. Clique em 'Salvar Configurações'.\n" +
            "6. Clique em 'Aplicar Tema' para mudar o tema em tempo real.\n\n" +
            "17.2 Temas disponíveis\n" +
            "• Claro: tema padrão (recomendado)\n" +
            "• Escuro: tema escuro para ambientes com pouca luz\n" +
            "• Nord: tema azul-acinzentado\n" +
            "• Dracula: tema roxo escuro");

        addCapituloWord(doc, "18. Backup e Restauração",
            "18.1 Exportar backup\n" +
            "1. Clique em 'Backup / Restore' no menu lateral.\n" +
            "2. Clique em 'Exportar Backup'.\n" +
            "3. Escolha o local para salvar o arquivo .db.\n" +
            "4. Aguarde a barra de progresso completar.\n\n" +
            "18.2 Restaurar backup\n" +
            "1. Clique em 'Restaurar Backup'.\n" +
            "2. Selecione o arquivo de backup.\n" +
            "3. Confirme a restauração.\n" +
            "4. Reinicie o software.\n\n" +
            "18.3 Recomendação\n" +
            "Faça backup semanalmente. Armazene os backups em local seguro (nuvem, HD externo).");

        addCapituloWord(doc, "19. Dicas de Vendas",
            "19.1 Como encontrar clientes\n" +
            "• Visite empresas da sua região (indústrias, comércios, clínicas, escritórios).\n" +
            "• Participe de grupos de empresários e associações comerciais.\n" +
            "• Use LinkedIn para prospectar empresas.\n" +
            "• Peça indicações para clientes satisfeitos.\n" +
            "• Ofereça uma consultoria inicial gratuita.\n\n" +
            "19.2 Como apresentar seus serviços\n" +
            "• Use a Proposta de Vendas (PDF gerado pelo software).\n" +
            "• Destaque a obrigatoriedade legal (NR-01).\n" +
            "• Mencione as multas evitadas (R$ 1.000 a R$ 50.000).\n" +
            "• Mostre casos de sucesso (empresas que você já atendeu).\n" +
            "• Ofereça um diagnóstico gratuito do ambiente.\n\n" +
            "19.3 Como fechar a venda\n" +
            "• Crie urgência (prazos de fiscalização).\n" +
            "• Ofereça condições de pagamento flexíveis.\n" +
            "• Dê garantia de satisfação.\n" +
            "• Faça follow-up em 3-5 dias.\n" +
            "• Peça a assinatura do contrato na hora.\n\n" +
            "19.4 Retenção de clientes\n" +
            "• Mantenha contato regular (mensal).\n" +
            "• Envie relatórios de conformidade.\n" +
            "• Ofereça serviços complementares (treinamentos, consultoria de EPI).\n" +
            "• Renove contratos antes do vencimento.\n" +
            "• Peça feedback e melhore constantemente.");

        addCapituloWord(doc, "20. Precificação de Serviços",
            "20.1 Como precificar seus serviços\n" +
            "• Calcule seu custo (tempo + deslocamento + equipamentos).\n" +
            "• Adicione margem de lucro (mínimo 50%).\n" +
            "• Considere o valor percebido pelo cliente.\n" +
            "• Compare com a concorrência (mas não seja o mais barato).\n" +
            "• Ajuste conforme o porte da empresa.\n\n" +
            "20.2 Tabela de preços sugerida\n" +
            "• PGR (Empresa Pequena, até 20 emp.): R$ 800 - R$ 1.200\n" +
            "• PGR (Empresa Média, 21-100 emp.): R$ 1.500 - R$ 2.500\n" +
            "• PGR (Empresa Grande, acima de 100): R$ 3.000 - R$ 5.000\n" +
            "• PCMSO: R$ 800 - R$ 1.500\n" +
            "• LTCAT: R$ 1.200 - R$ 2.000\n" +
            "• Consultoria de EPI: R$ 500 - R$ 1.000\n" +
            "• Inspeção de Segurança: R$ 300 - R$ 600\n" +
            "• Medição de Ruído: R$ 200 - R$ 400\n" +
            "• Treinamento NR-33: R$ 200 - R$ 300/aluno\n" +
            "• Treinamento NR-35: R$ 150 - R$ 250/aluno\n\n" +
            "20.3 Modelos de cobrança\n" +
            "• Por serviço (pontual): cobre por PGR elaborado.\n" +
            "• Recorrente (mensal): cobre manutenção mensal do PGR.\n" +
            "• Por colaborador: cobre por número de empregados.\n" +
            "• Pacote: combine PGR + PCMSO + treinamentos com desconto.\n\n" +
            "20.4 Dicas de precificação\n" +
            "• Nunca cobre menos que R$ 500 por PGR (desvaloriza o serviço).\n" +
            "• Ofereça desconto para contratos recorrentes (10-15%).\n" +
            "• Cobr extra por medições (ruído, calor, químicos).\n" +
            "• Inclua o deslocamento no preço.\n" +
            "• Revise os preços anualmente.");

        addCapituloWord(doc, "21. Estratégias de Marketing",
            "21.1 Marketing Digital\n" +
            "• Crie um site profissional com seus serviços.\n" +
            "• Use LinkedIn para prospectar empresas.\n" +
            "• Publique conteúdo sobre segurança do trabalho (blog, redes sociais).\n" +
            "• Use Google Ads para aparecer em buscas locais.\n" +
            "• Crie um perfil no Google Meu Negócio.\n\n" +
            "21.2 Marketing Offline\n" +
            "• Cartões de visita profissionais.\n" +
            "• Folders e panfletos para distribuição.\n" +
            "• Participação em feiras e eventos de SST.\n" +
            "• Parcerias com contadores e advogados trabalhistas.\n" +
            "• Visitas presenciais a empresas.\n\n" +
            "21.3 Diferenciais competitivos\n" +
            "• Use o software Gestão PGR para mostrar profissionalismo.\n" +
            "• Gere propostas em PDF com sua marca.\n" +
            "• Ofereça relatórios de conformidade.\n" +
            "• Mantenha PGR atualizado com alertas automáticos.\n" +
            "• Garanta atendimento rápido e personalizado.\n\n" +
            "21.4 Posicionamento\n" +
            "• Posicione-se como especialista, não como 'faz-tudo'.\n" +
            "• Escolha um nicho (indústria, construção, serviços).\n" +
            "• Torne-se referência na sua região.\n" +
            "• Invista em certificações e cursos.\n" +
            "• Mantenha-se atualizado com as NRs.");

        addCapituloWord(doc, "22. Solução de Problemas",
            "Problema: O software não abre.\n" +
            "Solução: Verifique se o Java 21 está instalado. Execute: java -version\n\n" +
            "Problema: Erro ao salvar dados.\n" +
            "Solução: Verifique se a pasta GestaoPGR tem permissão de escrita.\n\n" +
            "Problema: Valores com ponto/vírgula.\n" +
            "Solução: Use ponto (.) como separador decimal. Ex: 1500.00\n\n" +
            "Problema: Proposta não salva itens.\n" +
            "Solução: Cadastre tipos de serviço antes de criar propostas.\n\n" +
            "Problema: Fatura não atualiza status.\n" +
            "Solução: Registre o pagamento completo (valor igual ao valor final da fatura).\n\n" +
            "Problema: Banco de dados não criado.\n" +
            "Solução: Delete o arquivo gestaopgr.db e reinicie o software.\n\n" +
            "Problema: Erro de tabela não existe.\n" +
            "Solução: O software recria o banco automaticamente se detectar schema incompleto.\n\n" +
            "Suporte: Em caso de dúvidas, consulte este manual ou entre em contato com o desenvolvedor.");

        addCapituloWord(doc, "23. Apêndice - Tabelas de Referência",
            "23.1 Categorias de risco\n" +
            "• FÍSICO: ruído, calor, vibração, radiação, iluminação\n" +
            "• QUÍMICO: produtos químicos, poeiras, fumos\n" +
            "• BIOLÓGICO: vírus, bactérias, fungos\n" +
            "• ERGONÔMICO: postura, movimentos repetitivos, levantamento de peso\n" +
            "• ACIDENTE: quedas, cortes, choques, incêndios\n" +
            "• PSICOSSOCIAL: estresse, assédio, burnout, violência\n\n" +
            "23.2 NRs mais relevantes para PGR\n" +
            "• NR-01: Disposições Gerais e Gerenciamento de Riscos\n" +
            "• NR-06: Equipamento de Proteção Individual\n" +
            "• NR-07: Programa de Controle Médico de Saúde Ocupacional\n" +
            "• NR-09: Avaliação e Controle de Exposições Ocupacionais\n" +
            "• NR-12: Segurança no Trabalho em Máquinas e Equipamentos\n" +
            "• NR-15: Atividades e Operações Insalubres\n" +
            "• NR-16: Atividades e Operações Perigosas\n" +
            "• NR-17: Ergonomia\n" +
            "• NR-18: Segurança na Construção Civil\n" +
            "• NR-33: Trabalho em Espaços Confinados\n" +
            "• NR-35: Trabalho em Altura\n\n" +
            "23.3 Classificação de riscos\n" +
            "• BAIXO: nível 1-4 (verde)\n" +
            "• MÉDIO: nível 5-9 (amarelo)\n" +
            "• ALTO: nível 10-16 (laranja)\n" +
            "• CRÍTICO: nível 17-25 (vermelho)\n\n" +
            "23.4 Status de PGR\n" +
            "• EM ELABORAÇÃO: em desenvolvimento\n" +
            "• REVISÃO: em revisão\n" +
            "• APROVADO: aprovado e vigente\n" +
            "• VENCIDO: prazo de validade expirado");

        doc.write(new FileOutputStream(path));
        doc.close();
    }

    private static void addCapituloWord(XWPFDocument doc, String titulo, String texto) {
        XWPFParagraph pTitle = doc.createParagraph();
        XWPFRun rTitle = pTitle.createRun();
        rTitle.setText(titulo);
        rTitle.setBold(true);
        rTitle.setFontSize(14);
        rTitle.setFontFamily("Calibri");

        for (String para : texto.split("\n\n")) {
            XWPFParagraph p = doc.createParagraph();
            XWPFRun r = p.createRun();
            r.setText(para.trim());
            r.setFontSize(11);
            r.setFontFamily("Calibri");
        }
        doc.createParagraph();
    }

    // ==================== PDF ====================
    private static void gerarPdf(String path) throws Exception {
        com.lowagie.text.Document doc = new com.lowagie.text.Document(PageSize.A4, 50, 50, 60, 60);
        PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(path));

        writer.setPageEvent(new PdfPageEventHelper() {
            public void onEndPage(PdfWriter w, com.lowagie.text.Document d) {
                try {
                    PdfContentByte cb = w.getDirectContent();
                    cb.beginText();
                    BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, false);
                    cb.setFontAndSize(bf, 8);
                    cb.setTextMatrix(50, 25);
                    cb.showText("Manual de Vendas - Gestao PGR  |  Pagina " + w.getPageNumber());
                    cb.endText();
                } catch (Exception ignored) {}
            }
        });

        doc.open();

        com.lowagie.text.Font titleFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 24, com.lowagie.text.Font.BOLD);
        com.lowagie.text.Font h1 = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 14, com.lowagie.text.Font.BOLD);
        com.lowagie.text.Font body = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 10, com.lowagie.text.Font.NORMAL);

        // Capa
        Paragraph t = new Paragraph("MANUAL DE VENDAS E USO", titleFont);
        t.setAlignment(Element.ALIGN_CENTER);
        doc.add(t);
        Paragraph sub = new Paragraph("Sistema Gestao PGR - Programa de Gerenciamento de Riscos", new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 14, com.lowagie.text.Font.NORMAL));
        sub.setAlignment(Element.ALIGN_CENTER);
        doc.add(sub);
        doc.add(new Paragraph(" "));
        Paragraph info = new Paragraph("Versao 1.0\nDesenvolvido por Maxwell Souza dos Santos\nCNPJ: 54.718.287/0001-19\n© 2026 - Todos os direitos reservados", body);
        info.setAlignment(Element.ALIGN_CENTER);
        doc.add(info);
        doc.add(new Paragraph(" "));
        Paragraph conf = new Paragraph("Documento confidencial - Uso interno", new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 9, com.lowagie.text.Font.ITALIC));
        conf.setAlignment(Element.ALIGN_CENTER);
        doc.add(conf);

        doc.newPage();

        // Sumário
        doc.add(new Paragraph("SUMARIO", h1));
        doc.add(new Paragraph(" "));
        for (String s : SUMARIO) {
            doc.add(new Paragraph(s, body));
        }

        doc.newPage();

        // Capítulos (resumidos para PDF)
        String[] capitulos = {
            "1. Introducao", "2. Primeiros Passos", "3. Cadastro de Empresas",
            "4. Tipos de Servico e Precos", "5. Propostas e Orcamentos",
            "6. Contratos", "7. Faturas e Pagamentos", "8. PGR",
            "9. PPP", "10. Inventario de Riscos", "11. Matriz de Risco",
            "12. Plano de Acao", "13. Alertas", "14. Relatorios",
            "15. Manual do Usuario", "16. Proposta de Vendas",
            "17. Configuracoes", "18. Backup e Restauracao",
            "19. Dicas de Vendas", "20. Precificacao",
            "21. Estrategias de Marketing", "22. Solucao de Problemas",
            "23. Apendice"
        };

        for (String cap : capitulos) {
            Paragraph p = new Paragraph(cap, h1);
            p.setSpacingBefore(10);
            doc.add(p);
            doc.add(new Paragraph("Conteudo detalhado disponivel no arquivo Word (.docx).", body));
            doc.add(new Paragraph(" "));
        }

        doc.close();
    }
}

