-- Insert a default admin user for testing (password: admin123)
-- The password hash is for 'admin123' using BCrypt
INSERT INTO usuarios (username, password_hash, nome_completo, email, ativo, data_criacao) 
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXiGiNa1j6W6.F.OT.TDNbqAjya', 'Administrator', 'admin@example.com', true, CURRENT_TIMESTAMP)
ON CONFLICT (username) DO NOTHING;

-- Insert some sample recipes for testing
INSERT INTO receitas (nome, descricao, tempo_preparo_min, porcoes, dificuldade, usuario_id)
SELECT 'Bolo de Chocolate', 'Um delicioso bolo de chocolate caseiro', 60, 8, 'Médio', u.id
FROM usuarios u WHERE u.username = 'admin'
ON CONFLICT DO NOTHING;

-- Get the recipe ID for ingredients and steps
INSERT INTO ingredientes (receita_id, nome, quantidade, unidade)
SELECT r.id, 'Farinha de trigo', 2.0, 'xícaras'
FROM receitas r WHERE r.nome = 'Bolo de Chocolate'
ON CONFLICT DO NOTHING;

INSERT INTO ingredientes (receita_id, nome, quantidade, unidade)
SELECT r.id, 'Chocolate em pó', 0.5, 'xícara'
FROM receitas r WHERE r.nome = 'Bolo de Chocolate'
ON CONFLICT DO NOTHING;

INSERT INTO ingredientes (receita_id, nome, quantidade, unidade)
SELECT r.id, 'Açúcar', 1.5, 'xícaras'
FROM receitas r WHERE r.nome = 'Bolo de Chocolate'
ON CONFLICT DO NOTHING;

INSERT INTO passos (receita_id, ordem, descricao)
SELECT r.id, 1, 'Pré-aqueça o forno a 180°C'
FROM receitas r WHERE r.nome = 'Bolo de Chocolate'
ON CONFLICT DO NOTHING;

INSERT INTO passos (receita_id, ordem, descricao)
SELECT r.id, 2, 'Misture os ingredientes secos em uma tigela'
FROM receitas r WHERE r.nome = 'Bolo de Chocolate'
ON CONFLICT DO NOTHING;

INSERT INTO passos (receita_id, ordem, descricao)
SELECT r.id, 3, 'Adicione os ingredientes líquidos e misture bem'
FROM receitas r WHERE r.nome = 'Bolo de Chocolate'
ON CONFLICT DO NOTHING;

INSERT INTO passos (receita_id, ordem, descricao)
SELECT r.id, 4, 'Despeje a massa na forma e asse por 45 minutos'
FROM receitas r WHERE r.nome = 'Bolo de Chocolate'
ON CONFLICT DO NOTHING;
