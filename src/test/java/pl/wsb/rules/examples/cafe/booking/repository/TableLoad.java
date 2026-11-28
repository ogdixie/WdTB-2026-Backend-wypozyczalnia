package pl.wsb.rules.examples.cafe.booking.repository;

import java.math.BigDecimal;

/**
 * Projekcja: wiersz zestawienia „obciążenie stolików” (P3). To nie encja, tylko widok wyniku zapytania;
 * Spring Data wypełnia gettery wartościami kolumn o tych samych aliasach.
 */
public interface TableLoad {

    Integer getTableNumber();

    long getReservations();

    BigDecimal getDeposits();
}
