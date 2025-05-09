DELETE FROM account;
ALTER TABLE account ALTER COLUMN id RESTART WITH 1;

INSERT INTO account (id, created_at, name, email, password, birth, photo, cpf, address, tel, role, status, metadata) VALUES
(1, CURRENT_TIMESTAMP - 111, 'Alice Silva', 'alice@example.com', '$2a$10$BHK0MMBsx/2EAuWLkkG6J.GCNq790dkJL.Iwvknrnx7eI/rFZt9da', '1995-03-15', 'https://randomuser.me/api/portraits/women/11.jpg', '12345678901', 'Rua das Flores, 123', '11999990001', 'ADMIN', 'ON', '{}'),
(2, CURRENT_TIMESTAMP - 105, 'Bruno Lima', 'bruno@example.com', '$2a$10$BHK0MMBsx/2EAuWLkkG6J.GCNq790dkJL.Iwvknrnx7eI/rFZt9da', '1988-07-22', 'https://randomuser.me/api/portraits/men/22.jpg', '23456789012', 'Av. Brasil, 456', '11999990002', 'ADMIN', 'ON', '{}'),
(3, CURRENT_TIMESTAMP - 91, 'Carla Souza', 'carla@example.com', '$2a$10$BHK0MMBsx/2EAuWLkkG6J.GCNq790dkJL.Iwvknrnx7eI/rFZt9da', '1992-01-10', 'https://randomuser.me/api/portraits/women/33.jpg', '34567890123', 'Rua Central, 789', '11999990003', 'OPERATOR', 'ON', '{}'),
(4, CURRENT_TIMESTAMP - 89, 'Diego Alves', 'diego@example.com', '$2a$10$BHK0MMBsx/2EAuWLkkG6J.GCNq790dkJL.Iwvknrnx7eI/rFZt9da', '1990-05-18', 'https://randomuser.me/api/portraits/men/44.jpg', '45678901234', 'Av. Paulista, 321', '11999990004', 'OPERATOR', 'ON', '{}'),
(5, CURRENT_TIMESTAMP - 75, 'Elaine Costa', 'elaine@example.com', '$2a$10$BHK0MMBsx/2EAuWLkkG6J.GCNq790dkJL.Iwvknrnx7eI/rFZt9da', '1997-11-30', 'https://randomuser.me/api/portraits/women/55.jpg', '56789012345', 'Rua das Acácias, 654', '11999990005', 'OPERATOR', 'ON', '{}'),
(6, CURRENT_TIMESTAMP - 56, 'Fabio Rocha', 'fabio@example.com', '$2a$10$BHK0MMBsx/2EAuWLkkG6J.GCNq790dkJL.Iwvknrnx7eI/rFZt9da', '1991-08-25', 'https://randomuser.me/api/portraits/men/66.jpg', '67890123456', 'Rua Bela Vista, 987', '11999990006', 'USER', 'ON', '{}'),
(7, CURRENT_TIMESTAMP - 44, 'Gabriela Mendes', 'gabriela@example.com', '$2a$10$BHK0MMBsx/2EAuWLkkG6J.GCNq790dkJL.Iwvknrnx7eI/rFZt9da', '1996-09-12', 'https://randomuser.me/api/portraits/women/77.jpg', '78901234567', 'Rua do Sol, 147', '11999990007', 'USER', 'ON', '{}'),
(8, CURRENT_TIMESTAMP - 33, 'Henrique Dias', 'henrique@example.com', '$2a$10$BHK0MMBsx/2EAuWLkkG6J.GCNq790dkJL.Iwvknrnx7eI/rFZt9da', '1985-02-28', 'https://randomuser.me/api/portraits/men/88.jpg', '89012345678', 'Av. Liberdade, 258', '11999990008', 'USER', 'ON', '{}'),
(9, CURRENT_TIMESTAMP - 22, 'Isabela Ferreira', 'isabela@example.com', '$2a$10$BHK0MMBsx/2EAuWLkkG6J.GCNq790dkJL.Iwvknrnx7eI/rFZt9da', '1994-06-05', 'https://randomuser.me/api/portraits/women/99.jpg', '90123456789', 'Rua Harmonia, 369', '11999990009', 'USER', 'ON', '{}'),
(10, CURRENT_TIMESTAMP - 11, 'João Pedro', 'joao@example.com', '$2a$10$BHK0MMBsx/2EAuWLkkG6J.GCNq790dkJL.Iwvknrnx7eI/rFZt9da', '1993-12-03', 'https://randomuser.me/api/portraits/men/01.jpg', '01234567890', 'Av. da Paz, 159', '11999990010', 'USER', 'ON', '{}');

DELETE FROM config;
ALTER TABLE config ALTER COLUMN id RESTART WITH 1;

INSERT INTO config (var_name, var_value, description) VALUES
('cookieMaxAge', '48', 'Tempo de vida do cookie em horas'),
('tokenMaxAge', '48', 'Tempo de vida do token JWT em horas'),
('secretKey', 'xcmzyCqUEFvjq1dU6hHje4slO9mI0A0K8RIcVddJklY=y44fn898gt308htv32ht4108h32gd', 'Chave secreta para geração de tokens JWT ou similares'),
('uploadDir', './uploads/', 'Diretório local para salvar arquivos enviados'),
('uploadUrl', '/uploads/', 'URL base para acesso aos arquivos enviados'),
('appName', 'Book''s Vanilla', 'Nome do sistema para exibição em páginas'),
('defaultUserPhoto', 'https://randomuser.me/api/portraits/lego/1.jpg', 'URL padrão para imagem de perfil de usuário'),
('defaultLang', 'pt-BR', 'Idioma padrão da interface'),
('maintenanceMode', 'false', 'Se true, exibe página de manutenção para todos os usuários'),
('apiVersion', 'v1', 'Versão atual da API'),
('supportEmail', 'suporte@books.com', 'E-mail de contato do suporte técnico');
