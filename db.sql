DELETE FROM account;
ALTER TABLE account ALTER COLUMN id RESTART WITH 1;

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





--INSERT INTO books (isbn, author, genre, launch, photo, publication_year, status, synopsis, title) VALUES
--('978-3-16-148410-0', 'George Orwell', 'Dystopian', '1949-06-08', 'https://example.com/1984.jpg', 1949, 'Available', 'A dystopian novel set in a totalitarian society ruled by Big Brother.', '1984'),
--('978-0-7432-7356-5', 'Harper Lee', 'Fiction', '1960-07-11', 'https://example.com/mockingbird.jpg', 1960, 'Checked Out', 'A novel about racial injustice in the Deep South.', 'To Kill a Mockingbird'),
--('978-0-452-28423-4', 'F. Scott Fitzgerald', 'Classic', '1925-04-10', 'https://example.com/gatsby.jpg', 1925, 'Available', 'A story of wealth, love, and tragedy in the Jazz Age.', 'The Great Gatsby'),
--('978-0-316-76948-0', 'Stephen King', 'Horror', '1986-09-15', 'https://example.com/it.jpg', 1986, 'Available', 'Seven children fight an evil entity that haunts their town.', 'It'),
--('978-0-553-21311-7', 'J.R.R. Tolkien', 'Fantasy', '1954-07-29', 'https://example.com/fellowship.jpg', 1954, 'Available', 'A hobbit begins a journey to destroy a powerful ring.', 'The Fellowship of the Ring'),
--('978-0-545-01022-1', 'Suzanne Collins', 'Science Fiction', '2008-09-14', 'https://example.com/hunger_games.jpg', 2008, 'Checked Out', 'In a dystopian future, teens must fight to the death in a televised event.', 'The Hunger Games'),
--('978-0-7432-7355-8', 'Dan Brown', 'Thriller', '2003-03-18', 'https://example.com/da_vinci_code.jpg', 2003, 'Available', 'A symbologist uncovers a conspiracy involving the Holy Grail.', 'The Da Vinci Code'),
--('978-0-14-303943-3', 'Gabriel García Márquez', 'Magical Realism', '1967-05-30', 'https://example.com/solitude.jpg', 1967, 'Available', 'The story of several generations of the Buendía family in Macondo.', 'One Hundred Years of Solitude'),
--('978-0-06-112008-4', 'Aldous Huxley', 'Dystopian', '1932-01-01', 'https://example.com/brave_new_world.jpg', 1932, 'Available', 'A futuristic society driven by technology and social control.', 'Brave New World');
