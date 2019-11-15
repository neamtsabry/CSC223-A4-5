public class Bike {
    // bike id
    int id;

    // whether or not it requires maintenance
    Boolean mnt;

    // user's report on bike
    String mntReport;

    // Location of bike can be either of the following:
    // docked/available at station --> 0,
    // live with customer --> 1,
    // docked but out of commission --> 2
    int location;

    // which station bike is in
    // if bike not available, station is set to -1
    int station;

    public Bike(int bikeId,  int bikeLocation, int stationId, String maintenance, String maintenanceReport){
        this.id = bikeId;
        if(maintenance.equals('y')){
            this.mnt = true;
        }

        this.mntReport = maintenanceReport;
        this.location = bikeLocation;

        if(location == 0 || location == 1 || location == 2){
            this.station = stationId;
        } else {
            this.station = -1;
        }

    }

    public int getId(){
        return id;
    }

    public void setId(int newId){
        this.id = newId;
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
