package busxtrack.model;

public class Bus {
    private int busId;
    private String busNumber;
    private int routeId;
    private int driverId;
    private int capacity;

    public Bus() {
    }

    public Bus(int busId, String busNumber, int routeId, int driverId, int capacity) {
        this.busId = busId;
        this.busNumber = busNumber;
        this.routeId = routeId;
        this.driverId = driverId;
        this.capacity = capacity;
    }

    public int getBusId() {
        return busId;
    }

    public void setBusId(int busId) {
        this.busId = busId;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
