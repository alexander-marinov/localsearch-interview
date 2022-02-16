package alex.marinov.services;

import alex.marinov.models.OpeningHours;
import alex.marinov.models.Place;
import io.specto.hoverfly.junit.core.Hoverfly;
import io.specto.hoverfly.junit5.HoverflyExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Set;

import static io.specto.hoverfly.junit.core.SimulationSource.dsl;
import static io.specto.hoverfly.junit.dsl.HoverflyDsl.service;
import static io.specto.hoverfly.junit.dsl.ResponseCreators.success;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(HoverflyExtension.class)
class PlacesServiceImplTest {

    private static final String API_URL = "http://some.api.com";
    private static final String API_PATH = "/some_path";
    private static final String PLACE_FERLIN_ID = "GXvPAor1ifNfpF0U5PTG0w";
    private static final String PLACE_MARCHE_ID = "ohGSnJtMIC5nPfYRi_HTAg";

    private static String json_casa_ferlin;
    private static String json_marche;

    @BeforeAll
    static void setup() throws IOException {
        json_casa_ferlin = readFile("Casa_Ferlin.json");
        json_marche = readFile("Marche.json");
    }

    static String readFile(String fileName) throws IOException {
        ClassLoader loader = PlacesServiceImplTest.class.getClassLoader();
        InputStream resource = Objects.requireNonNull(loader.getResourceAsStream(fileName));
        return new String(resource.readAllBytes());
    }

    @Test
    void Should_ShowClosedOnWeekend_When_Casa_Ferlin(Hoverfly hoverfly) throws IOException {
        //Given
        PlacesService service = new PlacesServiceImpl(API_URL + API_PATH);
        hoverfly.simulate(dsl(
                service(API_URL)
                        .get(API_PATH + "/" + PLACE_FERLIN_ID)
                        .willReturn(success(json_casa_ferlin, MediaType.APPLICATION_JSON.getType()))
        ));

        //When
        Place place = service.findById(PLACE_FERLIN_ID).get();

        //Then
        OpeningHours firstSlot = place.getOpeningHours().get(0);
        OpeningHours secondSlot = place.getOpeningHours().get(1);
        assertEquals("Casa Ferlin", place.getName());
        assertEquals("Stampfenbachstrasse 38, 8006 Zürich", place.getAddress());
        assertEquals("Monday - Friday", firstSlot.getWeekDays());
        assertEquals(Set.of("11:30 - 14:00", "18:30 - 22:00"), firstSlot.getTimeSlots());
        assertEquals("Saturday - Sunday", secondSlot.getWeekDays());
        assertEquals(Set.of("closed"), secondSlot.getTimeSlots());
    }

    @Test
    void Should_ClosedMondayOpenWeekend_When_Marche(Hoverfly hoverfly) throws IOException {
        //Given
        PlacesService service = new PlacesServiceImpl(API_URL + API_PATH);
        hoverfly.simulate(dsl(
                service(API_URL)
                        .get(API_PATH + "/" + PLACE_MARCHE_ID)
                        .willReturn(success(json_marche, MediaType.APPLICATION_JSON.getType()))
        ));

        //When
        Place place = service.findById(PLACE_MARCHE_ID).get();

        //Then
        OpeningHours mondaySlot = place.getOpeningHours().get(0);
        OpeningHours tfSlot = place.getOpeningHours().get(1);
        OpeningHours satSlot = place.getOpeningHours().get(2);
        OpeningHours sunSlot = place.getOpeningHours().get(3);

        assertEquals("Le Café du Marché", place.getName());
        assertEquals("Rue de Conthey 17, 1950 Sion", place.getAddress());

        assertEquals("Monday", mondaySlot.getWeekDays());
        assertEquals(Set.of("closed"), mondaySlot.getTimeSlots());

        assertEquals("Tuesday - Friday", tfSlot.getWeekDays());
        assertEquals(Set.of("11:30 - 15:00", "18:30 - 00:00"), tfSlot.getTimeSlots());

        assertEquals("Saturday", satSlot.getWeekDays());
        assertEquals(Set.of("18:00 - 00:00"), satSlot.getTimeSlots());

        assertEquals("Sunday", sunSlot.getWeekDays());
        assertEquals(Set.of("11:30 - 15:00"), sunSlot.getTimeSlots());

    }
}
