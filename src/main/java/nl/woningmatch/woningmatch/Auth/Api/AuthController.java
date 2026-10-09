package nl.woningmatch.woningmatch.Auth.Api;

import nl.woningmatch.woningmatch.Auth.Dto.RegisterRequest;
import nl.woningmatch.woningmatch.User.Dto.UserResponse;
import nl.woningmatch.woningmatch.Auth.Service.AuthService;
import nl.woningmatch.woningmatch.User.Model.UserModel;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @RequestBody RegisterRequest request
    ) {

        UserModel user = authService.register(request);

        UserResponse response = new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}