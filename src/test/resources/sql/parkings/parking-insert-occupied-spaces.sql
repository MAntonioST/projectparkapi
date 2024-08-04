-- Inserindo dados na tabela USERS
INSERT INTO USERS (id, username, password, role) VALUES (100, 'alan@techcorp.com', '$2a$12$xOJDEDfUTdVQg.wLosammebZi9HWGk34Niolo.pnjL0QXrYqwTD46', 'ROLE_ADMIN');
INSERT INTO USERS (id, username, password, role) VALUES (101, 'john.doe@innovatech.com', '$2a$12$xOJDEDfUTdVQg.wLosammebZi9HWGk34Niolo.pnjL0QXrYqwTD46', 'ROLE_CLIENTE');
INSERT INTO USERS (id, username, password, role) VALUES (102, 'jane.smith@cyberdynetech.com', '$2a$12$xOJDEDfUTdVQg.wLosammebZi9HWGk34Niolo.pnjL0QXrYqwTD46', 'ROLE_CLIENTE');
INSERT INTO USERS (id, username, password, role) VALUES (103, 'alice.jones@nextgen.com', '$2a$12$xOJDEDfUTdVQg.wLosammebZi9HWGk34Niolo.pnjL0QXrYqwTD46', 'ROLE_CLIENTE');
INSERT INTO USERS (id, username, password, role) VALUES (104, 'bob.brown@futuratech.com', '$2a$12$xOJDEDfUTdVQg.wLosammebZi9HWGk34Niolo.pnjL0QXrYqwTD46', 'ROLE_CLIENTE');

-- Inserindo dados na tabela CUSTOMERS
INSERT INTO CUSTOMERS (id, name, cpf, id_user) VALUES (21, 'Jane Smith', '56296581076', 102);
INSERT INTO CUSTOMERS (id, name, cpf, id_user) VALUES (22, 'John Clarck', '25125389072', 101);

INSERT INTO PARKING_SPACES (id, code, status) values (100, 'A-01', 'OCCUPIED');
INSERT INTO PARKING_SPACES (id, code, status) values (200, 'A-02', 'OCCUPIED');
INSERT INTO PARKING_SPACES (id, code, status) values (300, 'A-03', 'OCCUPIED');
INSERT INTO PARKING_SPACES (id, code, status) values (400, 'A-04', 'OCCUPIED');
INSERT INTO PARKING_SPACES (id, code, status) values (500, 'A-05', 'OCCUPIED');

INSERT INTO CUSTOMER_PARKING_SPACE (receipt_number, license_plate, brand, model, color, entry_time, customer_id, parking_space_id)
    VALUES ('20230313-101300', 'FIT-1010', 'FIAT', 'PALIO', 'GREEN', '2023-03-13 10:15:00', 22, 100);
INSERT INTO CUSTOMER_PARKING_SPACE (receipt_number, license_plate, brand, model, color, entry_time, customer_id, parking_space_id)
    VALUES ('20230314-101400', 'SIE-1020', 'FIAT', 'SIENA', 'WHITE', '2023-03-14 10:15:00', 21, 200);
INSERT INTO CUSTOMER_PARKING_SPACE (receipt_number, license_plate, brand, model, color, entry_time, customer_id, parking_space_id)
    VALUES ('20230315-101500', 'FIT-1030', 'FIAT', 'PALIO', 'GREEN', '2023-03-14 10:15:00', 22, 300);
INSERT INTO CUSTOMER_PARKING_SPACE (receipt_number, license_plate, brand, model, color, entry_time, customer_id, parking_space_id)
    VALUES ('20230316-101600', 'SIE-1040', 'FIAT', 'SIENA', 'GREEN', '2023-03-14 10:15:00', 21, 400);
INSERT INTO CUSTOMER_PARKING_SPACE (receipt_number, license_plate, brand, model, color, entry_time, customer_id, parking_space_id)
    VALUES ('20230317-101700', 'SIE-1050', 'FIAT', 'SIENA', 'GREEN', '2023-03-14 10:15:00', 22, 500);