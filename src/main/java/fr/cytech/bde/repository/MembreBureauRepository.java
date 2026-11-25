package fr.cytech.bde.repository;

import fr.cytech.bde.domain.MembreBureau;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MembreBureauRepository extends JpaRepository<MembreBureau, Long> {

    List<MembreBureau> findAllByOrderByNomAscPrenomAsc();

    Optional<MembreBureau> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}
