package nl.woningmatch.woningmatch.Preference.Api;

import jakarta.validation.Valid;

import nl.woningmatch.woningmatch.Preference.Dto.PreferenceRequest;
import nl.woningmatch.woningmatch.Preference.Dto.PreferenceResponse;
import nl.woningmatch.woningmatch.Preference.Mapper.PreferenceMapper;
import nl.woningmatch.woningmatch.Preference.Model.PreferenceModel;
import nl.woningmatch.woningmatch.Preference.Service.PreferenceService;
import nl.woningmatch.woningmatch.Security.UserPrincipal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/preferences")
public class PreferenceController {

    private final PreferenceService preferenceService;

    public PreferenceController(PreferenceService preferenceService) {
        this.preferenceService = preferenceService;
    }

    // POST /api/preferences
    @PostMapping
    public ResponseEntity<PreferenceResponse> createPreferences(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody PreferenceRequest request) {

        Long userId = principal.id();

        PreferenceModel preference = PreferenceMapper.mapToEntity(request, null);

        PreferenceModel saved = preferenceService.createPreferences(userId, preference);

        PreferenceResponse response = PreferenceMapper.mapToDto(saved);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /api/preferences
    @GetMapping
    public ResponseEntity<PreferenceResponse> getPreferences(
            @AuthenticationPrincipal UserPrincipal principal) {

        Long userId = principal.id();

        PreferenceModel preference = preferenceService.getPreferences(userId);

        PreferenceResponse response = PreferenceMapper.mapToDto(preference);

        return ResponseEntity.ok(response);
    }

    // PUT /api/preferences
    @PutMapping
    public ResponseEntity<PreferenceResponse> updatePreferences(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody PreferenceRequest request) {

        Long userId = principal.id();

        PreferenceModel updatedPreference = PreferenceMapper.mapToEntity(request, null);

        PreferenceModel saved = preferenceService.updatePreferences(userId, updatedPreference);

        PreferenceResponse response = PreferenceMapper.mapToDto(saved);

        return ResponseEntity.ok(response);
    }
}