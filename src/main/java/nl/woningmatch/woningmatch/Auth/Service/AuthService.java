
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

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "E-mailadres bestaat al"
            );
        }

        if (request.getPassword() == null ||
                request.getPassword().length() < 8) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Wachtwoord moet minimaal 8 tekens bevatten"
            );
        }

        LocalDate minimumDate = LocalDate.now().minusYears(16);

        if (request.getDateOfBirth() == null ||
                request.getDateOfBirth().isAfter(minimumDate)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Gebruiker moet minimaal 16 jaar oud zijn"
            );
        }

        String passwordHash =
                passwordEncoder.encode(request.getPassword());

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

    // Gebruiker ophalen via ID
    public UserModel findById(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Gebruiker niet gevonden"
                        )
                );
    }
}
