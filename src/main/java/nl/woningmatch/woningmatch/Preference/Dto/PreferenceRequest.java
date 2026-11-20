
package nl.woningmatch.woningmatch.Preference.Dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class PreferenceRequest {

    @NotNull
    @DecimalMin("0.01")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal maxRent;

    @NotBlank
    private String preferredLocation;

    @NotNull
    @Min(0)
    private Integer minBedrooms;

    @NotNull
    @Min(1)
    private Integer minArea;

    @NotBlank
    private String energyLabel;

    @NotNull
    private Boolean parkingRequired;

    @NotNull
    private Boolean balconyRequired;

    @NotNull
    @Valid
    private PreferenceWeightsDto weights;

    public PreferenceRequest() {
    }

    public BigDecimal getMaxRent() {
        return maxRent;
    }

    public void setMaxRent(BigDecimal maxRent) {
        this.maxRent = maxRent;
    }

    public String getPreferredLocation() {
        return preferredLocation;
    }

    public void setPreferredLocation(String preferredLocation) {
        this.preferredLocation = preferredLocation;
    }

    public Integer getMinBedrooms() {
        return minBedrooms;
    }

    public void setMinBedrooms(Integer minBedrooms) {
        this.minBedrooms = minBedrooms;
    }

    public Integer getMinArea() {
        return minArea;
    }

    public void setMinArea(Integer minArea) {
        this.minArea = minArea;
    }

    public String getEnergyLabel() {
        return energyLabel;
    }

    public void setEnergyLabel(String energyLabel) {
        this.energyLabel = energyLabel;
    }

    public Boolean getParkingRequired() {
        return parkingRequired;
    }

    public void setParkingRequired(Boolean parkingRequired) {
        this.parkingRequired = parkingRequired;
    }

    public Boolean getBalconyRequired() {
        return balconyRequired;
    }

    public void setBalconyRequired(Boolean balconyRequired) {
        this.balconyRequired = balconyRequired;
    }

    public PreferenceWeightsDto getWeights() {
        return weights;
    }

    public void setWeights(PreferenceWeightsDto weights) {
        this.weights = weights;
    }
}
