package busxtrack.model;

import java.sql.Time;

public class Timetable {
    private int timetableId;
    private int busId;
    private int routeId;
    private Time departureTime;
    private String tripType;

    public Timetable() {
    }

    public Timetable(int timetableId, int busId, int routeId, Time departureTime, String tripType) {
        this.timetableId = timetableId;
        this.busId = busId;
        this.routeId = routeId;
        this.departureTime = departureTime;
        this.tripType = tripType;
    }

    public int getTimetableId() {
        return timetableId;
    }

    public void setTimetableId(int timetableId) {
        this.timetableId = timetableId;
    }

    public int getBusId() {
        return busId;
    }

    public void setBusId(int busId) {
        this.busId = busId;
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public Time getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Time departureTime) {
        this.departureTime = departureTime;
    }

    public String getTripType() {
        return tripType;
    }

    public void setTripType(String tripType) {
        this.tripType = tripType;
    }
}
