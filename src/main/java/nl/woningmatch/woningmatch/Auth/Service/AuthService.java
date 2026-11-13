
package nl.woningmatch.woningmatch.Auth.Service;

import nl.woningmatch.woningmatch.Auth.Dto.LoginRequest;
import nl.woningmatch.woningmatch.Auth.Dto.RegisterRequest;
import nl.woningmatch.woningmatch.User.Model.UserModel;
import nl.woningmatch.woningmatch.User.Repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Gebruiker registreren

    public UserModel register(RegisterRequest request) {

        // Controleer of registratiegegevens aanwezig zijn
        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Registratiegegevens ontbreken"
            );
        }

        // Naam is verplicht
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Naam is verplicht"
            );
        }

        // Geboortedatum is verplicht
        if (request.getDateOfBirth() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Geboortedatum is verplicht"
            );
        }

        // Locatie is verplicht
        if (request.getLocation() == null || request.getLocation().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Locatie is verplicht"
            );
        }

        // E-mailadres is verplicht
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "E-mailadres is verplicht"
            );
        }

        // Wachtwoord is verplicht
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Wachtwoord is verplicht"
            );
        }

        // Wachtwoord moet minimaal 8 tekens bevatten
        if (request.getPassword().length() < 8) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Wachtwoord moet minimaal 8 tekens bevatten"
            );
        }

        // Gebruiker moet minimaal 16 jaar zijn
        LocalDate minimumDate = LocalDate.now().minusYears(16);

        if (request.getDateOfBirth().isAfter(minimumDate)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Gebruiker moet minimaal 16 jaar oud zijn"
            );
        }

        // Controleer of het e-mailadres al bestaat
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "E-mailadres bestaat al"
            );
        }

        String passwordHash = passwordEncoder.encode(request.getPassword());

        UserModel user = new UserModel(
                request.getName(),
                request.getDateOfBirth(),
                request.getLocation(),
                request.getEmail(),
                passwordHash,
                "USER"
        );

        return userRepository.save(user);
    }

    // Gebruiker inloggen
    public UserModel login(LoginRequest request) {

        UserModel user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Ongeldig e-mailadres of wachtwoord"
                        )
                );

        if (request.getPassword() == null ||
                !passwordEncoder.matches(
                        request.getPassword(),
                        user.getPasswordHash())) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Ongeldig e-mailadres of wachtwoord"
            );
        }

        return user;
    }

}
