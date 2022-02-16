package alex.marinov.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Place {

    private final String name;
    private final String address;
    @JsonProperty("opening_hours")
    private final List<OpeningHours> openingHours;

    public Place(String name, String address, List<OpeningHours> openingHours) {
        this.name = name;
        this.address = address;
        this.openingHours = openingHours;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public List<OpeningHours> getOpeningHours() {
        return openingHours;
    }
}
