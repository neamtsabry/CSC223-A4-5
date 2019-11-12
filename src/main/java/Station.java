/**
 * This is a class that holds station objects
 * for ease of mapping to id
 * <p>
 * It also contains a method to parse station objects back into
 * strings to help write to the station data when desired
 */
public class Station {
    String name;
    int bikes;
    int pedelecs;
    int availableDocks;
    int maintenanceRequest;
    int capacity;
    boolean kioskBoolean;
    int kioskNumber;
    String address;

    public Station(String nameValue, Integer bikesValue,
                   Integer pedelecsValue, Integer availableDocksValue,
                   Integer maintenanceRequestValue,
                   Integer capacityValue,
                   Integer kiosk, String address1) {
        this.name = nameValue;
        this.bikes = bikesValue;
        this.pedelecs = pedelecsValue;
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

    public String getStationString() {
        String fileInput = this.name + "," + this.bikes + ","
                + this.pedelecs + "," + this.availableDocks +
                "," + this.maintenanceRequest + ","
                + this.capacity + "," + this.kioskNumber + "," + this.address;
        return fileInput;
    }

    public String getStationValue() {
        return this.name;
    }

    public void setStationValue(String newName) {
        this.name = newName;
    }

    public int getBikes() {
        return this.bikes;
    }

    public void setBikes(int newNumBikes) {
        this.bikes = newNumBikes;
    }

    public int getPeds() {
        return this.pedelecs;
    }

    public void setPeds(int newNumPeds) {
        this.pedelecs = newNumPeds;
    }

    public int getAvailableDocks() {
        return this.availableDocks;
    }

    public void setAvailableDocks(int newNumAvbDocks) {
        this.availableDocks = newNumAvbDocks;
    }

    public int getMaintenanceRequest() {
        return this.maintenanceRequest;
    }

    public void setMaintenanceRequest(int newNumRqst) {
        this.maintenanceRequest = newNumRqst;
    }

    public int getCapacity() {
        return this.capacity;
    }

    public void setCapacity(int newCap) {
        this.capacity = newCap;
    }

    public boolean getKioskBoolean() {
        return this.kioskBoolean;
    }

    public void setKioskBoolean(boolean newKioskBool) {
        this.kioskBoolean = newKioskBool;
    }

	public int getKioskNum() {
		return this.kioskNumber;
	}

	public void setKioskNum(int newKioskNum) {
		this.kioskNumber = newKioskNum;
	}

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String newAdd) {
        this.address = newAdd;
    }
}