package ar.edu.utn.frc.tup.lciii.controllers;

import ar.edu.utn.frc.tup.lciii.dtos.common.CountryDTO;
import ar.edu.utn.frc.tup.lciii.dtos.common.CountryRequestDTO;
import ar.edu.utn.frc.tup.lciii.entity.CountryEntity;
import ar.edu.utn.frc.tup.lciii.model.Country;
import ar.edu.utn.frc.tup.lciii.service.CountryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class CountryControllerTest {
    @InjectMocks
    private CountryController countryController;

    @Mock
    private CountryService countryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllCountries() {
        Country country1 = Country.builder()
                .name("Argentina")
                .code("ARG")
                .population(45195777)
                .area(2780400)
                .region("Americas")
                .borders(Arrays.asList("BRA", "CHL"))
                .languages(Map.of("spa", "Spanish"))
                .build();

        Country country2 = Country.builder()
                .name("Brazil")
                .code("BRA")
                .population(213993437)
                .area(8515767)
                .region("Americas")
                .borders(Arrays.asList("ARG", "COL"))
                .languages(Map.of("por", "Portuguese"))
                .build();

        // Simulando el comportamiento del servicio
        when(countryService.getAllCountries()).thenReturn(Arrays.asList(country1, country2));

        // Llamando al método del controlador
        ResponseEntity<List<CountryDTO>> response = countryController.getAllCountries(null, null);

        // Verificando el resultado
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        assertEquals("Argentina", response.getBody().get(0).getName());
        assertEquals("BRA", response.getBody().get(1).getCode());
    }

    @Test
    void getCountriesByContinent() {
        Country country = Country.builder()
                .name("Argentina")
                .code("ARG")
                .population(45195777)
                .area(2780400)
                .region("Americas")
                .borders(Arrays.asList("BRA", "CHL"))
                .languages(Map.of("spa", "Spanish"))
                .build();

        // Simulando el comportamiento del servicio
        when(countryService.getCountriesByContinent("Americas")).thenReturn(Arrays.asList(country));

        // Llamando al método del controlador
        ResponseEntity<List<CountryDTO>> response = countryController.getCountriesByContinent("Americas");

        // Verificando el resultado
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals("Argentina", response.getBody().get(0).getName());
        assertEquals("ARG", response.getBody().get(0).getCode());
    }
    @Test
    void getCountriesByContinent_InvalidContinent() {
        ResponseEntity<List<CountryDTO>> response = countryController.getCountriesByContinent("InvalidContinent");

        assertEquals(400, response.getStatusCodeValue());
    }
    @Test
    void getCountriesByLanguage() {
        Country country = Country.builder()
                .name("Argentina")
                .code("ARG")
                .population(45195777)
                .area(2780400)
                .region("Americas")
                .borders(Arrays.asList("BRA", "CHL"))
                .languages(Map.of("spa", "Spanish"))
                .build();

        // Simulando el comportamiento del servicio
        when(countryService.getCountriesByLanguage("Spanish")).thenReturn(Arrays.asList(country));

        // Llamando al método del controlador
        ResponseEntity<List<CountryDTO>> response = countryController.getCountriesByLanguage("Spanish");

        // Verificando el resultado
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals("Argentina", response.getBody().get(0).getName());
        assertEquals("ARG", response.getBody().get(0).getCode());
    }
    @Test
    void getCountriesByLanguage_InvalidLanguage() {
        ResponseEntity<List<CountryDTO>> response = countryController.getCountriesByLanguage("InvalidLanguage");

        assertEquals(400, response.getStatusCodeValue());
    }
    @Test
    void getCountryWithMostBorders() {
        Country country = Country.builder().code("ARG").name("Argentina").build();
        when(countryService.getCountryWithMostBorders()).thenReturn(country);

        ResponseEntity<CountryDTO> response = countryController.getCountryWithMostBorders();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Argentina", response.getBody().getName());
    }
    @Test
    void getCountryWithMostBorders_NotFound() {
        when(countryService.getCountryWithMostBorders()).thenReturn(null);

        ResponseEntity<CountryDTO> response = countryController.getCountryWithMostBorders();

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void saveCountries() {
        CountryRequestDTO request = new CountryRequestDTO();
        request.setCantPaises(2);
        CountryEntity countryEntity = new CountryEntity("ARG", "Argentina");

        when(countryService.saveCountries(2)).thenReturn(Arrays.asList(countryEntity));

        ResponseEntity<List<CountryEntity>> response = countryController.saveCountries(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals("ARG", response.getBody().get(0).getCode());
    }
}