package fr.cytech.bde.web.mvc.form;

import fr.cytech.bde.domain.DocumentAdmin;
import fr.cytech.bde.domain.enums.TypeDocument;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class DocumentForm {

    @NotBlank(message = "Le titre est obligatoire.")
    @Size(max = 180, message = "Le titre ne peut pas dépasser 180 caractères.")
    private String titre;

    @NotNull(message = "Le type de document est obligatoire.")
    private TypeDocument type;

    @Size(max = 500, message = "La référence ne peut pas dépasser 500 caractères.")
    private String reference;

    @NotNull(message = "La date de création est obligatoire.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateCreation = LocalDate.now();

    private Long dossierId;

    public static DocumentForm depuis(DocumentAdmin document) {
        DocumentForm form = new DocumentForm();
        form.setTitre(document.getTitre());
        form.setType(document.getType());
        form.setReference(document.getReference());
        form.setDateCreation(document.getDateCreation());
        form.setDossierId(document.getDossier() == null ? null : document.getDossier().getId());
        return form;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public TypeDocument getType() {
        return type;
    }

    public void setType(TypeDocument type) {
        this.type = type;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public LocalDate getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Long getDossierId() {
        return dossierId;
    }

    public void setDossierId(Long dossierId) {
        this.dossierId = dossierId;
    }
}
