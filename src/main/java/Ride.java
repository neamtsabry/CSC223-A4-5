import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    private String startTimeStamp;

    // end time returned
    // null if isReturned is false
    private String endTimeStamp;

    //TODO ??
    private String payment;

    public Ride(UUID rideIdVal, int bikeIdVal, String usernameVal,
                Boolean isReturnedVal) throws ParseException {
        this.rideId = rideIdVal;
        this.bikeId = bikeIdVal;
        this.username = usernameVal;
        this.isReturned = isReturnedVal;

        if(isReturnedVal){
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            Date date1 = format.parse(startTimeStamp);
            Date date2 = format.parse(endTimeStamp);
            this.rideLength = date2.getTime() - date1.getTime();
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

    public int getBikeId() {
        return this.bikeId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    public void setStartTimeStamp(String startTimeStamp) {
        this.startTimeStamp = startTimeStamp;
    }

    public String getStartTimeStamp() {
        return this.startTimeStamp;
    }

    public void setEndTimeStamp(String endTimeStamp) {
        this.endTimeStamp = endTimeStamp;
    }

    public String getEndTimeStamp() {
        return this.endTimeStamp;
    }

    public void setRideLength(long rideLength) {
        this.rideLength = rideLength;
    }

    public long getRideLength() {
        return this.rideLength;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getPayment() {
        return this.payment;
    }

    public void setIsReturned(boolean newIsReturned) {
        this.isReturned = newIsReturned;
    }

    public Boolean getIsReturned(){
        return this.isReturned;
    }
}
