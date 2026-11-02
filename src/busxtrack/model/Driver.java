package busxtrack.model;

public class Driver {
    private int driverId;
    private String driverName;
    private String phoneNumber;
    private String licenseNumber;

    public Driver() {
    }

    public Driver(int driverId, String driverName, String phoneNumber, String licenseNumber) {
        this.driverId = driverId;
        this.driverName = driverName;
        this.phoneNumber = phoneNumber;
        this.licenseNumber = licenseNumber;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }
}
