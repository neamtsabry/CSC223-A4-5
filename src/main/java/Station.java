public class Station {
    // station name
    private String name;

    // number of bikes in station
    private int bikes;

    // number of available docks
    private int availableDocks;

    // number of maintenance requests
    private int maintenanceRequest;

    // the station capacity (how many bikes can it take?)
    private int capacity;

    // true if station has a kiosk
    private boolean kioskBoolean;

    // number of kiosks if kioskBoolean is true
    private int kioskNumber;

    // address of the station
    private String address;

    /**
     * Constructor to create a new station object
     * @param nameValue station name
     * @param bikesValue number of bikes
     * @param availableDocksValue number of available docks
     * @param maintenanceRequestValue number of maintenance requests
     * @param capacityValue the station capacity
     * @param kiosk number of kiosks
     * @param address1 station address
     */
    public Station(String nameValue, Integer bikesValue,
                   Integer availableDocksValue,
                   Integer maintenanceRequestValue,
                   Integer capacityValue,
                   Integer kiosk, String address1) {
        this.name = nameValue;
        this.bikes = bikesValue;
        this.availableDocks = availableDocksValue;
        this.maintenanceRequest = maintenanceRequestValue;
        this.capacity = capacityValue;
        this.kioskNumber = kiosk;

        if (kiosk > 0) {
            this.kioskBoolean = true;
        } else {
            this.kioskBoolean = false;
        }

        this.address = address1;
    }

    /**
     * Getters and setters for station object values and fields
     */

    public String getStationString() {
        String fileInput = this.name + "," + this.bikes + "," +
                + this.availableDocks +
                "," + this.maintenanceRequest + ","
                + this.capacity + "," + this.kioskNumber + "," + this.address;
        return fileInput;
    }

    public String getStationName() { return this.name; }

    public void setStationName(String newName) { this.name = newName; }

    public int getBikes() { return this.bikes; }

    public void setBikes(int newNumBikes) { this.bikes = newNumBikes; }

    public int getAvailableDocks() { return this.availableDocks; }

    public void setAvailableDocks(int newNumAvbDocks) { this.availableDocks = newNumAvbDocks; }

    public int getMaintenanceRequest() { return this.maintenanceRequest; }

    public void setMaintenanceRequest(int newNumRqst) { this.maintenanceRequest = newNumRqst; }

    public int getCapacity() { return this.capacity; }

    public void setCapacity(int newCap) { this.capacity = newCap; }

    public boolean getKioskBoolean() { return this.kioskBoolean; }

    public void setKioskBoolean(boolean newKioskBool) { this.kioskBoolean = newKioskBool; }

	public int getKioskNum() { return this.kioskNumber; }

	public void setKioskNum(int newKioskNum) { this.kioskNumber = newKioskNum; }

    public String getAddress() { return this.address; }

    public void setAddress(String newAdd) { this.address = newAdd; }
}