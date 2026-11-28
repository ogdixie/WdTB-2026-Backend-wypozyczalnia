package pl.wsb.rules.examples.cafe.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.wsb.rules.examples.cafe.booking.entity.Reservation;

import java.util.List;

/**
 * Repozytorium rezerwacji: interfejs bez implementacji, Spring Data buduje zapytania z nazw metod.
 * Nazwa {@code findByGuest_FullName} przechodzi przez relację {@code Reservation.guest} do kolumny {@code guest.full_name}.
 * Od LAB 04 także odpowiedzi na pytania inwestora z przykładu kawiarni (P1, P3; P2 jest w {@link GuestRepository}).
 */
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByGuest_FullName(String fullName);

    long countByTable_Number(Integer number);

    // P1: zapytanie pochodne przez relację, z sortowaniem w nazwie metody
    List<Reservation> findByGuest_FullNameOrderByStartsAtDesc(String fullName);

    // P3: zestawienie GROUP BY; wynik to projekcja TableLoad, aliasy w select = nazwy getterów projekcji
    @Query("""
            select t.number as tableNumber, count(r) as reservations, sum(r.deposit) as deposits
            from Reservation r join r.table t
            group by t.number
            order by sum(r.deposit) desc
            """)
    List<TableLoad> tableLoad();
}
