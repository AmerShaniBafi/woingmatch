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
    // Constructor voor het aanmaken van een registratieverzoek
    public RegisterRequest(
            String name,
            LocalDate dateOfBirth,
            String location,
            String email,
            String password) {

        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.location = location;
        this.email = email;
        this.password = password;
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
