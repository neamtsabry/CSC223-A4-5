import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * represents a ride taken in the system
 */
public class Ride {

    // ASSUME ONLY ONE RIDE PER USER AT A TIME

    /** should be random ride id, but consistent */
    private UUID rideId;

    /** bike id */
    private int bikeId;

    /** user name */
    private String username;

    /** true if bike was returned */
    private boolean isReturned;

    /** ride length */
    private long rideLength;

    /** start time rented */
    private Instant startTimeStamp;

    /** end time returned
    * null if isReturned is false */
    private Instant endTimeStamp;

    /** payment made for ride */
    private double payment;

    /** station at which the ride ends and bike is returned */
    private int stationTo;

    /** station at which the ride starts and bike is checked out from */
    private int stationFrom;

    /**
     * constructor initializes ride object
     * @param rideIdVal sets ride id
     * @param bikeIdVal sets bike id that is being ridden
     * @param usernameVal sets user that is taking the ride
     * @param isReturnedVal sets whether bike has been returned
     * @param startTimeStampVal sets start time of ride
     * @param endTimeStampVal sets end time of ride
     * @throws ParseException
     */
    public Ride(UUID rideIdVal, int bikeIdVal, String usernameVal,
                Boolean isReturnedVal, Instant startTimeStampVal,
                Instant endTimeStampVal, Integer stationFromVal, Integer stationToVal) throws ParseException {
        this.rideId = rideIdVal;
        this.bikeId = bikeIdVal;
        this.username = usernameVal;
        this.isReturned = isReturnedVal;
        this.startTimeStamp = startTimeStampVal;
        this.endTimeStamp = endTimeStampVal;
        this.stationTo = stationToVal;
        this.stationFrom = stationFromVal;


        if(endTimeStamp != null){
            this.getRideLength(); //if ride has ended calculate ride length
        }
    }

    /**
     * set ride id
     */
    public void setRideId(UUID rideId) {
        this.rideId = rideId;
    }

    /**
     * returns ride's id
     */
    public UUID getRideId() {
        return this.rideId;
    }

    /**
     * sets the bike id of ride
     */
    public void setBikeId(int bikeId) {
        this.bikeId = bikeId;
    }

    /**
     * returns bike id of ride
     */
    public int getBikeId() { return this.bikeId; }

    /**
     * sets username taking ride
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * returns username of rider
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * set starting time for ride
     */
    public void setStartTimeStamp(Instant startTimeStamp) { this.startTimeStamp = startTimeStamp; }

    /**
     * return starting time for ride
     */
    public Instant getStartTimeStamp() {
        return this.startTimeStamp;
    }

    /**
     * set end time for ride
     */
    public void setEndTimeStamp(Instant endTimeStamp) {
        this.endTimeStamp = endTimeStamp;
    }

    /**
     * returns end time for ride
     */
    public Instant getEndTimeStamp() {
        return this.endTimeStamp;
    }

    /**
     * calculates ride length using start and end times
     * @return ride length
     */
    public long getRideLength() {
        // if ride still wasn't returned
        if(this.endTimeStamp == null){
            // return duration of ride so far
            this.rideLength = Duration.between(getStartTimeStamp(), Instant.now()).toMinutes();
        }

        if(this.isReturned){
            this.rideLength = Duration.between(getStartTimeStamp(), getEndTimeStamp()).toMinutes();
        }

        return this.rideLength;
    }

    /**
     * sets length of ride
     */
    public void setRideLength(long newRideLength) {
        this.rideLength = newRideLength;
    }

    /**
     * sets payment for ride
     */
    public void setPayment(double payment) {
        this.payment = payment;
    }

    /**
     * returns payment for ride
     */
    public double getPayment() {
        return this.payment;
    }

    /**
     * set whether bike has been returned
     */
    public void setIsReturned(boolean newIsReturned) {
        this.isReturned = newIsReturned;
    }

    /**
     * return whether bike has been returned
     */
    public Boolean getIsReturned(){
        return this.isReturned;
    }

    /**
     * return station at which the bike is returned and ride ends
     */
    public int getStationTo(){
        return this.stationTo;
    }

    /**
     * sets the station at which the bike is returned and ride ends
     */
    public void setStationTo(int newStationTo){
        this.stationTo = newStationTo;
    }

    /**
     * return the station at which the bike is checked out and ride starts
     */
    public int getStationFrom() {
        return stationFrom;
    }

    /**
     * set the station at which the bike is checked out and ride starts
     */
    public void setStationFrom(int newStationFrom) {
        this.stationFrom = newStationFrom;
    }

    /**
     * calculates whether bike has been checked out for over 24 hours
     * @return boolean representing whether bike has been out 24+ hours
     */
    public Boolean isRented24Hours() {
        Instant now =  Instant.now();

        //calculating time between start and now
        long between = Duration.between(getStartTimeStamp(), now).toHours();

        if(between >= 24) return true;
        return false;
    }
}
