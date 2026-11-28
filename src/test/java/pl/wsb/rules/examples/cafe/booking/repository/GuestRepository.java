package pl.wsb.rules.examples.cafe.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.wsb.rules.examples.cafe.booking.entity.Guest;

import java.time.LocalDateTime;
import java.util.List;

/** Repozytorium gości. P2: goście wybrani po warunku na rezerwacjach i stolikach, czyli „od drugiej strony” relacji. */
public interface GuestRepository extends JpaRepository<Guest, Long> {

    @Query("""
            select distinct g
            from Reservation r join r.guest g join r.table t
            where t.room = :room and r.startsAt between :from and :to
            order by g.fullName
            """)
    List<Guest> guestsOfRoom(@Param("room") String room, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
