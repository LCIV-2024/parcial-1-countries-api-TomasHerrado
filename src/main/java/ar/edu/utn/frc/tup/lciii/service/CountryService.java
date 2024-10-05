package ar.edu.utn.frc.tup.lciii.service;

import ar.edu.utn.frc.tup.lciii.dtos.common.CountryDTO;
import ar.edu.utn.frc.tup.lciii.entity.CountryEntity;
import ar.edu.utn.frc.tup.lciii.model.Country;
import ar.edu.utn.frc.tup.lciii.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CountryService {

        private final CountryRepository countryRepository;

        private final RestTemplate restTemplate;

        public List<Country> getAllCountries() {
                String url = "https://restcountries.com/v3.1/all";
                List<Map<String, Object>> response = restTemplate.getForObject(url, List.class);
                return response.stream().map(this::mapToCountry).collect(Collectors.toList());
        }

        /**
         * Agregar mapeo de campo cca3 (String)
         * Agregar mapeo campos borders ((List<String>))
         */
        public List<Country> getCountriesByContinent(String continent) {
                List<Country> allCountries = getAllCountries();
                return allCountries.stream()
                        .filter(country -> country.getRegion().equalsIgnoreCase(continent))
                        .collect(Collectors.toList());
        }

        public List<Country> getCountriesByLanguage(String language) {
                List<Country> allCountries = getAllCountries();
                return allCountries.stream()
                        .filter(country -> country.getLanguages() != null &&
                                country.getLanguages().values().stream()
                                        .anyMatch(lang -> lang.equalsIgnoreCase(language)))
                        .collect(Collectors.toList());
        }

        public Country getCountryWithMostBorders() {
                List<Country> allCountries = getAllCountries();
                return allCountries.stream()
                        .filter(country -> country.getBorders() != null)
                        .max((c1, c2) -> Integer.compare(c1.getBorders().size(), c2.getBorders().size()))
                        .orElse(null);
        }


        private Country mapToCountry(Map<String, Object> countryData) {
                Map<String, Object> nameData = (Map<String, Object>) countryData.get("name");
                return Country.builder()
                        .name((String) nameData.get("common"))
                        .population(((Number) countryData.get("population")).longValue())
                        .area(((Number) countryData.get("area")).doubleValue())
                        .region((String) countryData.get("region"))
                        .languages((Map<String, String>) countryData.get("languages"))
                        .build();
        }


        public CountryDTO mapToDTO(Country country) {
                return new CountryDTO(country.getCode(), country.getName());
        }

        public List<CountryEntity> saveCountries(int amount) {
                List<CountryEntity> countries = generateCountries(amount);
                return countryRepository.saveAll(countries);
        }

        private List<CountryEntity> generateCountries(int amount) {
                List<CountryEntity> allCountries = List.of(
                        new CountryEntity("KIR", "Kiribati"),
                        new CountryEntity("ASM", "American Samoa"),
                        new CountryEntity("NZL", "New Zealand")
                        // Agrega más países según sea necesario
                );

                return allCountries.stream()
                        .limit(amount)
                        .collect(Collectors.toList());
        }
}