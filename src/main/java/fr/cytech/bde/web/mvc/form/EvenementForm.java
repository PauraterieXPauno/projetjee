package fr.cytech.bde.web.mvc.form;

import fr.cytech.bde.domain.Evenement;
import fr.cytech.bde.domain.enums.StatutEvenement;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EvenementForm {

    @NotBlank(message = "Le titre est obligatoire.")
    @Size(max = 180, message = "Le titre ne peut pas dépasser 180 caractères.")
    private String titre;

    @NotNull(message = "La date est obligatoire.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;

    @NotBlank(message = "Le lieu est obligatoire.")
    @Size(max = 160, message = "Le lieu ne peut pas dépasser 160 caractères.")
    private String lieu;

    @NotNull(message = "Le budget prévisionnel est obligatoire.")
    @DecimalMin(value = "0.00", message = "Le budget ne peut pas être négatif.")
    @Digits(integer = 13, fraction = 2, message = "Le budget doit contenir au maximum 2 décimales.")
    private BigDecimal budgetPrevisionnel;

    @NotNull(message = "Le statut est obligatoire.")
    private StatutEvenement statut = StatutEvenement.PLANIFIE;

    public static EvenementForm depuis(Evenement evenement) {
        EvenementForm form = new EvenementForm();
        form.setTitre(evenement.getTitre());
        form.setDate(evenement.getDate());
        form.setLieu(evenement.getLieu());
        form.setBudgetPrevisionnel(evenement.getBudgetPrevisionnel());
        form.setStatut(evenement.getStatut());
        return form;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public BigDecimal getBudgetPrevisionnel() {
        return budgetPrevisionnel;
    }

    public void setBudgetPrevisionnel(BigDecimal budgetPrevisionnel) {
        this.budgetPrevisionnel = budgetPrevisionnel;
    }

    public StatutEvenement getStatut() {
        return statut;
    }

    public void setStatut(StatutEvenement statut) {
        this.statut = statut;
    }
}
