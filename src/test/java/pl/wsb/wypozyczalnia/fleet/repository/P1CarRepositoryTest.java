package pl.wsb.wypozyczalnia.fleet.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import pl.wsb.wypozyczalnia.fleet.entity.Car;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class P1CarRepositoryTest {

    @Autowired
    private CarRepository cars;

    @Test
    @DisplayName("P1 Jakie samochody należą do oddziałów w podanym mieście? Od najniższej ceny za dobę.")
    void p1CarsInCity() {
        List<Car> found = cars.findByBranch_CityOrderByDailyPriceAsc("Wrocław");

        assertThat(found)
                .extracting(Car::getModel)
                .containsExactly("M4 Competition", "911 Carrera S");
    }
}