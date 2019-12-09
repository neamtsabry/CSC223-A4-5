import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * represents a ride taken in the system
 */
public class Ride {

    // ASSUME ONLY ONE RIDE PER USER AT A TIME

    // should be random ride id, but consistent
    private UUID rideId;

    // bike id
    private int bikeId;

    // user name
    private String username;

    // true if bike was returned
    private boolean isReturned;

    // ride length
    private long rideLength;

    // start time rented
    private Instant startTimeStamp;

    // end time returned
    // null if isReturned is false
    private Instant endTimeStamp;

    //payment made for ride
    private double payment;

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
                Instant endTimeStampVal) throws ParseException {
        this.rideId = rideIdVal;
        this.bikeId = bikeIdVal;
        this.username = usernameVal;
        this.isReturned = isReturnedVal;
        this.startTimeStamp = startTimeStampVal;
        this.endTimeStamp = endTimeStampVal;

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
        if(this.isReturned){
            this.rideLength = Duration.between(getStartTimeStamp(), getEndTimeStamp()).toHours();
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
     * calculates whether bike has been checked out for over 24 hours
     * @return boolean representing whether bike has been out 24+ hours
     * @throws ParseException
     * @throws InterruptedException
     */
    public Boolean isRented24Hours() throws ParseException, InterruptedException {
        Instant now =  Instant.now();

        //testing start time stamp
        System.out.println(getStartTimeStamp());

        //calculating time between start and now
        long between = Duration.between(getStartTimeStamp(), now).toHours();

        if(between >= 24) return true;
        return false;
    }
}
