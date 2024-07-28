-- Inserindo dados na tabela USERS
INSERT INTO USERS (id, username, password, role) VALUES (100, 'alan@techcorp.com', '$2a$12$xOJDEDfUTdVQg.wLosammebZi9HWGk34Niolo.pnjL0QXrYqwTD46', 'ROLE_ADMIN');
INSERT INTO USERS (id, username, password, role) VALUES (101, 'john.doe@innovatech.com', '$2a$12$xOJDEDfUTdVQg.wLosammebZi9HWGk34Niolo.pnjL0QXrYqwTD46', 'ROLE_CLIENTE');
INSERT INTO USERS (id, username, password, role) VALUES (102, 'jane.smith@cyberdynetech.com', '$2a$12$xOJDEDfUTdVQg.wLosammebZi9HWGk34Niolo.pnjL0QXrYqwTD46', 'ROLE_CLIENTE');
INSERT INTO USERS (id, username, password, role) VALUES (103, 'alice.jones@nextgen.com', '$2a$12$xOJDEDfUTdVQg.wLosammebZi9HWGk34Niolo.pnjL0QXrYqwTD46', 'ROLE_CLIENTE');
INSERT INTO USERS (id, username, password, role) VALUES (104, 'bob.brown@futuratech.com', '$2a$12$xOJDEDfUTdVQg.wLosammebZi9HWGk34Niolo.pnjL0QXrYqwTD46', 'ROLE_CLIENTE');

-- Inserindo dados na tabela PARKING_SPOTS
INSERT INTO PARKING_SPACES (id, code, status) VALUES(10, 'A-01', 'FREE');
INSERT INTO PARKING_SPACES (id, code, status) VALUES(20, 'A-02', 'FREE');
INSERT INTO PARKING_SPACES (id, code, status) VALUES(30, 'A-03', 'OCCUPIED');
INSERT INTO PARKING_SPACES (id, code, status) VALUES(40, 'A-04', 'FREE');
