package fr.cytech.bde.repository;

import fr.cytech.bde.domain.DossierAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DossierAdminRepository extends JpaRepository<DossierAdmin, Long> {

    List<DossierAdmin> findAllByOrderByNomAsc();
}
