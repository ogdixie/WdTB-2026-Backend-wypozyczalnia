package pl.wsb.rules.examples.cafe.booking.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import pl.wsb.rules.examples.cafe.booking.entity.Guest;
import pl.wsb.rules.examples.cafe.booking.entity.Reservation;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Wzór testów pytań inwestora dla przykładu kawiarni (dane z data.sql z instrukcji LAB 03).
 * Nazwa klasy nie kończy się na „Test”, więc Maven jej nie uruchamia: kawiarnia nie jest aplikacją.
 * W projekcie zespołu każde pytanie ma własną klasę testową (np. {@code P1ReservationsOfGuestTest}) w src/test/java
 * w pakiecie repozytorium, żeby przy pracy równoległej każda osoba edytowała tylko swoje pliki.
 */
@DataJpaTest
class ReservationQueriesExample {

    @Autowired
    private ReservationRepository reservations;

    @Autowired
    private GuestRepository guests;

    @Test
    @DisplayName("P1 Rezerwacje gościa o podanym imieniu i nazwisku, od najnowszej")
    void p1ReservationsOfGuest() {
        List<Reservation> found = reservations.findByGuest_FullNameOrderByStartsAtDesc("Anna Kowalska");

        assertThat(found).hasSize(2);
        assertThat(found).extracting(Reservation::getStartsAt).isSortedAccordingTo(Comparator.reverseOrder());
    }

    @Test
    @DisplayName("P2 Goście z rezerwacją w podanej sali i okresie, każdy raz")
    void p2GuestsOfRoom() {
        List<Guest> found = guests.guestsOfRoom("Sala główna",
                LocalDateTime.of(2026, 11, 14, 0, 0), LocalDateTime.of(2026, 11, 16, 23, 59));

        assertThat(found).extracting(Guest::getFullName).containsExactly("Anna Kowalska", "Jan Nowak");
    }

    @Test
    @DisplayName("P3 Liczba rezerwacji i suma zaliczek dla każdego stolika")
    void p3TableLoad() {
        List<TableLoad> rows = reservations.tableLoad();

        assertThat(rows).isNotEmpty();
        assertThat(rows.get(0).getReservations()).isPositive();
    }
}
