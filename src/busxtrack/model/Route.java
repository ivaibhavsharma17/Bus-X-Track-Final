package busxtrack.model;

public class Route {
    private int routeId;
    private String routeName;
    private String source;
    private String destination;

    public Route() {
    }

    public Route(int routeId, String routeName, String source, String destination) {
        this.routeId = routeId;
        this.routeName = routeName;
        this.source = source;
        this.destination = destination;
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
