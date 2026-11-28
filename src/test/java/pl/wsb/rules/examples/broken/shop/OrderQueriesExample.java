package pl.wsb.rules.examples.broken.shop;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Celowo błędne testy pytań do autotestu reguł LAB 04: P2 bez JOIN (zapytanie pochodne bez relacji),
 * P3 zwraca Object[] zamiast projekcji. Brak P1.
 */
@DataJpaTest
class OrderQueriesExample {

    @Autowired
    private OrderRepository orders;

    @Test
    @DisplayName("P2 Zamówienia nowe")
    void p2() {
        assertThat(orders.findByStatus(Order.Status.NEW)).isNotNull();
    }

    @Test
    @DisplayName("P3 Liczba zamówień w każdym statusie")
    void p3() {
        assertThat(orders.countPerStatus()).isNotNull();
    }
}
