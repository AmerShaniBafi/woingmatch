
package nl.woningmatch.woningmatch.Preference.Dto;

import java.math.BigDecimal;

public class PreferenceResponse {

    private Long id;
    private BigDecimal maxRent;
    private String preferredLocation;
    private Integer minBedrooms;
    private Integer minArea;
    private String energyLabel;
    private Boolean parkingRequired;
    private Boolean balconyRequired;
    private PreferenceWeightsDto weights;

    public PreferenceResponse(
            Long id,
            BigDecimal maxRent,
            String preferredLocation,
            Integer minBedrooms,
            Integer minArea,
            String energyLabel,
            Boolean parkingRequired,
            Boolean balconyRequired,
            PreferenceWeightsDto weights) {

        this.id = id;
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

    public PreferenceWeightsDto getWeights() {
        return weights;
    }
}
