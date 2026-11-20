
package nl.woningmatch.woningmatch.Preference.Mapper;

import nl.woningmatch.woningmatch.Preference.Dto.PreferenceRequest;
import nl.woningmatch.woningmatch.Preference.Dto.PreferenceResponse;
import nl.woningmatch.woningmatch.Preference.Dto.PreferenceWeightsDto;
import nl.woningmatch.woningmatch.Preference.Model.PreferenceModel;
import nl.woningmatch.woningmatch.Preference.Model.PreferenceWeights;
import nl.woningmatch.woningmatch.User.Model.UserModel;

public class PreferenceMapper {

    // Request DTO omzetten naar een nieuwe entity
    public static PreferenceModel mapToEntity(
            PreferenceRequest request,
            UserModel user) {

        return new PreferenceModel(
                user,
                request.getMaxRent(),
                request.getPreferredLocation(),
                request.getMinBedrooms(),
                request.getMinArea(),
                request.getEnergyLabel(),
                request.getParkingRequired(),
                request.getBalconyRequired(),
                mapWeightsToEntity(request.getWeights())
        );
    }

    // Bestaande entity bijwerken met requestgegevens
//    public static void updateEntity(
//            PreferenceModel preference,
//            PreferenceRequest request) {
//
//        preference.updatePreferences(
//                request.getMaxRent(),
//                request.getPreferredLocation(),
//                request.getMinBedrooms(),
//                request.getMinArea(),
//                request.getEnergyLabel(),
//                request.getParkingRequired(),
//                request.getBalconyRequired(),
//                mapWeightsToEntity(request.getWeights())
//        );
//    }

    // Entity omzetten naar response DTO
    public static PreferenceResponse mapToDto(
            PreferenceModel preference) {

        return new PreferenceResponse(
                preference.getId(),
                preference.getMaxRent(),
                preference.getPreferredLocation(),
                preference.getMinBedrooms(),
                preference.getMinArea(),
                preference.getEnergyLabel(),
                preference.getParkingRequired(),
                preference.getBalconyRequired(),
                mapWeightsToDto(preference.getWeights())
        );
    }

    private static PreferenceWeights mapWeightsToEntity(
            PreferenceWeightsDto dto) {

        return new PreferenceWeights(
                dto.getMaxRentWeight(),
                dto.getLocationWeight(),
                dto.getBedroomsWeight(),
                dto.getAreaWeight(),
                dto.getEnergyLabelWeight(),
                dto.getParkingWeight(),
                dto.getBalconyWeight()
        );
    }

    private static PreferenceWeightsDto mapWeightsToDto(
            PreferenceWeights weights) {

        return new PreferenceWeightsDto(
                weights.getMaxRentWeight(),
                weights.getLocationWeight(),
                weights.getBedroomsWeight(),
                weights.getAreaWeight(),
                weights.getEnergyLabelWeight(),
                weights.getParkingWeight(),
                weights.getBalconyWeight()
        );
    }
}
