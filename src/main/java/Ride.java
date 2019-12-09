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

    private double payment;

    private int stationTo;

    private int stationFrom;

    public Ride(UUID rideIdVal, int bikeIdVal, String usernameVal,
                Boolean isReturnedVal, Instant startTimeStampVal,
                Instant endTimeStampVal, Integer stationFromVal, Integer stationToVal) throws ParseException {
        this.rideId = rideIdVal;
        this.bikeId = bikeIdVal;
        this.username = usernameVal;
        this.isReturned = isReturnedVal;
        this.startTimeStamp = startTimeStampVal;
        this.endTimeStamp = endTimeStampVal;

        this.stationFrom = stationFromVal;
        this.stationTo = stationToVal;

        if(endTimeStamp != null){
            this.getRideLength();
        }
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
        // if ride still wasn't returned
        if(this.endTimeStamp == null){
            // return duration of ride so far
            this.rideLength = Duration.between(getStartTimeStamp(), Instant.now()).toHours();
        }

        if(this.isReturned){
            this.rideLength = Duration.between(getStartTimeStamp(), getEndTimeStamp()).toHours();
        }

        return this.rideLength;
    }

    public void setRideLength(long newRideLength) {
        this.rideLength = newRideLength;
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

    public int getStationTo(){
        return this.stationTo;
    }

    public void setStationTo(int newStationTo){
        this.stationTo = newStationTo;
    }

    public int getStationFrom() {
        return stationFrom;
    }

    public void setStationFrom(int newStationFrom) {
        this.stationFrom = newStationFrom;
    }

    // checks if it's been 24 hours since user rented bike or not
    public Boolean isRented24Hours() throws ParseException, InterruptedException {
        Instant now =  Instant.now();

        //testing start time stamp
        System.out.println(getStartTimeStamp());

        long between = Duration.between(getStartTimeStamp(), now).toHours();

        if(between >= 24) return true;
        return false;
    }
}
