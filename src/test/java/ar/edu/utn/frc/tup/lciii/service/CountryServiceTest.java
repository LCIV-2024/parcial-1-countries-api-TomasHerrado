package ar.edu.utn.frc.tup.lciii.service;

import ar.edu.utn.frc.tup.lciii.dtos.common.CountryDTO;
import ar.edu.utn.frc.tup.lciii.entity.CountryEntity;
import ar.edu.utn.frc.tup.lciii.model.Country;
import ar.edu.utn.frc.tup.lciii.repository.CountryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CountryServiceTest {
    @InjectMocks
    private CountryService countryService;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllCountries() {
        Map<String, Object> countryData1 = new HashMap<>();
        countryData1.put("name", Map.of("common", "Argentina"));
        countryData1.put("population", 45000000);
        countryData1.put("area", 2780000.0);
        countryData1.put("region", "Americas");

        Map<String, Object> countryData2 = new HashMap<>();
        countryData2.put("name", Map.of("common", "Brazil"));
        countryData2.put("population", 212559417);
        countryData2.put("area", 8515767.0);
        countryData2.put("region", "Americas");

        when(restTemplate.getForObject("https://restcountries.com/v3.1/all", List.class))
                .thenReturn(Arrays.asList(countryData1, countryData2));

        List<Country> countries = countryService.getAllCountries();

        assertEquals(2, countries.size());
        assertEquals("Argentina", countries.get(0).getName());
        assertEquals("Brazil", countries.get(1).getName());
    }

    @Test
    void getCountriesByContinent() {
        when(countryService.getAllCountries()).thenReturn(Arrays.asList(
                Country.builder().region("Americas").name("Argentina").build(),
                Country.builder().region("Europe").name("Germany").build()
        ));

        List<Country> countries = countryService.getCountriesByContinent("Americas");

        assertEquals(1, countries.size());
        assertEquals("Argentina", countries.get(0).getName());
    }

    @Test
    void getCountriesByLanguage() {
        when(countryService.getAllCountries()).thenReturn(Arrays.asList(
                Country.builder().languages(Map.of("spa", "Spanish")).name("Argentina").build(),
                Country.builder().languages(Map.of("eng", "English")).name("USA").build()
        ));

        List<Country> countries = countryService.getCountriesByLanguage("Spanish");

        assertEquals(1, countries.size());
        assertEquals("Argentina", countries.get(0).getName());
    }

    @Test
    void getCountryWithMostBorders() {
        Country country1 = Country.builder().name("CountryA").borders(Arrays.asList("CountryB", "CountryC")).build();
        Country country2 = Country.builder().name("CountryB").borders(Arrays.asList("CountryA")).build();
        Country country3 = Country.builder().name("CountryC").borders(Arrays.asList("CountryA", "CountryB", "CountryD")).build();

        when(countryService.getAllCountries()).thenReturn(Arrays.asList(country1, country2, country3));

        Country result = countryService.getCountryWithMostBorders();

        assertNotNull(result);
        assertEquals("CountryC", result.getName());
    }

    @Test
    void mapToDTO() {
        Country country = Country.builder().code("ARG").name("Argentina").build();
        CountryDTO dto = countryService.mapToDTO(country);

        assertEquals("ARG", dto.getCode());
        assertEquals("Argentina", dto.getName());
    }

    @Test
    void saveCountries() {
        CountryEntity countryEntity = new CountryEntity("KIR", "Kiribati");
        when(countryRepository.saveAll(any())).thenReturn(Arrays.asList(countryEntity));

        List<CountryEntity> savedCountries = countryService.saveCountries(1);

        assertEquals(1, savedCountries.size());
        assertEquals("KIR", savedCountries.get(0).getCode());
        assertEquals("Kiribati", savedCountries.get(0).getName());
    }
}