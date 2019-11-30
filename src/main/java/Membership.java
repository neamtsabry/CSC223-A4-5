import java.time.LocalDate;

public abstract class Membership {

    private int totalRidesTaken;

    private LocalDate lastPayment;

    public Membership() {
        this.totalRidesTaken = 0;
    }

    public int getTotalRidesTaken() {
        return totalRidesTaken;
    }

    public void setTotalRidesTaken(int totalRidesTaken) {
        this.totalRidesTaken = totalRidesTaken;
    }

    public LocalDate getLastPayment() {
        return lastPayment;
    }

    public void setLastPayment(LocalDate lastPayment) {
        this.lastPayment = lastPayment;
    }

    public int getMembershipInt(){
        return 0;
    }

}
