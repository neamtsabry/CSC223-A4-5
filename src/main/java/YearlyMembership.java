import java.time.LocalDate;
import java.time.Period;

/**
 * yearly membership extends abstract membership
 * represents a user's yearly membership subscription
 */
public class YearlyMembership extends Membership{

    /** rides that come included in membership each year */
    private final int maxRides = 260;

    /**
     * constructor uses abstract membership's constructor
     */
    public YearlyMembership() {
        super();
    }

    /**
     * This is the constructor for an existing yearly membership
     * @param totalRidesLeft is the total rides left for this membership for a customer
     * @param lastPayment is the last time the customer made a payment for a yearly membership
     * @param memberSince is the date since which a customer has been a yearly membership
     */
    YearlyMembership(int totalRidesLeft, LocalDate lastPayment, LocalDate memberSince){
        super(totalRidesLeft, lastPayment, memberSince);
    }

    /**
     * checks whether it is time to renew the monthly membership
     * @return true if it has been 365+ days since last payment, false if less
     */
    public boolean checkPaymentDue(){
        LocalDate now = LocalDate.now();
        Period period = Period.between(now, super.getLastPayment());
        int diff = period.getDays();
        return (diff >= 365);
    }

    /** checks whether there are 0 free rides left */
    public boolean checkMaxRidesExceeded(){
        return super.getTotalRidesLeft() >= maxRides;
    }

    /** returns 3, which represents a yearly membership */
    public int getMembershipInt(){
        return 3;
    }

    /** returns type of membership as a string */
    public String getMembershipString(){
        return "Yearly";
    }
}
