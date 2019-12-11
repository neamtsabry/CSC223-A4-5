import java.util.Objects;
/**
 *  Bike class that represents a bike in the system
 */
public class Bike {
    /** bike id */
    private int id;

    /**
     * Location of bike can be either of the following:
     * docked/available at station --> 0,
     * docked but out of commission --> 1
     * live with customer --> 2
     */
    private int location;

    /**
     * which station bike is in
     * if bike not available, station is set to -1
     */
    private int station = 0;

    /** whether or not it requires maintenance */
    private Boolean mnt;

    /** user's report on bike */
    private String mntReport;

    /**
     * Constructor for
     * @param bikeId the set integer  id for the bike
     * @param bikeLocation location of the bike between 0 and 2 (explained above for the field)
     * @param stationId the set integer id for the station
     * @param maintenance boolean, true if bike requires maintenance
     * @param maintenanceReport string report explaining why a bike requires maintenance
     */
    Bike(int bikeId,  int bikeLocation, int stationId, String maintenance, String maintenanceReport){
        this.id = bikeId;
        this.location = bikeLocation;
        this.mnt = (maintenance.equals("y"));
        this.mntReport = maintenanceReport;
        this.station = stationId;
    }

    /**
     * This is the getter method to access the bike id
     * @return the bike id
     */
    int getId(){
        return id;
    }

    /**
     * This is the getter method to access the station id that the bike is at
     * @return the station id that the bike is at
     */
    int getStation(){
        return this.station;
    }

    /**
     * This is the setter method for the station the bike is at
     * @param newStationValue is the new station id for where the bike is
     */
    void setStation(int newStationValue){
        this.station = newStationValue;
    }

    /**
     * This is the getter method to access the bike location
     * @return the bike location
     */
    int getBikeLocation(){
        return this.location;
    }

    /**
     * This is the setter method for bike location
     * @param newBikeLocation is the new bike location
     */
    void setBikeLocation(int newBikeLocation){
        this.location = newBikeLocation;
    }

    /**
     * This is the getter method to know if the bike requires maintenance
     * @return true if bike requires maintenance and false otherwise
     */
    boolean getMnt(){
        return mnt;
    }

    /**
     * This is the setter method for whether the bike needs maintenance
     * @param newMnt set true if bike needs maintenance and false otherwise
     */
    void setMnt(boolean newMnt){
        this.mnt = newMnt;
    }

    /**
     * This is the getter method to access the message for maintenance
     * @return message written by user for maintenance report
     */
    String getMntReport(){
        return mntReport;
    }

    /**
     * This is the setter method for the message for maintenance
     * @param newMntReport is the message written by user for maintenance report
     */
    void setMntReport(String newMntReport){
        this.mntReport = newMntReport;
    }
}
