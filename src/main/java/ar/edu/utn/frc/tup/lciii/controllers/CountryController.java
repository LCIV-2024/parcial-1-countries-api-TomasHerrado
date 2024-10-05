package ar.edu.utn.frc.tup.lciii.controllers;
import ar.edu.utn.frc.tup.lciii.dtos.common.CountryDTO;
import ar.edu.utn.frc.tup.lciii.dtos.common.CountryRequestDTO;
import ar.edu.utn.frc.tup.lciii.entity.CountryEntity;
import ar.edu.utn.frc.tup.lciii.model.Country;
import ar.edu.utn.frc.tup.lciii.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CountryController {

    private final CountryService countryService;

    @GetMapping("/countries")
    public ResponseEntity<List<CountryDTO>> getAllCountries(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code) {

        List<CountryDTO> countries = countryService.getAllCountries()
                .stream()
                .map(countryService::mapToDTO)
                .filter(countryDTO -> (name == null || countryDTO.getName().equalsIgnoreCase(name)) &&
                        (code == null || countryDTO.getCode().equalsIgnoreCase(code)))
                .collect(Collectors.toList());

        return ResponseEntity.ok(countries);
    }
    @GetMapping("/countries/continent/{continent}")
    public ResponseEntity<List<CountryDTO>> getCountriesByContinent(@PathVariable String continent) {
        List<String> validContinents = List.of("Africa", "Americas", "Asia", "Europe", "Oceania", "Antarctic");
        if (!validContinents.contains(continent)) {
            return ResponseEntity.badRequest().build();
        }
        List<CountryDTO> countries = countryService.getCountriesByContinent(continent)
                .stream()
                .map(countryService::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(countries);
    }
    @GetMapping("/countries/{language}/language")
    public ResponseEntity<List<CountryDTO>> getCountriesByLanguage(@PathVariable String language) {
        List<String> validLanguages = List.of("English", "Spanish", "French", "German", "Portuguese",
                "Chinese", "Arabic", "Russian", "Hindi", "Swahili");
        if (!validLanguages.contains(language)) {
            return ResponseEntity.badRequest().build();
        }
        List<CountryDTO> countries = countryService.getCountriesByLanguage(language)
                .stream()
                .map(countryService::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(countries);
    }
    @GetMapping("/countries/most-borders")
    public ResponseEntity<CountryDTO> getCountryWithMostBorders() {
        Optional<Country> countryOpt = Optional.ofNullable(countryService.getCountryWithMostBorders());

        return countryOpt.map(country -> ResponseEntity.ok(countryService.mapToDTO(country)))
                .orElse(ResponseEntity.notFound().build());
    }
    @PostMapping("/countries")
    public ResponseEntity<List<CountryEntity>> saveCountries(@RequestBody CountryRequestDTO request) {
        List<CountryEntity> savedCountries = countryService.saveCountries(request.getCantPaises());
        return ResponseEntity.ok(savedCountries);
    }


}