import java.time.LocalDate;

public class PayAsYouGoMembership extends Membership {

    PayAsYouGoMembership() {
        super();
    }

    PayAsYouGoMembership(int totalRidesLeft, LocalDate lastPayment, LocalDate memberSince){
        super(totalRidesLeft, lastPayment, memberSince);
    }

    //returns int which represents PAYG membership
    public int getMembershipInt(){
        return 1;
    }

    //returns string with membership type
    public String getMembershipString(){
        return "Pay Per Ride";
    }
}
