package fr.cytech.bde.repository;

import fr.cytech.bde.domain.LigneTresorerie;
import fr.cytech.bde.domain.enums.StatutLigne;
import fr.cytech.bde.domain.enums.TypeLigne;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface LigneTresorerieRepository extends JpaRepository<LigneTresorerie, Long> {

    List<LigneTresorerie> findAllByOrderByDateOperationDescIdDesc();

    List<LigneTresorerie> findByEvenementIdOrderByDateOperationDescIdDesc(Long evenementId);

    List<LigneTresorerie> findByEvenementIsNullOrderByDateOperationDescIdDesc();

    List<LigneTresorerie> findByDocumentJustificatifId(Long documentId);

    List<LigneTresorerie> findByCreateurIdOrderByDateOperationDescIdDesc(Long membreId);

    boolean existsByEvenementIdAndStatut(Long evenementId, StatutLigne statut);

    boolean existsByCreateurId(Long membreId);

    boolean existsByDocumentJustificatifId(Long documentId);

    long countByEvenementId(Long evenementId);

    long countByEvenementIdAndStatut(Long evenementId, StatutLigne statut);

    long countByStatut(StatutLigne statut);

    @Query("""
            select coalesce(sum(l.montant), 0)
            from LigneTresorerie l
            where l.type = :type and l.statut = :statut
            """)
    BigDecimal sumMontantByTypeAndStatut(@Param("type") TypeLigne type,
                                          @Param("statut") StatutLigne statut);

    @Query("""
            select coalesce(sum(l.montant), 0)
            from LigneTresorerie l
            where l.statut = :statut
            """)
    BigDecimal sumMontantByStatut(@Param("statut") StatutLigne statut);

    @Query("""
            select coalesce(sum(l.montant), 0)
            from LigneTresorerie l
            where l.evenement.id = :evenementId and l.type = :type and l.statut = :statut
            """)
    BigDecimal sumMontantForEvenement(@Param("evenementId") Long evenementId,
                                      @Param("type") TypeLigne type,
                                      @Param("statut") StatutLigne statut);

    @Query("""
            select coalesce(sum(l.montant), 0)
            from LigneTresorerie l
            where l.evenement.id = :evenementId and l.statut = :statut
            """)
    BigDecimal sumMontantForEvenementByStatut(@Param("evenementId") Long evenementId,
                                               @Param("statut") StatutLigne statut);
}
