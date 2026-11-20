
package nl.woningmatch.woningmatch.Preference.Repository;

import nl.woningmatch.woningmatch.Preference.Model.PreferenceModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PreferenceRepository
        extends JpaRepository<PreferenceModel, Long> {

    Optional<PreferenceModel> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}
