package nl.woningmatch.woningmatch.Service;

import nl.woningmatch.woningmatch.Auth.Dto.LoginRequest;
import nl.woningmatch.woningmatch.Auth.Dto.RegisterRequest;
import nl.woningmatch.woningmatch.Auth.Service.AuthService;
import nl.woningmatch.woningmatch.User.Model.UserModel;
import nl.woningmatch.woningmatch.User.Repository.UserRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final String NAME = "Henk";
    private static final LocalDate BIRTH_DATE = LocalDate.of(2002, 5, 23);
    private static final String LOCATION = "Tilburg";
    private static final String EMAIL = "henk@fontys.nl";
    private static final String PASSWORD = "12345678";
    private static final String HASHED_PASSWORD = "hashedPassword";

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    // ---------------------------------------------------------------
    // Registratie
    // ---------------------------------------------------------------

    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource("invalidRegistrationData")
    @DisplayName("Registratie met ontbrekende verplichte velden geeft 400")
    void register_WithMissingRequiredFields_ThrowsBadRequest(
            String scenario, RegisterRequest request, String expectedMessage) {

        ResponseStatusException exception = assertRegistrationRejected(request, HttpStatus.BAD_REQUEST);

        assertEquals(expectedMessage, exception.getReason());
    }

    static Stream<Arguments> invalidRegistrationData() {
        return Stream.of(
                Arguments.of("Naam ontbreekt",
                        registerRequest(null, BIRTH_DATE, LOCATION, EMAIL, PASSWORD),
                        "Naam is verplicht"),
                Arguments.of("Naam is leeg",
                        registerRequest("", BIRTH_DATE, LOCATION, EMAIL, PASSWORD),
                        "Naam is verplicht"),
                Arguments.of("Naam bevat alleen spaties",
                        registerRequest("   ", BIRTH_DATE, LOCATION, EMAIL, PASSWORD),
                        "Naam is verplicht"),
                Arguments.of("Geboortedatum ontbreekt",
                        registerRequest(NAME, null, LOCATION, EMAIL, PASSWORD),
                        "Geboortedatum is verplicht"),
                Arguments.of("Locatie ontbreekt",
                        registerRequest(NAME, BIRTH_DATE, null, EMAIL, PASSWORD),
                        "Locatie is verplicht"),
                Arguments.of("Locatie is leeg",
                        registerRequest(NAME, BIRTH_DATE, "", EMAIL, PASSWORD),
                        "Locatie is verplicht"),
                Arguments.of("E-mailadres ontbreekt",
                        registerRequest(NAME, BIRTH_DATE, LOCATION, null, PASSWORD),
                        "E-mailadres is verplicht"),
                Arguments.of("E-mailadres is leeg",
                        registerRequest(NAME, BIRTH_DATE, LOCATION, "", PASSWORD),
                        "E-mailadres is verplicht"),
                Arguments.of("Wachtwoord ontbreekt",
                        registerRequest(NAME, BIRTH_DATE, LOCATION, EMAIL, null),
                        "Wachtwoord is verplicht"),
                Arguments.of("Wachtwoord is leeg",
                        registerRequest(NAME, BIRTH_DATE, LOCATION, EMAIL, ""),
                        "Wachtwoord is verplicht")
        );
    }

    @Test
    @DisplayName("TC-01.2: Geldige registratie geeft gebruiker terug")
    void register_WithValidData_ReturnsUser() {
        RegisterRequest request = validRegisterRequest();

        when(passwordEncoder.encode(PASSWORD)).thenReturn(HASHED_PASSWORD);
        when(userRepository.save(any(UserModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserModel result = authService.register(request);

        assertNotNull(result);
        assertEquals(NAME, result.getName());
        assertEquals(EMAIL, result.getEmail());
        assertEquals(HASHED_PASSWORD, result.getPasswordHash());
        verify(userRepository).save(any(UserModel.class));
    }

    @Test
    @DisplayName("TC-01.3: Te kort wachtwoord geeft 400")
    void register_WithShortPassword_ThrowsBadRequest() {
        RegisterRequest request = registerRequest(NAME, BIRTH_DATE, LOCATION, EMAIL, "123");

        assertRegistrationRejected(request, HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("TC-01.4: Bestaand e-mailadres geeft 409")
    void register_WithExistingEmail_ThrowsConflict() {
        RegisterRequest request = validRegisterRequest();

        when(userRepository.existsByEmail(EMAIL)).thenReturn(true);

        assertRegistrationRejected(request, HttpStatus.CONFLICT);
    }

    @Test
    @DisplayName("TC-01.5: Gebruiker jonger dan 16 jaar geeft 400")
    void register_WithUnderageUser_ThrowsBadRequest() {
        RegisterRequest request = registerRequest(
                NAME, LocalDate.now().minusYears(15), LOCATION, EMAIL, PASSWORD);

        assertRegistrationRejected(request, HttpStatus.BAD_REQUEST);
    }

    // Login

    @Test
    @DisplayName("TC-02.1: Onbekend e-mailadres geeft 401")
    void login_WithUnknownEmail_ThrowsUnauthorized() {
        LoginRequest request = new LoginRequest("onbekend@fontys.nl", PASSWORD);

        when(userRepository.findByEmail("onbekend@fontys.nl")).thenReturn(Optional.empty());

        assertLoginRejected(request);
    }

    @Test
    @DisplayName("Extra: Onjuist wachtwoord geeft 401")
    void login_WithWrongPassword_ThrowsUnauthorized() {
        LoginRequest request = new LoginRequest(EMAIL, "verkeerd");

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(createUser()));
        when(passwordEncoder.matches("verkeerd", HASHED_PASSWORD)).thenReturn(false);

        assertLoginRejected(request);
    }

    @Test
    @DisplayName("TC-02.2: Geldige inloggegevens geven gebruiker terug")
    void login_WithValidCredentials_ReturnsUser() {
        LoginRequest request = new LoginRequest(EMAIL, PASSWORD);
        UserModel user = createUser();

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(PASSWORD, HASHED_PASSWORD)).thenReturn(true);

        UserModel result = authService.login(request);

        assertNotNull(result);
        assertEquals(EMAIL, result.getEmail());
        assertSame(user, result);
    }

    // Helpers

    private ResponseStatusException assertRegistrationRejected(
            RegisterRequest request, HttpStatus expectedStatus) {

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.register(request));

        assertEquals(expectedStatus, exception.getStatusCode());
        verify(userRepository, never()).save(any());

        return exception;
    }

    private void assertLoginRejected(LoginRequest request) {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.login(request));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }

    private static RegisterRequest validRegisterRequest() {
        return registerRequest(NAME, BIRTH_DATE, LOCATION, EMAIL, PASSWORD);
    }

    private static RegisterRequest registerRequest(
            String name, LocalDate birthDate, String location, String email, String password) {
        return new RegisterRequest(name, birthDate, location, email, password);
    }

    private UserModel createUser() {
        return new UserModel(NAME, BIRTH_DATE, LOCATION, EMAIL, HASHED_PASSWORD, "USER");
    }
}