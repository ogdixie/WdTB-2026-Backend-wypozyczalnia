package pl.wsb.rules.examples.cafe.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.wsb.rules.examples.cafe.booking.entity.ReservationExtra;

/** Repozytorium dodatków wybranych do rezerwacji (encja łącząca): na razie bez własnych metod, wystarczą odziedziczone (findAll, findById, save…). */
public interface ReservationExtraRepository extends JpaRepository<ReservationExtra, Long> {
}
