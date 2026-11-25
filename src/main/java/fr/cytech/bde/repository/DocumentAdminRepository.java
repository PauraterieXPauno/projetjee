package fr.cytech.bde.repository;

import fr.cytech.bde.domain.DocumentAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentAdminRepository extends JpaRepository<DocumentAdmin, Long> {

    List<DocumentAdmin> findAllByOrderByDateCreationDescIdDesc();

    List<DocumentAdmin> findByDossierIsNullOrderByDateCreationDescIdDesc();

    List<DocumentAdmin> findByDossierIdOrderByDateCreationDescIdDesc(Long dossierId);

    long countByDossierId(Long dossierId);

    boolean existsByDossierId(Long dossierId);
}
