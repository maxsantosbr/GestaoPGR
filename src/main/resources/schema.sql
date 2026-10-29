-- Schema do banco SQLite para Gestão de PGR
-- Versão 1.0

PRAGMA foreign_keys = ON;

-- ==================== EMPRESAS ====================
CREATE TABLE IF NOT EXISTS empresa (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    razao_social TEXT NOT NULL,
    nome_fantasia TEXT,
    cnpj TEXT,
    cnae TEXT,
    cnae_descricao TEXT,
    endereco TEXT,
    cidade TEXT,
    uf TEXT,
    cep TEXT,
    telefone TEXT,
    email TEXT,
    responsavel TEXT,
    num_empregados INTEGER DEFAULT 0,
    logo_path TEXT,
    ativo INTEGER NOT NULL DEFAULT 1,
    created_at TEXT NOT NULL DEFAULT (datetime('now')),
    updated_at TEXT NOT NULL DEFAULT (datetime('now'))
);

-- ==================== SETORES / AMBIENTES ====================
CREATE TABLE IF NOT EXISTS setor (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    empresa_id INTEGER NOT NULL,
    nome TEXT NOT NULL,
    descricao TEXT,
    num_trabalhadores INTEGER DEFAULT 0,
    ativo INTEGER NOT NULL DEFAULT 1,
    created_at TEXT NOT NULL DEFAULT (datetime('now')),
    FOREIGN KEY (empresa_id) REFERENCES empresa(id)
);

-- ==================== CARGOS / FUNÇÕES ====================
CREATE TABLE IF NOT EXISTS cargo (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    empresa_id INTEGER NOT NULL,
    nome TEXT NOT NULL,
    cbo TEXT,
    descricao TEXT,
    ativo INTEGER NOT NULL DEFAULT 1,
    created_at TEXT NOT NULL DEFAULT (datetime('now')),
    FOREIGN KEY (empresa_id) REFERENCES empresa(id)
);

-- ==================== BIBLIOTECA DE PERIGOS ====================
CREATE TABLE IF NOT EXISTS perigo (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    codigo TEXT,
    nome TEXT NOT NULL,
    categoria TEXT NOT NULL,
    descricao TEXT,
    fonte TEXT,
    medidas_controle TEXT,
    epi_recomendado TEXT,
    nr_referencia TEXT,
    ativo INTEGER NOT NULL DEFAULT 1
);

-- ==================== INVENTÁRIO DE RISCOS ====================
CREATE TABLE IF NOT EXISTS risco (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    empresa_id INTEGER NOT NULL,
    setor_id INTEGER,
    cargo_id INTEGER,
    perigo_id INTEGER,
    perigo_descricao TEXT NOT NULL,
    categoria TEXT NOT NULL,
    fonte TEXT,
    atividade TEXT,
    num_trabalhadores_expostos INTEGER DEFAULT 0,
    probabilidade INTEGER NOT NULL DEFAULT 1,
    severidade INTEGER NOT NULL DEFAULT 1,
    nivel_risco INTEGER,
    classificacao TEXT,
    medidas_existentes TEXT,
    medidas_propostas TEXT,
    epi_recomendado TEXT,
    status TEXT NOT NULL DEFAULT 'IDENTIFICADO',
    created_at TEXT NOT NULL DEFAULT (datetime('now')),
    updated_at TEXT NOT NULL DEFAULT (datetime('now')),
    FOREIGN KEY (empresa_id) REFERENCES empresa(id),
    FOREIGN KEY (setor_id) REFERENCES setor(id),
    FOREIGN KEY (cargo_id) REFERENCES cargo(id),
    FOREIGN KEY (perigo_id) REFERENCES perigo(id)
);

-- ==================== PLANO DE AÇÃO ====================
CREATE TABLE IF NOT EXISTS plano_acao (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    risco_id INTEGER NOT NULL,
    empresa_id INTEGER NOT NULL,
    acao TEXT NOT NULL,
    tipo TEXT NOT NULL,
    responsavel TEXT,
    prazo TEXT,
    custo_estimado REAL,
    status TEXT NOT NULL DEFAULT 'PENDENTE',
    data_conclusao TEXT,
    observacao TEXT,
    created_at TEXT NOT NULL DEFAULT (datetime('now')),
    updated_at TEXT NOT NULL DEFAULT (datetime('now')),
    FOREIGN KEY (risco_id) REFERENCES risco(id),
    FOREIGN KEY (empresa_id) REFERENCES empresa(id)
);

-- ==================== PGR ====================
CREATE TABLE IF NOT EXISTS pgr (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    empresa_id INTEGER NOT NULL,
    codigo TEXT,
    titulo TEXT NOT NULL,
    versao INTEGER NOT NULL DEFAULT 1,
    data_elaboracao TEXT NOT NULL,
    data_validade TEXT,
    elaborador TEXT,
    elaborador_registro TEXT,
    status TEXT NOT NULL DEFAULT 'EM_ELABORACAO',
    observacoes TEXT,
    pdf_path TEXT,
    created_at TEXT NOT NULL DEFAULT (datetime('now')),
    updated_at TEXT NOT NULL DEFAULT (datetime('now')),
    FOREIGN KEY (empresa_id) REFERENCES empresa(id)
);

-- ==================== PGR VERSÕES ====================
CREATE TABLE IF NOT EXISTS pgr_versao (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    pgr_id INTEGER NOT NULL,
    versao INTEGER NOT NULL,
    data TEXT NOT NULL,
    alteracoes TEXT,
    pdf_path TEXT,
    created_at TEXT NOT NULL DEFAULT (datetime('now')),
    FOREIGN KEY (pgr_id) REFERENCES pgr(id)
);

-- ==================== ALERTAS ====================
CREATE TABLE IF NOT EXISTS alerta (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    empresa_id INTEGER,
    tipo TEXT NOT NULL,
    mensagem TEXT NOT NULL,
    nivel TEXT NOT NULL DEFAULT 'INFO',
    lido INTEGER NOT NULL DEFAULT 0,
    data_criacao TEXT NOT NULL DEFAULT (datetime('now')),
    FOREIGN KEY (empresa_id) REFERENCES empresa(id)
);

-- ==================== USUÁRIOS ====================
CREATE TABLE IF NOT EXISTS usuario (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    login TEXT NOT NULL UNIQUE,
    senha_hash TEXT NOT NULL,
    nome TEXT NOT NULL,
    email TEXT,
    perfil TEXT NOT NULL DEFAULT 'TECNICO',
    ativo INTEGER NOT NULL DEFAULT 1,
    created_at TEXT NOT NULL DEFAULT (datetime('now'))
);

-- ==================== TIPOS DE SERVIÇO ====================
CREATE TABLE IF NOT EXISTS tipo_servico (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT NOT NULL,
    descricao TEXT,
    preco_base REAL NOT NULL DEFAULT 0,
    unidade TEXT NOT NULL DEFAULT 'UN',
    ativo INTEGER NOT NULL DEFAULT 1,
    created_at TEXT NOT NULL DEFAULT (datetime('now'))
);

-- ==================== PROPOSTAS ====================
CREATE TABLE IF NOT EXISTS proposta (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    empresa_id INTEGER NOT NULL,
    codigo TEXT,
    titulo TEXT NOT NULL,
    descricao TEXT,
    status TEXT NOT NULL DEFAULT 'RASCUNHO',
    data_criacao TEXT NOT NULL DEFAULT (datetime('now')),
    data_validade TEXT,
    valor_total REAL NOT NULL DEFAULT 0,
    desconto REAL DEFAULT 0,
    valor_final REAL NOT NULL DEFAULT 0,
    condicoes_pagamento TEXT,
    observacoes TEXT,
    created_at TEXT NOT NULL DEFAULT (datetime('now')),
    updated_at TEXT NOT NULL DEFAULT (datetime('now')),
    FOREIGN KEY (empresa_id) REFERENCES empresa(id)
);

CREATE TABLE IF NOT EXISTS proposta_item (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    proposta_id INTEGER NOT NULL,
    tipo_servico_id INTEGER,
    descricao TEXT NOT NULL,
    quantidade REAL NOT NULL DEFAULT 1,
    valor_unitario REAL NOT NULL DEFAULT 0,
    valor_total REAL NOT NULL DEFAULT 0,
    FOREIGN KEY (proposta_id) REFERENCES proposta(id),
    FOREIGN KEY (tipo_servico_id) REFERENCES tipo_servico(id)
);

-- ==================== CONTRATOS ====================
CREATE TABLE IF NOT EXISTS contrato (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    empresa_id INTEGER NOT NULL,
    proposta_id INTEGER,
    codigo TEXT,
    titulo TEXT NOT NULL,
    descricao TEXT,
    tipo TEXT NOT NULL DEFAULT 'PONTUAL',
    status TEXT NOT NULL DEFAULT 'ATIVO',
    data_inicio TEXT NOT NULL,
    data_fim TEXT,
    valor_total REAL NOT NULL DEFAULT 0,
    valor_recorrente REAL DEFAULT 0,
    periodicidade TEXT,
    dia_vencimento INTEGER,
    observacoes TEXT,
    created_at TEXT NOT NULL DEFAULT (datetime('now')),
    updated_at TEXT NOT NULL DEFAULT (datetime('now')),
    FOREIGN KEY (empresa_id) REFERENCES empresa(id),
    FOREIGN KEY (proposta_id) REFERENCES proposta(id)
);

-- ==================== FATURAS ====================
CREATE TABLE IF NOT EXISTS fatura (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    empresa_id INTEGER NOT NULL,
    contrato_id INTEGER,
    proposta_id INTEGER,
    codigo TEXT,
    descricao TEXT NOT NULL,
    data_emissao TEXT NOT NULL DEFAULT (datetime('now')),
    data_vencimento TEXT NOT NULL,
    valor REAL NOT NULL DEFAULT 0,
    desconto REAL DEFAULT 0,
    valor_final REAL NOT NULL DEFAULT 0,
    status TEXT NOT NULL DEFAULT 'PENDENTE',
    observacoes TEXT,
    created_at TEXT NOT NULL DEFAULT (datetime('now')),
    updated_at TEXT NOT NULL DEFAULT (datetime('now')),
    FOREIGN KEY (empresa_id) REFERENCES empresa(id),
    FOREIGN KEY (contrato_id) REFERENCES contrato(id),
    FOREIGN KEY (proposta_id) REFERENCES proposta(id)
);

-- ==================== PAGAMENTOS ====================
CREATE TABLE IF NOT EXISTS pagamento (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    fatura_id INTEGER NOT NULL,
    empresa_id INTEGER NOT NULL,
    data_pagamento TEXT NOT NULL,
    valor REAL NOT NULL,
    forma_pagamento TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'CONFIRMADO',
    comprovante_path TEXT,
    observacao TEXT,
    created_at TEXT NOT NULL DEFAULT (datetime('now')),
    FOREIGN KEY (fatura_id) REFERENCES fatura(id),
    FOREIGN KEY (empresa_id) REFERENCES empresa(id)
);

-- ==================== FORMAS DE PAGAMENTO ====================
CREATE TABLE IF NOT EXISTS forma_pagamento (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT NOT NULL,
    ativo INTEGER NOT NULL DEFAULT 1
);

-- ==================== PPP ====================
CREATE TABLE IF NOT EXISTS ppp (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    empresa_id INTEGER NOT NULL,
    trabalhador_nome TEXT NOT NULL,
    trabalhador_cpf TEXT,
    trabalhador_nit TEXT,
    data_nascimento TEXT,
    data_admissao TEXT,
    data_demissao TEXT,
    cargo TEXT,
    setor TEXT,
    agente_nocivo TEXT,
    intensidade TEXT,
    tecnica_medicao TEXT,
    epc TEXT,
    epi TEXT,
    resultado_medicao TEXT,
    data_ppp TEXT NOT NULL,
    observacoes TEXT,
    pdf_path TEXT,
    created_at TEXT NOT NULL DEFAULT (datetime('now')),
    updated_at TEXT NOT NULL DEFAULT (datetime('now')),
    FOREIGN KEY (empresa_id) REFERENCES empresa(id)
);

-- ==================== CONFIGURAÇÕES ====================
CREATE TABLE IF NOT EXISTS configuracao (
    chave TEXT PRIMARY KEY,
    valor TEXT,
    updated_at TEXT NOT NULL DEFAULT (datetime('now'))
);

-- ==================== ÍNDICES ====================
CREATE INDEX IF NOT EXISTS idx_empresa_ativo ON empresa(ativo);
CREATE INDEX IF NOT EXISTS idx_setor_empresa ON setor(empresa_id);
CREATE INDEX IF NOT EXISTS idx_risco_empresa ON risco(empresa_id);
CREATE INDEX IF NOT EXISTS idx_risco_setor ON risco(setor_id);
CREATE INDEX IF NOT EXISTS idx_risco_classificacao ON risco(classificacao);
CREATE INDEX IF NOT EXISTS idx_plano_risco ON plano_acao(risco_id);
CREATE INDEX IF NOT EXISTS idx_plano_status ON plano_acao(status);
CREATE INDEX IF NOT EXISTS idx_pgr_empresa ON pgr(empresa_id);
CREATE INDEX IF NOT EXISTS idx_pgr_status ON pgr(status);
CREATE INDEX IF NOT EXISTS idx_alerta_lido ON alerta(lido);
CREATE INDEX IF NOT EXISTS idx_proposta_empresa ON proposta(empresa_id);
CREATE INDEX IF NOT EXISTS idx_proposta_status ON proposta(status);
CREATE INDEX IF NOT EXISTS idx_contrato_empresa ON contrato(empresa_id);
CREATE INDEX IF NOT EXISTS idx_contrato_status ON contrato(status);
CREATE INDEX IF NOT EXISTS idx_fatura_empresa ON fatura(empresa_id);
CREATE INDEX IF NOT EXISTS idx_fatura_status ON fatura(status);
CREATE INDEX IF NOT EXISTS idx_fatura_vencimento ON fatura(data_vencimento);
CREATE INDEX IF NOT EXISTS idx_pagamento_fatura ON pagamento(fatura_id);
CREATE INDEX IF NOT EXISTS idx_pagamento_empresa ON pagamento(empresa_id);
CREATE INDEX IF NOT EXISTS idx_ppp_empresa ON ppp(empresa_id);

-- ==================== DADOS INICIAIS ====================
INSERT OR IGNORE INTO forma_pagamento (id, nome) VALUES
(1, 'PIX'), (2, 'Boleto'), (3, 'Cartão de Crédito'), (4, 'Cartão de Débito'),
(5, 'Transferência Bancária'), (6, 'Dinheiro'), (7, 'Cheque');

INSERT OR IGNORE INTO tipo_servico (id, nome, descricao, preco_base, unidade) VALUES
(1, 'Elaboração de PGR', 'Programa de Gerenciamento de Riscos completo', 1500.00, 'EMPRESA'),
(2, 'Elaboração de PCMSO', 'Programa de Controle Médico de Saúde Ocupacional', 800.00, 'EMPRESA'),
(3, 'Elaboração de LTCAT', 'Laudo Técnico das Condições Ambientais do Trabalho', 1200.00, 'EMPRESA'),
(4, 'Treinamento NR-10', 'Treinamento de segurança em eletricidade (8h)', 150.00, 'UN'),
(5, 'Treinamento NR-11', 'Treinamento de transporte e movimentação (8h)', 120.00, 'UN'),
(6, 'Treinamento NR-12', 'Treinamento de segurança em máquinas (8h)', 120.00, 'UN'),
(7, 'Treinamento NR-33', 'Treinamento de espaço confinado (16h)', 250.00, 'UN'),
(8, 'Treinamento NR-35', 'Treinamento de trabalho em altura (8h)', 150.00, 'UN'),
(9, 'Consultoria de EPI', 'Avaliação e adequação de EPIs por cargo', 500.00, 'EMPRESA'),
(10, 'Inspeção de Segurança', 'Inspeção de segurança do trabalho', 300.00, 'UN'),
(11, 'Medição de Ruído', 'Medição de níveis de ruído (NR-15)', 200.00, 'UN'),
(12, 'Medição de Calor (IBGT)', 'Medição de calor (NR-15)', 200.00, 'UN'),
(13, 'Elaboração de PGR (Empresa Pequena)', 'PGR para empresas até 20 empregados', 800.00, 'EMPRESA'),
(14, 'Elaboração de PGR (Empresa Média)', 'PGR para empresas 21-100 empregados', 1500.00, 'EMPRESA'),
(15, 'Elaboração de PGR (Empresa Grande)', 'PGR para empresas acima de 100 empregados', 3000.00, 'EMPRESA');

INSERT OR IGNORE INTO perigo (id, codigo, nome, categoria, descricao, medidas_controle, nr_referencia) VALUES
(1, 'F001', 'Ruído', 'FISICO', 'Exposição a níveis de ruído acima de 80 dB(A)', 'Protetor auricular, enclausuramento, afastamento', 'NR-15, NR-06'),
(2, 'F002', 'Calor', 'FISICO', 'Exposição a calor excessivo (IBUTG acima do limite)', 'Ventilação, hidratação, pausas, vestimenta adequada', 'NR-15'),
(3, 'F003', 'Vibração', 'FISICO', 'Exposição a vibração de mãos/braços ou corpo inteiro', 'Luvas antivibração, manutenção de equipamentos', 'NR-15'),
(4, 'F004', 'Iluminação inadequada', 'FISICO', 'Níveis de iluminação abaixo ou acima do recomendado', 'Adequação da iluminação, manutenção de luminárias', 'NR-17'),
(5, 'F005', 'Radiação não ionizante', 'FISICO', 'Exposição a radiação UV, infravermelho, laser', 'Protetor solar, óculos, vestimenta', 'NR-15'),
(6, 'Q001', 'Produtos químicos', 'QUIMICO', 'Exposição a solventes, ácidos, bases, tintas', 'EPI, ventilação, substituição por produto menos tóxico', 'NR-15'),
(7, 'Q002', 'Poeira', 'QUIMICO', 'Exposição a poeiras (sílica, madeira, cimento)', 'Máscara respiratória, aspiração, ventilação', 'NR-15'),
(8, 'B001', 'Agentes biológicos', 'BIOLOGICO', 'Exposição a vírus, bactérias, fungos', 'EPI, higienização, vacinação', 'NR-32'),
(9, 'E001', 'Postura inadequada', 'ERGONOMICO', 'Trabalho em posturas forçadas ou estáticas', 'Ajuste de mobiliário, ginástica laboral, pausas', 'NR-17'),
(10, 'E002', 'Movimentos repetitivos', 'ERGONOMICO', 'Atividades com gestos repetitivos (LER/DORT)', 'Pausas, rodízio de tarefas, ginástica', 'NR-17'),
(11, 'E003', 'Levantamento de peso', 'ERGONOMICO', 'Manuseio manual de cargas', 'Treinamento, auxílio mecânico, limites de peso', 'NR-17'),
(12, 'E004', 'Trabalho em pé prolongado', 'ERGONOMICO', 'Permanência em pé por longos períodos', 'Assento de descanso, tapete anti-fadiga, pausas', 'NR-17'),
(13, 'A001', 'Queda de altura', 'ACIDENTE', 'Risco de queda de andaimes, telhados, escadas', 'Cinto de segurança, guarda-corpo, linha de vida', 'NR-35'),
(14, 'A002', 'Queda ao mesmo nível', 'ACIDENTE', 'Pisos escorregadios, obstáculos, desníveis', 'Sinalização, limpeza, piso antiderrapante', 'NR-24'),
(15, 'A003', 'Corte/laceração', 'ACIDENTE', 'Uso de ferramentas cortantes, máquinas', 'Luvas de proteção, protetor de máquina, treinamento', 'NR-12'),
(16, 'A004', 'Choque elétrico', 'ACIDENTE', 'Contato com redes elétricas, equipamentos', 'Desenergização, EPI, sinalização', 'NR-10'),
(17, 'A005', 'Incêndio/explosão', 'ACIDENTE', 'Materiais inflamáveis, curto-circuito', 'Extintores, sinalização, treinamento', 'NR-23'),
(18, 'A006', 'Atropelamento', 'ACIDENTE', 'Movimentação de veículos, empilhadeiras', 'Sinalização, rotas segregadas, treinamento', 'NR-11'),
(19, 'P001', 'Estresse ocupacional', 'PSICOSSOCIAL', 'Exposição a fatores de estresse no trabalho (pressão por metas, sobrecarga, prazos)', 'Gestão de carga de trabalho, apoio psicológico, pausas, rodízio', 'NR-01, NR-17'),
(20, 'P002', 'Assédio moral', 'PSICOSSOCIAL', 'Comportamentos abusivos, humilhações, perseguição no ambiente de trabalho', 'Canal de denúncia, treinamento, código de conduta, mediação', 'NR-01'),
(21, 'P003', 'Assédio sexual', 'PSICOSSOCIAL', 'Comportamentos de conotação sexual não desejados', 'Canal de denúncia, treinamento, código de conduta, política de tolerância zero', 'NR-01'),
(22, 'P004', 'Síndrome de Burnout', 'PSICOSSOCIAL', 'Esgotamento físico e mental por estresse crônico no trabalho', 'Gestão de carga, pausas, apoio psicológico, rodízio de tarefas', 'NR-01, NR-17'),
(23, 'P005', 'Violência no trabalho', 'PSICOSSOCIAL', 'Agressões físicas ou verbais no ambiente de trabalho', 'Segurança, canal de denúncia, treinamento, protocolo de atendimento', 'NR-01'),
(24, 'P006', 'Sobrecarga de trabalho', 'PSICOSSOCIAL', 'Excesso de demandas e responsabilidades', 'Gestão de carga, priorização, delegação, contratação', 'NR-01, NR-17'),
(25, 'P007', 'Pressão por metas', 'PSICOSSOCIAL', 'Pressão excessiva por resultados e metas', 'Metas realistas, acompanhamento, feedback, apoio', 'NR-01'),
(26, 'P008', 'Ausência de pausas', 'PSICOSSOCIAL', 'Falta de intervalos e pausas durante a jornada', 'Programação de pausas, rodízio, adequação de jornada', 'NR-01, NR-17'),
(27, 'P009', 'Falta de suporte da liderança', 'PSICOSSOCIAL', 'Ausência de apoio, feedback e reconhecimento da gestão', 'Treinamento de liderança, comunicação, feedback', 'NR-01'),
(28, 'P010', 'Jornada excessiva', 'PSICOSSOCIAL', 'Horas extras frequentes e jornadas prolongadas', 'Controle de jornada, adequação de escalas, contratação', 'NR-01, NR-17'),
(29, 'P011', 'Conflito de papéis', 'PSICOSSOCIAL', 'Ambiguidade ou conflito nas atribuições do cargo', 'Descrição de cargos, clareza de funções, comunicação', 'NR-01'),
(30, 'P012', 'Isolamento social', 'PSICOSSOCIAL', 'Trabalho isolado, sem interação com equipe', 'Integração, comunicação, trabalho em equipe', 'NR-01');

INSERT OR IGNORE INTO configuracao (chave, valor) VALUES
('tema', 'claro'),
('tecnico_nome', ''),
('tecnico_registro', ''),
('tecnico_email', ''),
('tecnico_telefone', ''),
('empresa_nome', ''),
('backup_auto', 'true'),
('backup_dias', '7');

INSERT OR IGNORE INTO usuario (id, login, senha_hash, nome, perfil) VALUES
(1, 'admin', 'admin123', 'Administrador', 'ADMIN');
