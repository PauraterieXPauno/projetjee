package fr.cytech.bde.web.api.dto;

public record MembreResponse(
        Long id,
        String nom,
        String prenom,
        String email,
        String role,
        String promotion,
        HabilitationResponse habilitation
) {
}
