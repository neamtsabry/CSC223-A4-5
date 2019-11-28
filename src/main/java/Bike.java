public class Bike {
    // bike id
    private int id;

    // Location of bike can be either of the following:
    // docked/available at station --> 0,
    // docked but out of commission --> 1
    // live with customer --> 2
    private int location;

    // which station bike is in
    // if bike not available, station is set to -1
    private int station;

    // whether or not it requires maintenance
    private Boolean mnt;

    // user's report on bike
    private String mntReport;

    /**
     * Constructor for
     * @param bikeId the set integer  id for the bike
     * @param bikeLocation location of the bike between 0 and 2 (explained above for the field)
     * @param stationId the set integer id for the station
     * @param maintenance boolean, true if bike requires maintenance
     * @param maintenanceReport string report explaining why a bike requires maintenance
     */
    public Bike(int bikeId,  int bikeLocation, int stationId, String maintenance, String maintenanceReport){
        this.id = bikeId;
        this.location = bikeLocation;
        this.station = stationId;

        if(maintenance.equals('y')) this.mnt = true;
        else this.mnt = false;

        this.mntReport = maintenanceReport;
    }

    /**
     * Get a comma separated string for bike's data
     *
     * @return comma separated string of bike data
     */
    public String getBikeString() {
        String fileLine = this.id + "," + this.location + "," +
                this.station + "," + this.mnt + "," + this.mntReport;
        return fileLine;
    }

    /**
     * Move a bike to a different (or no) station
     * Also sets station data to match this move
     *
     * @param newStationValue - station ID of the station the bike is moving to
     */
    public void moveStation(int newStationValue) {
        if (this.getStation() != 0) { // check if bike had an old station; '0' represents a bike without a current station
            Station oldStation = ValleyBikeSim.getStationObj(this.station); // get old station object
            oldStation.setBikes(oldStation.getBikes() - 1); // decrement number of bikes in old station
            oldStation.setAvailableDocks(oldStation.getAvailableDocks()+1); // increment available docks at new station
        }
        this.station = newStationValue; // set bike's station to new station
        if (newStationValue != 0) { // check if new station is a '0,' which is a placeholder station
            Station newStation = ValleyBikeSim.getStationObj(this.station); // get new station object
            newStation.setBikes(newStation.getBikes() + 1); // increment number of bikes in new station
            newStation.setAvailableDocks(newStation.getAvailableDocks()-1); // decrement available docks at new station
            setBikeLocation(0);
        }
        else {
            setBikeLocation(2);
        }
    }
    /**
     * All following methods get and set appropriate fields
     * for the bike object
     */

    public int getId(){
        return id;
    }

    public void setId(int newId){
        this.id = newId;
    }

    public int getStation(){
        return this.station;
    }

    public void setStation(int newStationValue){
        this.station = newStationValue;
    }


    public int getBikeLocation(){
        return this.location;
    }

    public void setBikeLocation(int newBikeLocation){
        this.location = newBikeLocation;
    }

    public boolean getMnt(){
        return mnt;
    }

    public void setMnt(boolean newMnt){
        this.mnt = newMnt;
    }

    public String getMntReport(){
        return mntReport;
    }

    public void setMntReport(String newMntReport){
        this.mntReport = newMntReport;
    }
}
