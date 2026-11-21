package pl.wsb.wypozyczalnia.rental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.wsb.wypozyczalnia.rental.entity.Rental;

import java.util.List;

public interface RentalRepository extends JpaRepository<Rental, Long> {

    List<Rental> findByCustomer_FullName(String fullName);

    List<Rental> findByCar_Brand(String brand);
}