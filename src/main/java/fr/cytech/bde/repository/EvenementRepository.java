package fr.cytech.bde.repository;

import fr.cytech.bde.domain.Evenement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvenementRepository extends JpaRepository<Evenement, Long> {

    List<Evenement> findAllByOrderByDateDescIdDesc();
}
