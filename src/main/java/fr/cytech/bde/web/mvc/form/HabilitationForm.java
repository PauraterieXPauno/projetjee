package fr.cytech.bde.web.mvc.form;

import fr.cytech.bde.domain.Habilitation;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public class HabilitationForm {

    @NotNull(message = "Le plafond de dépense est obligatoire.")
    @DecimalMin(value = "0.00", message = "Le plafond ne peut pas être négatif.")
    @Digits(integer = 13, fraction = 2, message = "Le plafond doit contenir au maximum 2 décimales.")
    private BigDecimal plafondDepense;

    private boolean actif = true;

    @NotNull(message = "La date d'attribution est obligatoire.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateAttribution = LocalDate.now();

    public static HabilitationForm depuis(Habilitation habilitation) {
        HabilitationForm form = new HabilitationForm();
        if (habilitation != null) {
            form.setPlafondDepense(habilitation.getPlafondDepense());
            form.setActif(habilitation.isActif());
            form.setDateAttribution(habilitation.getDateAttribution());
        }
        return form;
    }

    public BigDecimal getPlafondDepense() {
        return plafondDepense;
    }

    public void setPlafondDepense(BigDecimal plafondDepense) {
        this.plafondDepense = plafondDepense;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    public LocalDate getDateAttribution() {
        return dateAttribution;
    }

    public void setDateAttribution(LocalDate dateAttribution) {
        this.dateAttribution = dateAttribution;
    }
}
