DELETE FROM account;
ALTER TABLE account ALTER COLUMN id RESTART WITH 1;
DELETE FROM book;
ALTER TABLE book ALTER COLUMN id RESTART WITH 1;

INSERT INTO account (created_at, name, email, password, birth, photo, cpf, address, tel, role, status, metadata) VALUES
(CURRENT_TIMESTAMP - 111, 'Alice Silva', 'alice@example.com', '$2a$10$BHK0MMBsx/2EAuWLkkG6J.GCNq790dkJL.Iwvknrnx7eI/rFZt9da', '1995-03-15', 'https://randomuser.me/api/portraits/women/11.jpg', '12345678901', 'Rua das Flores, 123', '11999990001', 'ADMIN', 'ON', '{}'),
(CURRENT_TIMESTAMP - 105, 'Bruno Lima', 'bruno@example.com', '$2a$10$BHK0MMBsx/2EAuWLkkG6J.GCNq790dkJL.Iwvknrnx7eI/rFZt9da', '1988-07-22', 'https://randomuser.me/api/portraits/men/22.jpg', '23456789012', 'Av. Brasil, 456', '11999990002', 'ADMIN', 'ON', '{}'),
(CURRENT_TIMESTAMP - 91, 'Carla Souza', 'carla@example.com', '$2a$10$BHK0MMBsx/2EAuWLkkG6J.GCNq790dkJL.Iwvknrnx7eI/rFZt9da', '1992-01-10', 'https://randomuser.me/api/portraits/women/33.jpg', '34567890123', 'Rua Central, 789', '11999990003', 'OPERATOR', 'ON', '{}'),
(CURRENT_TIMESTAMP - 89, 'Diego Alves', 'diego@example.com', '$2a$10$BHK0MMBsx/2EAuWLkkG6J.GCNq790dkJL.Iwvknrnx7eI/rFZt9da', '1990-05-18', 'https://randomuser.me/api/portraits/men/44.jpg', '45678901234', 'Av. Paulista, 321', '11999990004', 'OPERATOR', 'ON', '{}'),
(CURRENT_TIMESTAMP - 75, 'Elaine Costa', 'elaine@example.com', '$2a$10$BHK0MMBsx/2EAuWLkkG6J.GCNq790dkJL.Iwvknrnx7eI/rFZt9da', '1997-11-30', 'https://randomuser.me/api/portraits/women/55.jpg', '56789012345', 'Rua das Acácias, 654', '11999990005', 'OPERATOR', 'ON', '{}'),
(CURRENT_TIMESTAMP - 56, 'Fabio Rocha', 'fabio@example.com', '$2a$10$BHK0MMBsx/2EAuWLkkG6J.GCNq790dkJL.Iwvknrnx7eI/rFZt9da', '1991-08-25', 'https://randomuser.me/api/portraits/men/66.jpg', '67890123456', 'Rua Bela Vista, 987', '11999990006', 'USER', 'ON', '{}'),
(CURRENT_TIMESTAMP - 44, 'Gabriela Mendes', 'gabriela@example.com', '$2a$10$BHK0MMBsx/2EAuWLkkG6J.GCNq790dkJL.Iwvknrnx7eI/rFZt9da', '1996-09-12', 'https://randomuser.me/api/portraits/women/77.jpg', '78901234567', 'Rua do Sol, 147', '11999990007', 'USER', 'ON', '{}'),
(CURRENT_TIMESTAMP - 33, 'Henrique Dias', 'henrique@example.com', '$2a$10$BHK0MMBsx/2EAuWLkkG6J.GCNq790dkJL.Iwvknrnx7eI/rFZt9da', '1985-02-28', 'https://randomuser.me/api/portraits/men/88.jpg', '89012345678', 'Av. Liberdade, 258', '11999990008', 'USER', 'ON', '{}'),
(CURRENT_TIMESTAMP - 22, 'Isabela Ferreira', 'isabela@example.com', '$2a$10$BHK0MMBsx/2EAuWLkkG6J.GCNq790dkJL.Iwvknrnx7eI/rFZt9da', '1994-06-05', 'https://randomuser.me/api/portraits/women/99.jpg', '90123456789', 'Rua Harmonia, 369', '11999990009', 'USER', 'ON', '{}'),
(CURRENT_TIMESTAMP - 11, 'João Pedro', 'joao@example.com', '$2a$10$BHK0MMBsx/2EAuWLkkG6J.GCNq790dkJL.Iwvknrnx7eI/rFZt9da', '1993-12-03', 'https://randomuser.me/api/portraits/men/01.jpg', '01234567890', 'Av. da Paz, 159', '11999990010', 'USER', 'ON', '{}');

INSERT INTO book (
    created_at, title, author, isbn, publisher, publication_year, edition_number,
    publication_place, number_of_pages, genre, format, access_url,
    file_format, has_drm, language, synopsis, keywords, cover_image_url,
    translator, original_language
) VALUES
(CURRENT_TIMESTAMP - 78, 'A Guerra dos Tronos', 'George R. R. Martin', '978-8556510342', 'Suma de Letras', 2019, 1, 'Rio de Janeiro, Brasil', 608, 'Fantasia', 'Brochura', NULL, NULL, FALSE, 'Português', 'Primeiro livro da épica série de fantasia...', 'fantasia, idade média, dragões', 'https://picsum.photos/297/397', NULL, 'Inglês'),
(CURRENT_TIMESTAMP - 67, 'O Senhor dos Anéis: A Sociedade do Anel', 'J.R.R. Tolkien', '978-8533603148', 'Martins Fontes', 2006, 1, 'São Paulo, Brasil', 576, 'Fantasia', 'Brochura', NULL, NULL, FALSE, 'Português', 'Um hobbit recebe um anel mágico e embarca em uma jornada...', 'fantasia, aventura, hobbits', 'https://picsum.photos/298/398', NULL, 'Inglês'),
(CURRENT_TIMESTAMP - 56, 'Neuromancer', 'William Gibson', '978-8576571770', 'Aleph', 2013, 1, 'São Paulo, Brasil', 320, 'Ficção Científica', 'Brochura', NULL, 'epub', TRUE, 'Português', 'Um hacker de computador decadente é contratado para um último trabalho...', 'cyberpunk, ficção científica, inteligência artificial', 'https://picsum.photos/299/399', NULL, 'Inglês'),
(CURRENT_TIMESTAMP - 45, 'Orgulho e Preconceito', 'Jane Austen', '978-8595081504', 'Penguin-Companhia', 2017, 1, 'São Paulo, Brasil', 432, 'Romance', 'Brochura', NULL, NULL, FALSE, 'Português', 'A história das turbulentas relações entre Elizabeth Bennet e Mr. Darcy...', 'romance, clássico, inglaterra', 'https://picsum.photos/300/400', NULL, 'Inglês'),
(CURRENT_TIMESTAMP - 34, 'Dom Casmurro', 'Machado de Assis', '978-8524790093', 'Ática', 2019, NULL, 'São Paulo, Brasil', 288, 'Romance', 'Brochura', NULL, 'pdf', FALSE, 'Português', 'A narrativa em primeira pessoa de Bento Santiago, o Dom Casmurro...', 'romance, literatura brasileira, clássico', 'https://picsum.photos/301/401', NULL, 'Português'),
(CURRENT_TIMESTAMP - 23, 'Sapiens: Uma Breve História da Humanidade', 'Yuval Noah Harari', '978-8535927575', 'Companhia das Letras', 2015, 1, 'São Paulo, Brasil', 464, 'Não Ficção', 'Brochura', NULL, NULL, FALSE, 'Português', 'Uma análise da história da humanidade desde os primeiros humanos até o presente...', 'história, ciência, humanidade', 'https://picsum.photos/302/402', NULL, 'Hebraico'),
(CURRENT_TIMESTAMP - 12, 'O Conto da Aia', 'Margaret Atwood', '978-8532530783', 'Rocco', 2017, 1, 'Rio de Janeiro, Brasil', 400, 'Ficção Distópica', 'Brochura', NULL, 'epub', TRUE, 'Português', 'Em uma república totalitária chamada Gilead...', 'distopia, feminismo, política', 'https://picsum.photos/303/403', 'Não Informado', 'Inglês'),
(CURRENT_TIMESTAMP - 10, 'Clean Code: A Handbook of Agile Software Craftsmanship', 'Robert C. Martin', '978-0132350884', 'Prentice Hall', 2008, 1, 'Upper Saddle River, USA', 464, 'Não Ficção', 'E-book', 'https://example.com/clean-code.epub', 'epub', FALSE, 'Inglês', 'Even bad code can function. But if code isn’t clean...', 'programação, desenvolvimento de software, boas práticas', 'https://picsum.photos/304/404', NULL, 'Inglês');
