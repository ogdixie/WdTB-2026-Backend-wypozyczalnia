package pl.wsb.rules.examples.broken.shop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Część celowo błędnego przykładu (patrz {@link Order}): repozytorium poza pakietem repository, zapytanie bez relacji.
 * LAB 04: zestawienie zwraca surowe tablice Object[] zamiast projekcji.
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByStatus(Order.Status status);

    @Query("select o.status, count(o) from Order o group by o.status")
    List<Object[]> countPerStatus();
}
