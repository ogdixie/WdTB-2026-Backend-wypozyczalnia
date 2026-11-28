# Pytania inwestora · Sprzętownia

Grupa 1 (CBm_3), zespół 12 · Repozytorium: pdziwinski22/WdTB-2026-Backend--sprzetownia-- · Pakiet: `pl.wsb.sprzetownia`

Każde pytanie to jedna metoda w repozytorium z kolumny „Repozytorium” i jedna klasa testowa `@DataJpaTest` z testem `@DisplayName("P1 …")` (P2, P3).
**To są propozycje, nie obowiązek.** Pytania przygotowano na podstawie Waszego kodu i danych z `data.sql` (stan repozytorium z 28 listopada 2026).
Możecie je zrealizować albo ułożyć własne pytania o Wasze dane i wpisać ich treść w `@DisplayName` testu. W obu przypadkach ocenę wystawia
pipeline (przebieg „Testy i reguły”), więc każde pytanie musi spełnić jego kryteria:
- **P1:** metoda bez `@Query`, której warunek w nazwie zaczyna się od pola relacji encji repozytorium, np. `findByGuest_FullNameOrderByStartsAtDesc`;
- **P2:** metoda z `@Query` w JPQL z `join`, bez `group by`;
- **P3:** metoda z `@Query` z `group by`, zwracająca listę projekcji (interfejs z getterami), a nie encję.

Gdy w `data.sql` jest tylko jeden pasujący wiersz, a test ma sprawdzić kolejność albo grupowanie, dopiszcie dane w `src/test/resources/sql/p1.sql`
(`p2.sql`, `p3.sql`) i dodajcie `@Sql("/sql/p1.sql")` przy teście; `data.sql` zostaje bez zmian (instrukcja LAB 04, część C).
Rodzaje zapytań, gałęzie i kryteria oceny: instrukcja LAB 04.

> **Schemat w repozytorium jest niepełny.** Tabele equipment, rental_point, customer i rental istnieją, ale nie są połączone żadną relacją. Pytania zakładają klucze obce: equipment.rental_point_id → rental_point, rental.customer_id → customer, rental.equipment_id → equipment. Najpierw dokończcie LAB 03 (warunek przystąpienia).

| Id | Ocena | Repozytorium | Pytanie inwestora | Tabele |
|---|---|---|---|---|
| P1 | 3 | EquipmentRepository | Jaki sprzęt jest przypisany do punktów wypożyczeń w podanym mieście? Od najniższej ceny za dobę. | equipment, rental_point |
| P2 | 3 | CustomerRepository | Którzy klienci mają co najmniej jedno wypożyczenie o podanym statusie (np. OVERDUE)? Każdy klient tylko raz. | customer, rental |
| P3 | 4 | EquipmentRepository | Ile sztuk sprzętu i jaka średnia cena za dobę przypada na każdy punkt wypożyczeń? Wynik: nazwa punktu, liczba sztuk, średnia cena za dobę, od największej liczby sztuk. | equipment, rental_point |

**Gdzie to piszecie.** Metoda w repozytorium, test w osobnej klasie, w tym samym pakiecie w `src/test/java`:

| Id | Metoda w | Test w |
|---|---|---|
| P1 | `src/main/java/pl/wsb/sprzetownia/catalog/repository/EquipmentRepository.java` | `src/test/java/pl/wsb/sprzetownia/catalog/repository/P1EquipmentRepositoryTest.java` |
| P2 | `src/main/java/pl/wsb/sprzetownia/customer/repository/CustomerRepository.java` | `src/test/java/pl/wsb/sprzetownia/customer/repository/P2CustomerRepositoryTest.java` |
| P3 | `src/main/java/pl/wsb/sprzetownia/catalog/repository/EquipmentRepository.java` | `src/test/java/pl/wsb/sprzetownia/catalog/repository/P3EquipmentRepositoryTest.java` |

**Podział pracy (rekomendacja).** Potrzebujecie co najmniej 4 repozytoriów (każde dla innej encji), w tym tych z tabeli, i trzech pytań.
Zalecamy podzielić to między siebie: każda osoba bierze jedno pytanie (razem z jego repozytorium) i robi je na własnej gałęzi `feature/…`
wychodzącej z `main`, potem Pull Request do `main`, który przegląda i scala inna osoba (instrukcja LAB 04, część B).
