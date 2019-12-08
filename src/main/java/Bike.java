import java.util.Objects;

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
    private int station = 0;

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
    Bike(int bikeId,  int bikeLocation, int stationId, String maintenance, String maintenanceReport) throws ClassNotFoundException {
        this.id = bikeId;
        this.location = bikeLocation;
        this.mnt = (maintenance.equals("y"));
        this.mntReport = maintenanceReport;
        this.moveStation(stationId);
    }

    /**
     * Move a bike to a different (or no) station
     * Also sets station data to match this move
     *
     * @param newStationValue - station ID of the station the bike is moving to
     */
    void moveStation(int newStationValue) throws ClassNotFoundException {
        if (! Objects.equals(this.getStation(),0)) { // check if bike had an old station; '0' represents a bike without a current station
            Station oldStation = ValleyBikeSim.getStationObj(this.station); // get old station object
            oldStation.removeFromBikeList(this); // remove bike from station's bike list
        }

        ValleyBikeSim.updateBikeStationId(this.id, newStationValue);

        if (! Objects.equals(newStationValue, 0)) { // check if new station is a '0,' which is a placeholder station
            Station newStation = ValleyBikeSim.getStationObj(this.station); // get new station object
            ValleyBikeSim.addBikeToStation(this.station, this.id);
            ValleyBikeSim.updateBikeLocation(this.id, 0);
        }

        else {
            ValleyBikeSim.updateBikeLocation(this.id, 2);
        }
    }

    /**
     * All following methods get and set appropriate fields
     * for the bike object
     */

    int getId(){
        return id;
    }

    void setId(int newId){
        this.id = newId;
    }

    int getStation(){
        return this.station;
    }

    void setStation(int newStationValue){
        this.station = newStationValue;
    }

    int getBikeLocation(){
        return this.location;
    }

    void setBikeLocation(int newBikeLocation){
        this.location = newBikeLocation;
    }

    boolean getMnt(){
        return mnt;
    }

    void setMnt(boolean newMnt){
        this.mnt = newMnt;
    }

    String getMntReport(){
        return mntReport;
    }

    void setMntReport(String newMntReport){
        this.mntReport = newMntReport;
    }
}
