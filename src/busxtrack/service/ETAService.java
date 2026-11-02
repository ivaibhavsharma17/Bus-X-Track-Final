package busxtrack.service;

import busxtrack.model.Stop;

import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ETAService {
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public String getStatus(Time departureTime, int lastStopMinutes) {
        LocalTime now = LocalTime.now();
        LocalTime startTime = departureTime.toLocalTime();
        LocalTime endTime = startTime.plusMinutes(lastStopMinutes);

        if (now.isBefore(startTime)) {
            return "NOT STARTED";
        }

        if (now.isAfter(endTime)) {
            return "COMPLETED";
        }

        return "RUNNING";
    }

    public String getEtaText(Time departureTime, int minutesFromStart) {
        LocalTime now = LocalTime.now();
        LocalTime expectedTime = departureTime.toLocalTime().plusMinutes(minutesFromStart);

        if (now.isBefore(expectedTime)) {
            int minutes = getMinuteDifference(now, expectedTime);
            return "Arrives in " + minutes + " minutes (" + expectedTime.format(timeFormatter) + ")";
        }

        LocalTime recentlyPassedTime = expectedTime.plusMinutes(2);
        if (now.equals(expectedTime) || now.isBefore(recentlyPassedTime)) {
            return "Arriving now (" + expectedTime.format(timeFormatter) + ")";
        }

        return "Already passed (" + expectedTime.format(timeFormatter) + ")";
    }

    public String getExpectedTime(Time departureTime, int minutesFromStart) {
        LocalTime expectedTime = departureTime.toLocalTime().plusMinutes(minutesFromStart);
        return expectedTime.format(timeFormatter);
    }

    public Stop findNextStop(Time departureTime, List<Stop> stops) {
        LocalTime now = LocalTime.now();

        for (int i = 0; i < stops.size(); i++) {
            Stop stop = stops.get(i);
            LocalTime expectedTime = departureTime.toLocalTime().plusMinutes(stop.getMinutesFromStart());

            if (now.isBefore(expectedTime) || now.equals(expectedTime)) {
                return stop;
            }
        }

        return null;
    }

    public int getLastStopMinutes(List<Stop> stops) {
        int lastStopMinutes = 0;

        for (int i = 0; i < stops.size(); i++) {
            Stop stop = stops.get(i);
            if (stop.getMinutesFromStart() > lastStopMinutes) {
                lastStopMinutes = stop.getMinutesFromStart();
            }
        }

        return lastStopMinutes;
    }

    private int getMinuteDifference(LocalTime startTime, LocalTime endTime) {
        int startMinutes = startTime.getHour() * 60 + startTime.getMinute();
        int endMinutes = endTime.getHour() * 60 + endTime.getMinute();
        return endMinutes - startMinutes;
    }
}
