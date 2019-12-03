import java.time.LocalDate;
import java.time.Period;

public class YearlyMembership extends Membership{

    private final int maxRides = 260;

    public YearlyMembership() {
        super();
    }

    public boolean checkPaymentDue(){
        LocalDate now = LocalDate.now();
        Period period = Period.between(now, super.getLastPayment());
        int diff = period.getDays();
        return (diff >= 365);
    }

    public boolean checkMaxRidesExceeded(){
        return super.getTotalRidesLeft() >= maxRides;
    }

    public int getMembershipInt(){
        return 3;
    }

    public String getMembershipString(){
        return "Yearly Membership";
    }
}
