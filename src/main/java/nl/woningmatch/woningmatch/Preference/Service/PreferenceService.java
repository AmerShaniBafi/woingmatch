
package nl.woningmatch.woningmatch.Preference.Service;

import nl.woningmatch.woningmatch.Preference.Model.PreferenceModel;
import nl.woningmatch.woningmatch.Preference.Model.PreferenceWeights;
import nl.woningmatch.woningmatch.Preference.Repository.PreferenceRepository;
import nl.woningmatch.woningmatch.User.Model.UserModel;
import nl.woningmatch.woningmatch.User.Repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PreferenceService {

    private final PreferenceRepository preferenceRepository;
    private final UserRepository userRepository;

    public PreferenceService(PreferenceRepository preferenceRepository, UserRepository userRepository)
    {
        this.preferenceRepository = preferenceRepository;
        this.userRepository = userRepository;
    }

    // Nieuwe woonvoorkeuren opslaan
    @Transactional
    public PreferenceModel createPreferences(Long userId, PreferenceModel preference)
    {
        validatePreferences(preference);

        if (preferenceRepository.existsByUserId(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Gebruiker heeft al woonvoorkeuren"
            );
        }

        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Gebruiker niet gevonden"
                ));

        // We koppelen de voorkeuren aan de gebruiker
        PreferenceModel newPreference = new PreferenceModel(
                user,
                preference.getMaxRent(),
                preference.getPreferredLocation(),
                preference.getMinBedrooms(),
                preference.getMinArea(),
                preference.getEnergyLabel(),
                preference.getParkingRequired(),
                preference.getBalconyRequired(),
                preference.getWeights()
        );

        return preferenceRepository.save(newPreference);
    }

    // Eigen woonvoorkeuren ophalen
    @Transactional(readOnly = true)
    public PreferenceModel getPreferences(Long userId) {

        return preferenceRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Geen woonvoorkeuren gevonden"
                ));
    }

    // Bestaande woonvoorkeuren wijzigen
    @Transactional
    public PreferenceModel updatePreferences(Long userId, PreferenceModel updatedPreference)
    {
        validatePreferences(updatedPreference);

        PreferenceModel preference = preferenceRepository
                .findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Geen woonvoorkeuren gevonden"
                ));

        preference.updatePreferences(
                updatedPreference.getMaxRent(),
                updatedPreference.getPreferredLocation(),
                updatedPreference.getMinBedrooms(),
                updatedPreference.getMinArea(),
                updatedPreference.getEnergyLabel(),
                updatedPreference.getParkingRequired(),
                updatedPreference.getBalconyRequired(),
                updatedPreference.getWeights()
        );

        // Hibernate dirty checking slaat wijzigingen op
        return preference;
    }

    // Businessregels controleren
    private void validatePreferences(PreferenceModel preference) {

        if (preference == null) {
            throw badRequest("Woonvoorkeuren zijn verplicht");
        }

        if (preference.getMaxRent() == null
                || preference.getMaxRent().signum() <= 0
                || preference.getMaxRent().scale() > 2
                || preference.getMaxRent().precision()
                - preference.getMaxRent().scale() > 8) {
            throw badRequest("Ongeldige maximale huurprijs");
        }

        if (preference.getPreferredLocation() == null
                || preference.getPreferredLocation().isBlank()) {
            throw badRequest("Voorkeurslocatie is verplicht");
        }

        if (preference.getMinBedrooms() == null
                || preference.getMinBedrooms() < 0) {
            throw badRequest("Minimum slaapkamers moet 0 of hoger zijn");
        }

        if (preference.getMinArea() == null
                || preference.getMinArea() <= 0) {
            throw badRequest("Minimum oppervlakte moet groter zijn dan 0");
        }

        if (preference.getEnergyLabel() == null
                || preference.getEnergyLabel().isBlank()) {
            throw badRequest("Energielabel is verplicht");
        }

        if (preference.getParkingRequired() == null
                || preference.getBalconyRequired() == null) {
            throw badRequest("Parkeren en balkon moeten ingevuld zijn");
        }

        validateWeights(preference.getWeights());
    }

    private void validateWeights(PreferenceWeights weights) {

        if (weights == null) {
            throw badRequest("Gewichten zijn verplicht");
        }

        Integer[] values = {
                weights.getMaxRentWeight(),
                weights.getLocationWeight(),
                weights.getBedroomsWeight(),
                weights.getAreaWeight(),
                weights.getEnergyLabelWeight(),
                weights.getParkingWeight(),
                weights.getBalconyWeight()
        };

        int total = 0;

        for (Integer weight : values) {

            if (weight == null || weight < 0 || weight > 100) {
                throw badRequest(
                        "Elk gewicht moet tussen 0 en 100 liggen"
                );
            }

            total += weight;
        }

        if (total != 100) {
            throw badRequest(
                    "De gewichten moeten samen 100% zijn"
            );
        }
    }

    private ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                message
        );
    }
}
