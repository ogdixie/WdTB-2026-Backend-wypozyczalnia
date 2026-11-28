package pl.wsb.rules.examples.cafe.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.wsb.rules.examples.cafe.booking.entity.Extra;

/** Repozytorium dodatków: na razie bez własnych metod, wystarczą odziedziczone (findAll, findById, save…). */
public interface ExtraRepository extends JpaRepository<Extra, Long> {
}
