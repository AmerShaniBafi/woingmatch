package nl.woningmatch.woningmatch.API;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import nl.woningmatch.woningmatch.User.Repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuthApiTest {

    private static final String REGISTER_URL = "/api/auth/register";
    private static final String LOGIN_URL = "/api/auth/login";
    private static final String CSRF_URL = "/api/auth/csrf";

    private static final String NAME = "Henk";
    private static final String DATE_OF_BIRTH = "2002-05-23";
    private static final String LOCATION = "Tilburg";
    private static final String PASSWORD = "12345678";

    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:18");

    static {
        POSTGRES.start();
    }

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    // Registratie

    @Test
    @DisplayName("API TC-01.1: Succesvolle registratie geeft 201")
    void register_WithValidData_ReturnsCreated() {
        String email = uniqueEmail();

        postRegister(registerBody(NAME, DATE_OF_BIRTH, LOCATION, email, PASSWORD))
                .then()
                .statusCode(201)
                .body("name", equalTo(NAME))
                .body("email", equalTo(email))
                .body("role", equalTo("USER"))
                .body("passwordHash", nullValue());

        assertTrue(userRepository.existsByEmail(email));
    }

    @Test
    @DisplayName("API TC-01.2: Registratie zonder naam geeft 400")
    void register_WithoutName_ReturnsBadRequest() {
        postRegister(registerBody("", DATE_OF_BIRTH, LOCATION, uniqueEmail(), PASSWORD))
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("API TC-01.3: Wachtwoord korter dan 8 tekens geeft 400")
    void register_WithShortPassword_ReturnsBadRequest() {
        postRegister(registerBody(NAME, DATE_OF_BIRTH, LOCATION, uniqueEmail(), "1234567"))
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("API TC-01.4: Dubbel e-mailadres geeft 409")
    void register_WithExistingEmail_ReturnsConflict() {
        String requestBody = registerBody(NAME, DATE_OF_BIRTH, LOCATION, uniqueEmail(), PASSWORD);

        postRegister(requestBody)
                .then()
                .statusCode(201);

        postRegister(requestBody)
                .then()
                .statusCode(409);
    }

    @Test
    @DisplayName("API TC-01.5: Gebruiker jonger dan 16 jaar geeft 400")
    void register_WithUnderageUser_ReturnsBadRequest() {
        postRegister(registerBody(NAME, "2020-05-23", LOCATION, uniqueEmail(), PASSWORD))
                .then()
                .statusCode(400);
    }

    // Login

    @Test
    @DisplayName("API TC-02.1: Inloggen met verkeerde gegevens geeft 401")
    void login_WithWrongCredentials_ReturnsUnauthorized() {
        postLogin(loginBody("onbekend@example.com", "verkeerd123"))
                .then()
                .statusCode(401);
    }

    @Test
    @DisplayName("API TC-02.2: Succesvol inloggen geeft 200 en een HttpOnly cookie")
    void login_WithValidCredentials_ReturnsOkAndJwtCookie() {
        String email = uniqueEmail();

        postRegister(registerBody(NAME, DATE_OF_BIRTH, LOCATION, email, PASSWORD))
                .then()
                .statusCode(201);

        postLogin(loginBody(email, PASSWORD))
                .then()
                .statusCode(200)
                .body("email", equalTo(email))
                .header("Set-Cookie", containsString("HttpOnly"));
    }

    // Helpers

    private Response postRegister(String body) {
        return requestWithCsrf()
                .body(body)
                .when()
                .post(REGISTER_URL);
    }

    private Response postLogin(String body) {
        return requestWithCsrf()
                .body(body)
                .when()
                .post(LOGIN_URL);
    }

    /**
     * Haalt een CSRF-token op en geeft een request terug met de vereiste
     * cookie en header voor POST-requests.
     */
    private RequestSpecification requestWithCsrf() {
        Response response = given()
                .when()
                .get(CSRF_URL);

        response.then().statusCode(200);

        String csrfToken = response.asString();
        String csrfCookie = response.getCookie("XSRF-TOKEN");

        return given()
                .contentType(ContentType.JSON)
                .cookie("XSRF-TOKEN", csrfCookie)
                .header("X-XSRF-TOKEN", csrfToken);
    }

    private static String uniqueEmail() {
        return "test-" + UUID.randomUUID() + "@example.com";
    }

    private static String registerBody(
            String name, String dateOfBirth, String location, String email, String password) {

        return """
                {
                    "name": "%s",
                    "dateOfBirth": "%s",
                    "location": "%s",
                    "email": "%s",
                    "password": "%s"
                }
                """.formatted(name, dateOfBirth, location, email, password);
    }

    private static String loginBody(String email, String password) {
        return """
                {
                    "email": "%s",
                    "password": "%s"
                }
                """.formatted(email, password);
    }
}