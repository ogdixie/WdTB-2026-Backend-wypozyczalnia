-- Dane startowe aplikacji. Tabele tworzy Hibernate z encji, a ten plik Spring Boot uruchamia zaraz potem.

-- LAB 01 - konta zespołu
insert into user_account (email, display_name, role, created_at) values
    ('s396414@student.wroclaw.merito.pl', 'Sebastian', 'USER',  current_timestamp),
    ('s877879@student.wroclaw.merito.pl', 'Mikołaj',   'USER',  current_timestamp),
    ('s332089@student.wroclaw.merito.pl', 'Wiktor',    'USER',  current_timestamp),
    ('admin@example.com',                 'Admin',     'ADMIN', current_timestamp);

-- LAB 03 - konta użytkowników
insert into user_account (email, display_name, role, created_at) values
    ('jan.kowalski@example.com', 'Jan Kowalski', 'USER', current_timestamp),
    ('anna.nowak@example.com',   'Anna Nowak',   'USER', current_timestamp);

-- LAB 03 - branch
insert into branch (name, city, street, phone, active) values
    ('rs-rent Wrocław',  'Wrocław',  'Legnicka 58',  '713001001', true),
    ('rs-rent Kraków',   'Kraków',   'Pawia 12',     '123002002', true),
    ('rs-rent Warszawa', 'Warszawa', 'Grzybowska 4', null,        false);

-- LAB 02 - car @update lab03
insert into car (brand, model, plate_number, horsepower, daily_price, status, branch_id) values
    ('Porsche',  '911 Carrera S',  'DW 911PS', 450, 1290.00, 'AVAILABLE', 1),
    ('BMW',      'M4 Competition', 'DW 4M4BM', 510,  990.00, 'RENTED',    1),
    ('Audi',     'R8 V10',         'DW 8R8AU', 570, 1590.00, 'SERVICE',   2),
    ('Mercedes', 'AMG GT',         'DW 6AMGT', 530, 1390.00, 'AVAILABLE', 2);

-- LAB 02 - customer @update lab03
insert into customer (full_name, email, phone, driving_license_number, registered_on, street, city, postal_code, user_account_id) values
    ('Jan Kowalski',     'jan.kowalski@example.com',     '600100200', '01234/19/0264', '2026-09-15', 'Legnicka 15',  'Wrocław', '53-671', 5),
    ('Anna Nowak',       'anna.nowak@example.com',       null,        '05678/21/0264', '2026-10-02', null,           null,      null,     6),
    ('Piotr Wiśniewski', 'piotr.wisniewski@example.com', '700300400', '09876/18/1261', '2026-10-20', 'Floriańska 3', 'Kraków',  '31-019', null);

-- LAB 02 - insurance_plan
insert into insurance_plan (name, daily_price, deductible, description, active) values
    ('Basic',      0.00, 10000.00, 'Obowiązkowe OC, wysoki udział własny', true),
    ('Standard',  99.00,  3000.00, 'OC + AC z obniżonym udziałem własnym', true),
    ('Premium',  199.00,     0.00, 'Pełna ochrona bez udziału własnego',   true);

-- LAB 03 - rental
insert into rental (start_date, end_date, total_price, status, car_id, customer_id, insurance_plan_id) values
    ('2026-11-02', '2026-11-05', 4167.00, 'COMPLETED', 1, 1, 2),
    ('2026-11-14', '2026-11-16', 1980.00, 'ACTIVE',    2, 2, null),
    ('2026-11-20', '2026-11-23', 4767.00, 'RESERVED',  4, 3, 3),
    ('2026-11-01', '2026-11-02', 1290.00, 'CANCELLED', 1, 2, 1);