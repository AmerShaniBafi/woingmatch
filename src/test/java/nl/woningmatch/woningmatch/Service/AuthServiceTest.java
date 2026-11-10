package nl.woningmatch.woningmatch.Service;

import nl.woningmatch.woningmatch.Auth.Dto.LoginRequest;
import nl.woningmatch.woningmatch.Auth.Dto.RegisterRequest;
import nl.woningmatch.woningmatch.Auth.Service.AuthService;
import nl.woningmatch.woningmatch.User.Model.UserModel;
import nl.woningmatch.woningmatch.User.Repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;


    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource("invalidRegistrationData")
    void register_WithMissingRequiredFields_ThrowsBadRequest(String scenario, RegisterRequest request, String expectedMessage)
    {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.register(request)
        );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode()
        );

        assertEquals(
                expectedMessage,
                exception.getReason()
        );

        verify(userRepository, never()).save(any(UserModel.class));
    }

    static Stream<Arguments> invalidRegistrationData() {

        LocalDate validDate = LocalDate.of(2002, 5, 23);

        return Stream.of(
                Arguments.of(
                        "Naam ontbreekt",
                        new RegisterRequest(null, validDate, "Tilburg",
                                "henk@fontys.nl", "12345678"),
                        "Naam is verplicht"
                ),

                Arguments.of(
                        "Naam is leeg",
                        new RegisterRequest("", validDate, "Tilburg",
                                "henk@fontys.nl", "12345678"),
                        "Naam is verplicht"
                ),

                Arguments.of(
                        "Naam bevat alleen spaties",
                        new RegisterRequest("   ", validDate, "Tilburg",
                                "henk@fontys.nl", "12345678"),
                        "Naam is verplicht"
                ),

                Arguments.of(
                        "Geboortedatum ontbreekt",
                        new RegisterRequest("Henk", null, "Tilburg",
                                "henk@fontys.nl", "12345678"),
                        "Geboortedatum is verplicht"
                ),

                Arguments.of(
                        "Locatie ontbreekt",
                        new RegisterRequest("Henk", validDate, null,
                                "henk@fontys.nl", "12345678"),
                        "Locatie is verplicht"
                ),

                Arguments.of(
                        "Locatie is leeg",
                        new RegisterRequest("Henk", validDate, "",
                                "henk@fontys.nl", "12345678"),
                        "Locatie is verplicht"
                ),

                Arguments.of(
                        "E-mailadres ontbreekt",
                        new RegisterRequest("Henk", validDate, "Tilburg",
                                null, "12345678"),
                        "E-mailadres is verplicht"
                ),

                Arguments.of(
                        "E-mailadres is leeg",
                        new RegisterRequest("Henk", validDate, "Tilburg",
                                "", "12345678"),
                        "E-mailadres is verplicht"
                ),

                Arguments.of(
                        "Wachtwoord ontbreekt",
                        new RegisterRequest("Henk", validDate, "Tilburg",
                                "henk@fontys.nl", null),
                        "Wachtwoord is verplicht"
                ),

                Arguments.of(
                        "Wachtwoord is leeg",
                        new RegisterRequest("Henk", validDate, "Tilburg",
                                "henk@fontys.nl", ""),
                        "Wachtwoord is verplicht"
                )
        );
    }


    // TC-01.2: Geldige registratie
    @Test
    void register_WithValidData_ReturnsUser() {

        RegisterRequest request = new RegisterRequest(
                "Henk",
                LocalDate.of(2002, 5, 23),
                "Tilburg",
                "henk@fontys.nl",
                "12345678"
        );

        when(passwordEncoder.encode("12345678"))
                .thenReturn("hashedPassword");

        when(userRepository.save(any(UserModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserModel result = authService.register(request);

        assertNotNull(result);
        assertEquals("Henk", result.getName());
        assertEquals("henk@fontys.nl", result.getEmail());
        assertEquals("hashedPassword", result.getPasswordHash());

        verify(userRepository).save(any(UserModel.class));
    }

    // TC-01.3: Wachtwoord te kort
    @Test
    void register_WithShortPassword_ThrowsBadRequest() {

        RegisterRequest request = new RegisterRequest(
                "Henk",
                LocalDate.of(2002, 5, 23),
                "Tilburg",
                "henk@fontys.nl",
                "123"
        );

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.register(request)
        );

        assertEquals(HttpStatus.BAD_REQUEST,
                exception.getStatusCode());

        verify(userRepository, never()).save(any());
    }

    // TC-01.4: E-mailadres bestaat al
    @Test
    void register_WithExistingEmail_ThrowsConflict() {

        RegisterRequest request = new RegisterRequest(
                "Henk",
                LocalDate.of(2002, 5, 23),
                "Tilburg",
                "henk@fontys.nl",
                "12345678"
        );

        when(userRepository.existsByEmail("henk@fontys.nl"))
                .thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.register(request)
        );

        assertEquals(HttpStatus.CONFLICT,
                exception.getStatusCode());

        verify(userRepository, never()).save(any());
    }

    // TC-01.5: Gebruiker jonger dan 16 jaar
    @Test
    void register_WithUnderageUser_ThrowsBadRequest() {

        RegisterRequest request = new RegisterRequest(
                "Henk",
                LocalDate.now().minusYears(15),
                "Tilburg",
                "henk@fontys.nl",
                "12345678"
        );

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.register(request)
        );

        assertEquals(HttpStatus.BAD_REQUEST,
                exception.getStatusCode());

        verify(userRepository, never()).save(any());
    }

    // TC-02.1: Ongeldig e-mailadres
    @Test
    void login_WithUnknownEmail_ThrowsUnauthorized() {

        LoginRequest request = new LoginRequest(
                "onbekend@fontys.nl",
                "12345678"
        );

        when(userRepository.findByEmail("onbekend@fontys.nl"))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.login(request)
        );

        assertEquals(HttpStatus.UNAUTHORIZED,
                exception.getStatusCode());
    }

    // Extra scenario: Onjuist wachtwoord
    @Test
    void login_WithWrongPassword_ThrowsUnauthorized() {

        LoginRequest request = new LoginRequest(
                "henk@fontys.nl",
                "verkeerd"
        );

        UserModel user = createUser();

        when(userRepository.findByEmail("henk@fontys.nl"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("verkeerd", "hashedPassword"))
                .thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.login(request)
        );

        assertEquals(HttpStatus.UNAUTHORIZED,
                exception.getStatusCode());
    }

    // TC-02.2: Geldige inloggegevens
    @Test
    void login_WithValidCredentials_ReturnsUser() {

        LoginRequest request = new LoginRequest(
                "henk@fontys.nl",
                "12345678"
        );

        UserModel user = createUser();

        when(userRepository.findByEmail("henk@fontys.nl"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("12345678", "hashedPassword"))
                .thenReturn(true);

        UserModel result = authService.login(request);

        assertNotNull(result);
        assertEquals("henk@fontys.nl", result.getEmail());
        assertSame(user, result);
    }

    private UserModel createUser() {
        return new UserModel(
                "Henk",
                LocalDate.of(2002, 5, 23),
                "Tilburg",
                "henk@fontys.nl",
                "hashedPassword",
                "USER"
        );
    }
}