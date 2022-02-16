package alex.marinov.controllers;

import alex.marinov.error.PlaceNotFoundException;
import alex.marinov.models.Place;
import alex.marinov.services.PlacesService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class PlacesController {

    private PlacesService service;

    public PlacesController(PlacesService service) {
        this.service = service;
    }

    @GetMapping("/places/{place_id}")
    public Place findById(
            @PathVariable("place_id") String placeId) {
        return service.findById(placeId)
                .orElseThrow(() -> new PlaceNotFoundException(placeId));
    }

}
