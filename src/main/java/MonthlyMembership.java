import java.time.LocalDate;
import java.time.Period;

/**
 * Class representing a monthly ValleyBike membership
 */
public class MonthlyMembership extends Membership{

    //rides that come included in membership each month
    private final int maxRides = 20;

    /**
     * constructor uses abstract class constructor
     */
    MonthlyMembership() {
        super();
    }

    /**
     * checks whether it is time to renew the monthly membership
     * @return true if it has been 30+ days since last payment, false if less
     */
    public boolean checkPaymentDue(){
        LocalDate now = LocalDate.now();
        //get length of time between last payment and now
        Period period = Period.between(now, super.getLastPayment());
        int diff = period.getDays();
        return (diff >= 30);
    }

    MonthlyMembership(int totalRidesLeft, LocalDate lastPayment, LocalDate memberSince){
        super(totalRidesLeft, lastPayment, memberSince);
    }

    //checks whether there are 0 free rides left
    public boolean checkMaxRidesExceeded(){
        return super.getTotalRidesLeft() >= maxRides;
    }

    //returns 2, which represents a monthly membership
    public int getMembershipInt(){
        return 2;
    }

    //returns type of membership as a string
    public String getMembershipString(){
        return "Monthly";
    }

}
