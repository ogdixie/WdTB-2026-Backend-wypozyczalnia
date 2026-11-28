# Pytania inwestora · Symulator mediów społecznościowych

Grupa 1 (CBm_3), zespół 4 · Repozytorium: Piachsecki/WdTB-2026-Backend--social-meda-simulator · Pakiet: `pl.wsb.projekt`

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

> **Schemat w repozytorium jest niepełny.** W schemacie jest tylko tabela user_account. Nagłówek projektu łączy dwa tematy (Social Media / CostSplitter); pytania przyjmują temat z nazwy repozytorium (symulator mediów społecznościowych) i zakładają tabele: profile (nickname, bio, user_account_id → user_account), post (profile_id → profile, content, created_at, like_count), comment (post_id → post, profile_id → profile, content, created_at). Najpierw dokończcie LAB 03 (warunek przystąpienia).

| Id | Ocena | Repozytorium | Pytanie inwestora | Tabele |
|---|---|---|---|---|
| P1 | 3 | PostRepository | Jakie posty opublikował profil o podanym pseudonimie? Od najnowszego. | post, profile |
| P2 | 3 | PostRepository | Które posty mają co najmniej jeden komentarz dodany w podanym okresie? Każdy post tylko raz. | post, comment |
| P3 | 4 | PostRepository | Ile postów i ile łącznie polubień ma każdy profil? Wynik: pseudonim, liczba postów, suma polubień, od największej sumy polubień. | post, profile |

**Gdzie to piszecie.** Metoda w repozytorium, test w osobnej klasie, w tym samym pakiecie w `src/test/java`:

| Id | Metoda w | Test w |
|---|---|---|
| P1 | `src/main/java/pl/wsb/projekt/<moduł>/repository/PostRepository.java` | `src/test/java/pl/wsb/projekt/<moduł>/repository/P1PostRepositoryTest.java` |
| P2 | `src/main/java/pl/wsb/projekt/<moduł>/repository/PostRepository.java` | `src/test/java/pl/wsb/projekt/<moduł>/repository/P2PostRepositoryTest.java` |
| P3 | `src/main/java/pl/wsb/projekt/<moduł>/repository/PostRepository.java` | `src/test/java/pl/wsb/projekt/<moduł>/repository/P3PostRepositoryTest.java` |

**Podział pracy (rekomendacja).** Potrzebujecie co najmniej 4 repozytoriów (każde dla innej encji), w tym tych z tabeli, i trzech pytań.
Zalecamy podzielić to między siebie: każda osoba bierze jedno pytanie (razem z jego repozytorium) i robi je na własnej gałęzi `feature/…`
wychodzącej z `main`, potem Pull Request do `main`, który przegląda i scala inna osoba (instrukcja LAB 04, część B).
