-- Inserindo dados na tabela USERS
INSERT INTO USERS (id, username, password, role) VALUES (100, 'alan@techcorp.com', '$2a$12$xOJDEDfUTdVQg.wLosammebZi9HWGk34Niolo.pnjL0QXrYqwTD46', 'ROLE_ADMIN');
INSERT INTO USERS (id, username, password, role) VALUES (101, 'john.doe@innovatech.com', '$2a$12$xOJDEDfUTdVQg.wLosammebZi9HWGk34Niolo.pnjL0QXrYqwTD46', 'ROLE_CLIENTE');
INSERT INTO USERS (id, username, password, role) VALUES (102, 'jane.smith@cyberdynetech.com', '$2a$12$xOJDEDfUTdVQg.wLosammebZi9HWGk34Niolo.pnjL0QXrYqwTD46', 'ROLE_CLIENTE');
INSERT INTO USERS (id, username, password, role) VALUES (103, 'alice.jones@nextgen.com', '$2a$12$xOJDEDfUTdVQg.wLosammebZi9HWGk34Niolo.pnjL0QXrYqwTD46', 'ROLE_CLIENTE');
INSERT INTO USERS (id, username, password, role) VALUES (104, 'bob.brown@futuratech.com', '$2a$12$xOJDEDfUTdVQg.wLosammebZi9HWGk34Niolo.pnjL0QXrYqwTD46', 'ROLE_CLIENTE');

-- Inserindo dados na tabela CUSTOMERS
INSERT INTO CUSTOMERS (id, name, cpf, id_user) VALUES (10, 'Jane Smith', '56296581076', 102);
INSERT INTO CUSTOMERS (id, name, cpf, id_user) VALUES (20, 'John Clarck', '25125389072', 101);