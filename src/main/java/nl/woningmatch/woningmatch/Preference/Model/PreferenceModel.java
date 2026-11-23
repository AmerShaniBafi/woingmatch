
package nl.woningmatch.woningmatch.Preference.Model;

import jakarta.persistence.*;
import nl.woningmatch.woningmatch.User.Model.UserModel;

import java.math.BigDecimal;

@Entity
@Table(name = "preferences")
public class PreferenceModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserModel user;

    @Column(name = "max_rent", nullable = false, precision = 10, scale = 2)
    private BigDecimal maxRent;

    @Column(name = "preferred_location", nullable = false)
    private String preferredLocation;

    @Column(name = "min_bedrooms", nullable = false)
    private Integer minBedrooms;

    @Column(name = "min_area", nullable = false)
    private Integer minArea;

    @Column(name = "energy_label", nullable = false)
    private String energyLabel;

    @Column(name = "parking_required", nullable = false)
    private Boolean parkingRequired;

    @Column(name = "balcony_required", nullable = false)
    private Boolean balconyRequired;

    @Embedded
    private PreferenceWeights weights;

    protected PreferenceModel() {
        // Vereist door JPA
    }

    public PreferenceModel(
            UserModel user,
            BigDecimal maxRent,
            String preferredLocation,
            Integer minBedrooms,
            Integer minArea,
            String energyLabel,
            Boolean parkingRequired,
            Boolean balconyRequired,
            PreferenceWeights weights) {

        this.user = user;

        updatePreferences(
                maxRent,
                preferredLocation,
                minBedrooms,
                minArea,
                energyLabel,
                parkingRequired,
                balconyRequired,
                weights
        );
    }

    public void updatePreferences(
            BigDecimal maxRent,
            String preferredLocation,
            Integer minBedrooms,
            Integer minArea,
            String energyLabel,
            Boolean parkingRequired,
            Boolean balconyRequired,
            PreferenceWeights weights) {

        this.maxRent = maxRent;
        this.preferredLocation = preferredLocation;
        this.minBedrooms = minBedrooms;
        this.minArea = minArea;
        this.energyLabel = energyLabel;
        this.parkingRequired = parkingRequired;
        this.balconyRequired = balconyRequired;
        this.weights = weights;
    }

    public Long getId() {
        return id;
    }

    public UserModel getUser() {
        return user;
    }

    public BigDecimal getMaxRent() {
        return maxRent;
    }

    public String getPreferredLocation() {
        return preferredLocation;
    }

    public Integer getMinBedrooms() {
        return minBedrooms;
    }

    public Integer getMinArea() {
        return minArea;
    }

    public String getEnergyLabel() {
        return energyLabel;
    }

    public Boolean getParkingRequired() {
        return parkingRequired;
    }

    public Boolean getBalconyRequired() {
        return balconyRequired;
    }

    public PreferenceWeights getWeights() {
        return weights;
    }
}
