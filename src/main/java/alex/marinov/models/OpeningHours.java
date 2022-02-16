package alex.marinov.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

public class OpeningHours {

    @JsonProperty("week_days")
    private final String weekDays;

    @JsonProperty("time_slots")
    private final Set<String> timeSlots;

    public OpeningHours(String weekDays, Set<String> timeSlots) {
        this.weekDays = weekDays;
        this.timeSlots = timeSlots;
    }

    public String getWeekDays() {
        return weekDays;
    }

    public Set<String> getTimeSlots() {
        return timeSlots;
    }
}
