package alex.marinov.controllers;

import alex.marinov.error.PlaceNotFoundException;
import alex.marinov.models.Place;
import alex.marinov.services.PlacesService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlacesControllerTest {

    @Mock
    private PlacesService service;

    @Test
    void Should_returnPace_When_validId() {
        //Given
        String PLACE_FERLIN_ID = "GXvPAor1ifNfpF0U5PTG0w";
        Place place = new Place("ferlin", "Zurich", null);
        when(service.findById(PLACE_FERLIN_ID)).thenReturn(Optional.of(place));
        //When
        PlacesController controller = new PlacesController(service);
        Place result =  controller.findById(PLACE_FERLIN_ID);
        //Then
        assertEquals(place, result);
    }

    @Test
    void Should_throwException_When_invalidId() {
        //Given
        String PLACE_INVALID_ID = "invalid";
        PlacesController controller = new PlacesController(service);
        when(service.findById(PLACE_INVALID_ID)).thenThrow(PlaceNotFoundException.class);
        //When, Then
        PlaceNotFoundException thrown = assertThrows(
                PlaceNotFoundException.class,
                () -> controller.findById(PLACE_INVALID_ID),
                "Expected findById() to throw, but it didn't"
        );
    }
}
