import java.time.LocalDate;
import java.time.Period;

public class MonthlyMembership extends Membership{

    private final int maxRides = 20;

    public MonthlyMembership() {
        super();
    }

    public boolean checkPaymentDue(){
        LocalDate now = LocalDate.now();
        Period period = Period.between(now, super.getLastPayment());
        int diff = period.getDays();
        return (diff >= 30);
    }

    public boolean checkMaxRidesExceeded(){
        return super.getTotalRidesLeft() >= maxRides;
    }

    public int getMembershipInt(){
        return 2;
    }

    public String getMembershipString(){
        return "Monthly";
    }

}
