import java.time.LocalDate;
import java.time.Period;

public class YearlyMembership extends Membership{

    //rides that come included in membership each year
    private final int maxRides = 260;

    public YearlyMembership() {
        super();
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

    //checks whether there are 0 free rides left
    public boolean checkMaxRidesExceeded(){
        return super.getTotalRidesLeft() >= maxRides;
    }

    //returns 3, which represents a yearly membership
    public int getMembershipInt(){
        return 3;
    }

    //returns type of membership as a string
    public String getMembershipString(){
        return "Yearly";
    }
}
