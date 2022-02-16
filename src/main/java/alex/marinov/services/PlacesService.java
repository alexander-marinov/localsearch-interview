package alex.marinov.services;

import alex.marinov.models.Place;

import java.util.Optional;

public interface PlacesService {

    Optional<Place> findById(String placeId);

}
