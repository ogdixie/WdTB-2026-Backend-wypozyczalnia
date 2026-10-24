-- Dane startowe aplikacji. Tabele tworzy Hibernate z encji, a ten plik Spring Boot uruchamia zaraz potem.
-- LAB 01: dopiszcie tu konta wszystkich osób z zespołu (reguła R1.03).
-- LAB 02: dopiszcie dane do swoich tabel (kolejność: najpierw tabele, na które wskazują klucze obce).
insert into user_account (email, display_name, role, created_at) values
    ('s396414@student.wroclaw.merito.pl',   'Sebastian',   'USER',    current_timestamp),
    ('s877879@student.wroclaw.merito.pl',  'Mikołaj',  'USER',    current_timestamp),
    ('s332089@student.wroclaw.merito.pl',       'Wiktor', 'USER', current_timestamp),
    ('admin@example.com',       'Admin', 'ADMIN',   current_timestamp);

-- LAB 01: Wasze konta (zmieńcie na swoje dane, rola USER)
-- insert into user_account (email, display_name, role, created_at) values
--     ('imie.nazwisko@student.merito.pl', 'Imię', 'USER', current_timestamp);
-- ===== LAB 02 · car =====
insert into car (brand, model, plate_number, horsepower, daily_price, status) values
    ('Porsche',  '911 Carrera S',  'DW 911PS', 450, 1290.00, 'AVAILABLE'),
    ('BMW',      'M4 Competition', 'DW 4M4BM', 510,  990.00, 'RENTED'),
    ('Audi',     'R8 V10',         'DW 8R8AU', 570, 1590.00, 'SERVICE'),
    ('Mercedes', 'AMG GT',         'DW 6AMGT', 530, 1390.00, 'AVAILABLE');

-- ===== LAB 02 · customer =====
insert into customer (full_name, email, phone, driving_license_number, registered_on, street, city, postal_code) values
    ('Jan Kowalski',     'jan.kowalski@example.com',     '600100200', '01234/19/0264', '2026-09-15', 'Legnicka 15',  'Wrocław', '53-671'),
    ('Anna Nowak',       'anna.nowak@example.com',       null,        '05678/21/0264', '2026-10-02', null,           null,      null),
    ('Piotr Wiśniewski', 'piotr.wisniewski@example.com', '700300400', '09876/18/1261', '2026-10-20', 'Floriańska 3', 'Kraków',  '31-019');

-- ===== LAB 02 · insurance_plan =====
insert into insurance_plan (name, daily_price, deductible, description, active) values
    ('Basic',      0.00, 10000.00, 'Obowiązkowe OC, wysoki udział własny', true),
    ('Standard',  99.00,  3000.00, 'OC + AC z obniżonym udziałem własnym', true),
    ('Premium',  199.00,     0.00, 'Pełna ochrona bez udziału własnego',   true);