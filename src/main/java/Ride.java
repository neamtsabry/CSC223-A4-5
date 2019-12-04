import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

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

    //TODO ??
    private double payment;

    public Ride(UUID rideIdVal, int bikeIdVal, String usernameVal,
                Boolean isReturnedVal) throws ParseException {
        this.rideId = rideIdVal;
        this.bikeId = bikeIdVal;
        this.username = usernameVal;
        this.isReturned = isReturnedVal;
    }

    public void setRideId(UUID rideId) {
        this.rideId = rideId;
    }

    public UUID getRideId() {
        return this.rideId;
    }

    public void setBikeId(int bikeId) {
        this.bikeId = bikeId;
    }

    public int getBikeId() { return this.bikeId; }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    public void setStartTimeStamp(Instant startTimeStamp) { this.startTimeStamp = startTimeStamp; }

    public Instant getStartTimeStamp() {
        return this.startTimeStamp;
    }

    public void setEndTimeStamp(Instant endTimeStamp) {
        this.endTimeStamp = endTimeStamp;
    }

    public Instant getEndTimeStamp() {
        return this.endTimeStamp;
    }

    public long getRideLength() {
        this.rideLength = Duration.between(getStartTimeStamp(), getEndTimeStamp()).toDays();
        return this.rideLength;
    }

    public void setPayment(double payment) {
        this.payment = payment;
    }

    public double getPayment() {
        return this.payment;
    }

    public void setIsReturned(boolean newIsReturned) {
        this.isReturned = newIsReturned;
    }

    public Boolean getIsReturned(){
        return this.isReturned;
    }

    // checks if it's been 24 hours since user rented bike or not
    public Boolean is24hours() throws ParseException, InterruptedException {
        Instant now =  Instant.now();
        
        //testing start time stamp
        System.out.println(getStartTimeStamp());

        long between = Duration.between(getStartTimeStamp(), now).toHours();

        if(between >= 24) return true;
        return false;
    }
}
