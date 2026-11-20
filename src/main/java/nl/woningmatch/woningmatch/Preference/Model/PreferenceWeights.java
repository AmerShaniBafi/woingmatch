
package nl.woningmatch.woningmatch.Preference.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class PreferenceWeights {

    @Column(name = "max_rent_weight", nullable = false)
    private Integer maxRentWeight;

    @Column(name = "location_weight", nullable = false)
    private Integer locationWeight;

    @Column(name = "bedrooms_weight", nullable = false)
    private Integer bedroomsWeight;

    @Column(name = "area_weight", nullable = false)
    private Integer areaWeight;

    @Column(name = "energy_label_weight", nullable = false)
    private Integer energyLabelWeight;

    @Column(name = "parking_weight", nullable = false)
    private Integer parkingWeight;

    @Column(name = "balcony_weight", nullable = false)
    private Integer balconyWeight;

    protected PreferenceWeights() {
        // Vereist door JPA
    }

    public PreferenceWeights(
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

    public Integer getLocationWeight() {
        return locationWeight;
    }

    public Integer getBedroomsWeight() {
        return bedroomsWeight;
    }

    public Integer getAreaWeight() {
        return areaWeight;
    }

    public Integer getEnergyLabelWeight() {
        return energyLabelWeight;
    }

    public Integer getParkingWeight() {
        return parkingWeight;
    }

    public Integer getBalconyWeight() {
        return balconyWeight;
    }
}
