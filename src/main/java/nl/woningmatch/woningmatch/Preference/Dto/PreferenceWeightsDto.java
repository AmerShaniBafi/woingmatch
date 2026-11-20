
package nl.woningmatch.woningmatch.Preference.Dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class PreferenceWeightsDto {

    @NotNull
    @Min(0)
    @Max(100)
    private Integer maxRentWeight;

    @NotNull
    @Min(0)
    @Max(100)
    private Integer locationWeight;

    @NotNull
    @Min(0)
    @Max(100)
    private Integer bedroomsWeight;

    @NotNull
    @Min(0)
    @Max(100)
    private Integer areaWeight;

    @NotNull
    @Min(0)
    @Max(100)
    private Integer energyLabelWeight;

    @NotNull
    @Min(0)
    @Max(100)
    private Integer parkingWeight;

    @NotNull
    @Min(0)
    @Max(100)
    private Integer balconyWeight;

    public PreferenceWeightsDto(
            Integer maxRentWeight,
            Integer locationWeight,
            Integer bedroomsWeight,
            Integer areaWeight,
            Integer energyLabelWeight,
            Integer parkingWeight,
            Integer balconyWeight) {

        this.maxRentWeight = maxRentWeight;
        this.locationWeight = locationWeight;
        this.bedroomsWeight = bedroomsWeight;
        this.areaWeight = areaWeight;
        this.energyLabelWeight = energyLabelWeight;
        this.parkingWeight = parkingWeight;
        this.balconyWeight = balconyWeight;
    }

    public Integer getMaxRentWeight() {
        return maxRentWeight;
    }

    public void setMaxRentWeight(Integer maxRentWeight) {
        this.maxRentWeight = maxRentWeight;
    }

    public Integer getLocationWeight() {
        return locationWeight;
    }

    public void setLocationWeight(Integer locationWeight) {
        this.locationWeight = locationWeight;
    }

    public Integer getBedroomsWeight() {
        return bedroomsWeight;
    }

    public void setBedroomsWeight(Integer bedroomsWeight) {
        this.bedroomsWeight = bedroomsWeight;
    }

    public Integer getAreaWeight() {
        return areaWeight;
    }

    public void setAreaWeight(Integer areaWeight) {
        this.areaWeight = areaWeight;
    }

    public Integer getEnergyLabelWeight() {
        return energyLabelWeight;
    }

    public void setEnergyLabelWeight(Integer energyLabelWeight) {
        this.energyLabelWeight = energyLabelWeight;
    }

    public Integer getParkingWeight() {
        return parkingWeight;
    }

    public void setParkingWeight(Integer parkingWeight) {
        this.parkingWeight = parkingWeight;
    }

    public Integer getBalconyWeight() {
        return balconyWeight;
    }

    public void setBalconyWeight(Integer balconyWeight) {
        this.balconyWeight = balconyWeight;
    }
}
