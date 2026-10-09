package nl.woningmatch.woningmatch.Auth.Dto;
import java.time.LocalDate;

public class RegisterRequest {

    private String name;
    private LocalDate dateOfBirth;
    private String location;
    private String email;
    private String password;

    public RegisterRequest() {
    }

    public String getName() {
        return name;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getLocation() {
        return location;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
