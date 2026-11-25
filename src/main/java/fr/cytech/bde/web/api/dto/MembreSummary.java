package fr.cytech.bde.web.api.dto;

public record MembreSummary(
        Long id,
        String nom,
        String prenom,
        String role
) {
}
