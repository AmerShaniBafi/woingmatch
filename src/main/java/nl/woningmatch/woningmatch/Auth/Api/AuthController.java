
package nl.woningmatch.woningmatch.Auth.Api;

import nl.woningmatch.woningmatch.Auth.Dto.LoginRequest;
import nl.woningmatch.woningmatch.Auth.Dto.RegisterRequest;
import nl.woningmatch.woningmatch.Auth.Service.AuthService;

import nl.woningmatch.woningmatch.Security.CookieService;
import nl.woningmatch.woningmatch.Security.JWTService;
import nl.woningmatch.woningmatch.Security.UserPrincipal;

import nl.woningmatch.woningmatch.User.Dto.UserResponse;
import nl.woningmatch.woningmatch.User.Model.UserModel;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JWTService jwtService;
    private final CookieService cookieService;

    public AuthController(
            AuthService authService,
            JWTService jwtService,
            CookieService cookieService) {

        this.authService = authService;
        this.jwtService = jwtService;
        this.cookieService = cookieService;
    }

    // Account aanmaken
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @RequestBody RegisterRequest request) {

        UserModel user = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toUserResponse(user));
    }

    // Inloggen en JWT-cookie aanmaken
    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(
            @RequestBody LoginRequest request) {

        // Controleer de inloggegevens
        UserModel user = authService.login(request);

        // Genereer een JWT-token
        String token = jwtService.generateToken(user);

        // Stuur JWT als HttpOnly-cookie naar de browser
        return ResponseEntity.ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        cookieService.createJwtCookie(token).toString()
                )
                .body(toUserResponse(user));
    }

    // UserModel omzetten naar UserResponse
    private UserResponse toUserResponse(UserModel user) {

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }


}
