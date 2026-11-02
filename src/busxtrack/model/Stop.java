package busxtrack.model;

public class Stop {
    private int stopId;
    private int routeId;
    private String stopName;
    private int stopOrder;
    private int minutesFromStart;

    public Stop() {
    }

    public Stop(int stopId, int routeId, String stopName, int stopOrder, int minutesFromStart) {
        this.stopId = stopId;
        this.routeId = routeId;
        this.stopName = stopName;
        this.stopOrder = stopOrder;
        this.minutesFromStart = minutesFromStart;
    }

    public int getStopId() {
        return stopId;
    }

    public void setStopId(int stopId) {
        this.stopId = stopId;
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    public int getStopOrder() {
        return stopOrder;
    }

    public void setStopOrder(int stopOrder) {
        this.stopOrder = stopOrder;
    }

    public int getMinutesFromStart() {
        return minutesFromStart;
    }

    public void setMinutesFromStart(int minutesFromStart) {
        this.minutesFromStart = minutesFromStart;
    }
}
