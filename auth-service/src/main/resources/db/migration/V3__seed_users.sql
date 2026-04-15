-- Seed users for testing. BCrypt hash corresponds to password: senha123
INSERT INTO auth.users (cpf, password_hash, name, role) VALUES
    ('12345678901', '$2a$10$n7zlE.Ryo66BzHtdkk5LLOf9TPY62qUmVWAk0SQfLDWyAqhcjzPeG', 'Paciente Teste', 'PATIENT'),
    ('12312356901', '$2a$10$n7zlE.Ryo66BzHtdkk5LLOf9TPY62qUmVWAk0SQfLDWyAqhcjzPeG', 'Paciente 2 Teste', 'PATIENT'),
    ('98765432100', '$2a$10$n7zlE.Ryo66BzHtdkk5LLOf9TPY62qUmVWAk0SQfLDWyAqhcjzPeG', 'Revisor Teste',  'REVIEWER'),
    ('11122233344', '$2a$10$n7zlE.Ryo66BzHtdkk5LLOf9TPY62qUmVWAk0SQfLDWyAqhcjzPeG', 'Admin Teste',    'ADMIN')
ON CONFLICT (cpf) DO NOTHING;
