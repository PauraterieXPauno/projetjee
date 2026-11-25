package fr.cytech.bde.web.mvc.form;

import fr.cytech.bde.domain.LigneTresorerie;
import fr.cytech.bde.domain.enums.TypeLigne;
import fr.cytech.bde.service.LigneTresorerieCommand;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TresorerieForm {

    @NotBlank(message = "Le libellé est obligatoire.")
    @Size(max = 180, message = "Le libellé ne peut pas dépasser 180 caractères.")
    private String libelle;

    @NotNull(message = "Le type est obligatoire.")
    private TypeLigne type = TypeLigne.DEPENSE;

    @NotNull(message = "Le montant est obligatoire.")
    @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à zéro.")
    @Digits(integer = 13, fraction = 2, message = "Le montant doit contenir au maximum 2 décimales.")
    private BigDecimal montant;

    @NotBlank(message = "La catégorie est obligatoire.")
    @Size(max = 80, message = "La catégorie ne peut pas dépasser 80 caractères.")
    private String categorie;

    @NotNull(message = "La date d'opération est obligatoire.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateOperation = LocalDate.now();

    private Long evenementId;

    @NotNull(message = "Le créateur est obligatoire.")
    private Long createurId;

    private Long documentJustificatifId;

    private boolean validationExplicite;

    public static TresorerieForm depuis(LigneTresorerie ligne) {
        TresorerieForm form = new TresorerieForm();
        form.setLibelle(ligne.getLibelle());
        form.setType(ligne.getType());
        form.setMontant(ligne.getMontant());
        form.setCategorie(ligne.getCategorie());
        form.setDateOperation(ligne.getDateOperation());
        form.setEvenementId(ligne.getEvenement() == null ? null : ligne.getEvenement().getId());
        form.setCreateurId(ligne.getCreateur().getId());
        form.setDocumentJustificatifId(ligne.getDocumentJustificatif() == null
                ? null : ligne.getDocumentJustificatif().getId());
        form.setValidationExplicite(false);
        return form;
    }

    public LigneTresorerieCommand versCommande() {
        return new LigneTresorerieCommand(
                libelle,
                type,
                montant,
                categorie,
                dateOperation,
                evenementId,
                createurId,
                documentJustificatifId,
                validationExplicite
        );
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public TypeLigne getType() {
        return type;
    }

    public void setType(TypeLigne type) {
        this.type = type;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public LocalDate getDateOperation() {
        return dateOperation;
    }

    public void setDateOperation(LocalDate dateOperation) {
        this.dateOperation = dateOperation;
    }

    public Long getEvenementId() {
        return evenementId;
    }

    public void setEvenementId(Long evenementId) {
        this.evenementId = evenementId;
    }

    public Long getCreateurId() {
        return createurId;
    }

    public void setCreateurId(Long createurId) {
        this.createurId = createurId;
    }

    public Long getDocumentJustificatifId() {
        return documentJustificatifId;
    }

    public void setDocumentJustificatifId(Long documentJustificatifId) {
        this.documentJustificatifId = documentJustificatifId;
    }

    public boolean isValidationExplicite() {
        return validationExplicite;
    }

    public void setValidationExplicite(boolean validationExplicite) {
        this.validationExplicite = validationExplicite;
    }
}
