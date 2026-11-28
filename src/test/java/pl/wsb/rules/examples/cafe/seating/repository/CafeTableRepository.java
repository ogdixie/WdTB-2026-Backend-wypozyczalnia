package pl.wsb.rules.examples.cafe.seating.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.wsb.rules.examples.cafe.seating.entity.CafeTable;

/** Repozytorium stolików: na razie bez własnych metod, wystarczą odziedziczone (findAll, findById, save…). */
public interface CafeTableRepository extends JpaRepository<CafeTable, Long> {
}
