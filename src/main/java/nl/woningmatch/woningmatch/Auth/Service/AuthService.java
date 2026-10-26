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
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserModel register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("E-mailadres bestaat al");
        }

        if (request.getPassword().length() < 8) {
            throw new RuntimeException(
                    "Wachtwoord moet minimaal 8 tekens bevatten"
            );
        }

        LocalDate minimumDate =
                LocalDate.now().minusYears(16);

        if (request.getDateOfBirth().isAfter(minimumDate)) {
            throw new RuntimeException(
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


    public UserModel login(LoginRequest request) {

        UserModel user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Ongeldig e-mailadres of wachtwoord"
                ));

        boolean passwordCorrect = passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash()
        );

        if (!passwordCorrect) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Ongeldig e-mailadres of wachtwoord"
            );
        }

        return user;
    }

}