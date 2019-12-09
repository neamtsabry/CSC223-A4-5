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
        this.station = stationId;
//        this.moveStation(stationId);
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
