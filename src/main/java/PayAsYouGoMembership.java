import java.time.LocalDate;

public class PayAsYouGoMembership extends Membership {

    PayAsYouGoMembership() {
        super();
    }

    PayAsYouGoMembership(int totalRidesLeft, LocalDate lastPayment, LocalDate memberSince){
        super(totalRidesLeft, lastPayment, memberSince);
    }

    public int getMembershipInt(){
        return 1;
    }

    public String getMembershipString(){
        return "Pay Per Ride";
    }
}
