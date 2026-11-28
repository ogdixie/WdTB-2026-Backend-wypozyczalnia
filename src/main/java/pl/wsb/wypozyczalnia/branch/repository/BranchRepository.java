package pl.wsb.wypozyczalnia.branch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.wsb.wypozyczalnia.branch.entity.Branch;

public interface BranchRepository extends JpaRepository<Branch, Long> {
}