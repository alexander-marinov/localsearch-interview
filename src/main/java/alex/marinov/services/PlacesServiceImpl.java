package alex.marinov.services;

import alex.marinov.error.ServiceConnectionException;
import alex.marinov.models.OpeningHours;
import alex.marinov.models.Place;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.*;

@Service
public class PlacesServiceImpl implements PlacesService {

    private final String apiUrl;

    public PlacesServiceImpl(@Value("${api.url}") String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public Optional<Place> findById(String placeId) {

        CloseableHttpClient client = HttpClients.custom().useSystemProperties().build();
        HttpGet httpGet = new HttpGet(apiUrl + "/" + placeId);
        try {
            HttpResponse response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, StandardCharsets.UTF_8.name());

            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNodes = mapper.readTree(responseString);
            Place place = extractPlace(jsonNodes);
            return Optional.of(place);
        } catch (IOException e) {
            throw new ServiceConnectionException(e);
        }
    }

    private Place extractPlace(JsonNode mainNode) {
        String name = mainNode.get("displayed_what").asText();
        String address = mainNode.get("displayed_where").asText();
        JsonNode hoursNode = mainNode.get("opening_hours");
        JsonNode daysNode = hoursNode.get("days");
        String firstDay = DayOfWeek.MONDAY.getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        List<OpeningHours> openingHours = new ArrayList<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            String dayName = day.name().toLowerCase(Locale.ROOT);
            Set<String> currentSlot = getSlotsPerDay(daysNode, dayName);
            Set<String> nextSlot = null;
            if (!DayOfWeek.SUNDAY.equals(day)) {
                String nextDay = day.plus(1).name().toLowerCase(Locale.ROOT);
                nextSlot = getSlotsPerDay(daysNode, nextDay);
            }
            if (!currentSlot.equals(nextSlot)) {
                OpeningHours wrkTime;
                String lastDay = day.getDisplayName(TextStyle.FULL, Locale.ENGLISH);
                if (firstDay.equals(lastDay)) {
                    wrkTime = new OpeningHours(firstDay, currentSlot);
                } else {
                    wrkTime = new OpeningHours(firstDay + " - " + lastDay, currentSlot);
                }
                openingHours.add(wrkTime);
                firstDay = day.plus(1).getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            }
        }
        return new Place(name, address, openingHours);
    }

    private Set<String> getSlotsPerDay(JsonNode daysNode, String weekDay) {
        if (!daysNode.has(weekDay)) {
            return Set.of("closed");
        }
        ArrayNode dayArr = (ArrayNode) daysNode.get(weekDay);
        Set<String> slots = new TreeSet<>();
        for(JsonNode slotsJson: dayArr) {
            String type = slotsJson.get("type").asText();
            if (!"OPEN".equalsIgnoreCase(type)) {
                continue;
            }
            String start = slotsJson.get("start").asText();
            String end = slotsJson.get("end").asText();
            slots.add(start + " - " + end);
        }
        return slots;
    }
}
