package nl.woningmatch.woningmatch.Security;

public record UserPrincipal(
        Long id,
        String email,
        String role
) {}