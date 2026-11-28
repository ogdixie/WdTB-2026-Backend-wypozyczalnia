package pl.wsb.wypozyczalnia.fleet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.wsb.wypozyczalnia.fleet.entity.Car;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {

    List<Car> findByBranch_CityOrderByDailyPriceAsc(String city);
}